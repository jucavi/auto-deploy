package es.emi.automaticdeploy.controller;

import es.emi.automaticdeploy.constant.DataSource;
import es.emi.automaticdeploy.constant.ExecType;
import es.emi.automaticdeploy.entity.ChangeLog;
import es.emi.automaticdeploy.entity.persistence.DatabaseProperties;
import es.emi.automaticdeploy.entity.persistence.MySQLDBRM;
import es.emi.automaticdeploy.entity.persistence.PostgreSQLDBRM;
import es.emi.automaticdeploy.entity.persistence.SQLServerDBRM;
import es.emi.automaticdeploy.util.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class MainTabPaneController implements Initializable {

    @FXML
    public Button btnTestConnection;
    @FXML
    public Button btnResetDatasource;
    @FXML
    public Button btnChooseDir;
    @FXML
    public Label labelDirectoryPath;
    @FXML
    public AnchorPane paneWarFiles;
    @FXML
    public ComboBox<String> cbxVersion;
    @FXML
    public Button btnSetupMigration;
    @FXML
    public CheckBox chbxNewDeploy;
    @FXML
    public VBox vboxWarFiles;
    @FXML
    public ScrollPane scrPaneWarFiles;
    @FXML
    public ComboBox<DataSource> cbxDatabaseSource;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab tabDatasource;
    @FXML
    private TextField textFieldDatabaseUrl;
    @FXML
    private TextField textFieldUsername;
    @FXML
    private PasswordField textFieldPassword;
    @FXML
    private Label labelConnectionStatus;

    private final Color success = Color.GREEN;
    private final Color error = Color.RED;

    private final Collection<String> VERSIONS = Arrays.asList(
            "23.12.1.2",
            "24.4.2.0",
            "24.5.1.0",
            "24.6.1.0",
            "24.6.1.2",
            "24.7.1.0"
    );

    private DatabaseProperties dbp;

    private final String TEMP_DIR = "TEM_DIR";
    private final String BASE_PATH = "WEB-INF/classes";
    private final String CHANGESET_DIR_RELATIVE_PATH = "db/changelog/scripts";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initDataSourceComboBox();
        initVersionComboBox();

    }

    @FXML
    public void testConnection() {

        dbp = getDatabaseProperties();

        try (Connection connection =
                     DriverManager.getConnection(
                             dbp.getUrl(),
                             dbp.getUser(),
                             dbp.getPassword())) {

            if (connection != null) {
                labelConnectionStatus.setTextFill(success);
                labelConnectionStatus.setText("Connection successful!\n");
            }
        } catch (Exception e) {
            labelConnectionStatus.setTextFill(error);
            labelConnectionStatus.setText("Connection failed: " + e.getMessage() + "\n");
        }
    }

    @FXML
    public void resetDatasource(Event event) {
        initDataSourceComboBox();
        cleanDatasourceUrl();
        cleanDatasourceUser();
        cleanDatasourcePassword();
    }

    @FXML
    public void chooseDir(ActionEvent actionEvent) throws IOException {

        String windowTitle = "Select WAR directory";
        Path warDirectory = getDirectoryPath(labelDirectoryPath, windowTitle);

        if (warDirectory != null) {
            List<Path> warFiles = WarFileReaderUtils.readWarFiles(warDirectory.toString());

            scrPaneWarFiles.setDisable(false);
            vboxWarFiles.setSpacing(10);
            vboxWarFiles.setPadding(new Insets(10, 10, 10, 10));

            for (Path warFile : warFiles) {
                CheckBox checkBox = new CheckBox(warFile.getFileName().toString());
                vboxWarFiles.getChildren().add(checkBox);

                checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    btnSetupMigration.setDisable(
                            checkAnyCheckboxSelected() || chbxNewDeploy.isSelected());
                });
            }

        } else {
            scrPaneWarFiles.setDisable(true);
        }
    }

    @FXML
    public void disableVersionCbx(ActionEvent actionEvent) {
        cbxVersion.setDisable(chbxNewDeploy.isSelected());

        btnSetupMigration.setDisable(
                checkAnyCheckboxSelected() || chbxNewDeploy.isSelected());
    }

    private boolean checkAnyCheckboxSelected() {
        return vboxWarFiles.getChildren()
                .stream()
                .filter(node -> node instanceof CheckBox)
                .map(node -> (CheckBox) node)
                .noneMatch(CheckBox::isSelected);
    }

    @FXML
    public void setupMigration(ActionEvent actionEvent) throws IOException {

        String warFolderPath = labelDirectoryPath.getText();
        Version currentVersion = new Version(cbxVersion.getValue());

        List<String> warsSelected = vboxWarFiles.getChildren()
                .stream()
                .filter(node -> node instanceof CheckBox)
                .map(node -> (CheckBox) node)
                .filter(CheckBox::isSelected)
                .map(Labeled::getText)
                .toList();

        for (String war : warsSelected) {
            Path absolute = Paths.get(warFolderPath, war);
            Path tempPath = WarFileHandlerUtils.unpack(absolute.toString(), TEMP_DIR);
            Path basePath = Paths.get(tempPath.toString(), BASE_PATH);
            Path changesetPath = Paths.get(basePath.toString(), CHANGESET_DIR_RELATIVE_PATH);

            List<Path> versionFolders = DirectoryUtils.getAllDirectories(changesetPath.toString());

            for (Path versionFolder : versionFolders) {
                Version version = new Version(versionFolder.getFileName().toString());

                if (version.compareTo(currentVersion) > 0) {
                    continue;
                }

                dbp.setDatabase("changeset_test");

                List<Path> files = DirectoryUtils.getAllFiles(versionFolder.toString());

                try (Connection connection =
                             DriverManager.getConnection(
                                     dbp.getUrl(),
                                     dbp.getUser(),
                                     dbp.getPassword())) {

                    createChangeLogTable(connection);

                    for (Path file : files) {
                        List<ChangeLog> changelogs = ChangeLogParser.parseChangeLog(basePath.toString(), file.toString(), 1L);

                        for (ChangeLog cl : changelogs) {
                            insertIntoChangeLogTable(connection, cl);
                        }
                    }

                }  catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private DatabaseProperties getDatabaseProperties() {

        DataSource dbType = cbxDatabaseSource.getValue();
        String url = textFieldDatabaseUrl.getText();
        String user = textFieldUsername.getText();
        String password = textFieldPassword.getText();

        DatabaseProperties db = null;

        switch (dbType.toString()) {
            case "MySQL" -> db = new MySQLDBRM.MySQLDBRMBuilder()
                    .setBaseUrl(url)
                    .setUser(user)
                    .setPassword(password)
                    .build();

            case "PostgreSQL" -> db = new PostgreSQLDBRM.PostgreSQLDBRMBuilder()
                    .setBaseUrl(url)
                    .setUser(user)
                    .setPassword(password)
                    .build();

            case "SQLServer" -> db = new SQLServerDBRM.SQLServerDBRMBuilder()
                    .setBaseUrl(url)
                    .setUser(user)
                    .setPassword(password)
                    .build();

            default -> labelConnectionStatus.setText("Unsupported DB type.\n");
        }

        return db;
    }

    private void initDataSourceComboBox() {
        cbxDatabaseSource.getItems().setAll(DataSource.values());
        cbxDatabaseSource.getSelectionModel().selectFirst();
    }

    private void initVersionComboBox() {
        cbxVersion.getItems().setAll(VERSIONS);
        cbxVersion.getSelectionModel().selectFirst();
    }

    private void cleanDatasourceUrl() {
        textFieldDatabaseUrl.setText("");
    }

    private void cleanDatasourceUser() {
        textFieldUsername.setText("");
    }

    private void cleanDatasourcePassword() {
        textFieldPassword.setText("");
    }

    private Path getDirectoryPath(Label infoLabel, String windowTitle) {

        File file = chooseDirectory(infoLabel, windowTitle);

        if (file != null && file.isDirectory()) {

            infoLabel.setText(file.getAbsolutePath());
            return file.toPath();

        } else {
            infoLabel.setText("Folder required!");
        }

        return null;
    }

    private File chooseDirectory(Label lbl, String windowTitle) {

        Stage stage = (Stage) lbl.getScene().getWindow();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        directoryChooser.setTitle(windowTitle);

        return directoryChooser.showDialog(stage);
    }

    private void createChangeLogTable(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS databasechangelog (" +
                "ID VARCHAR(255) NOT NULL, " +
                "AUTHOR VARCHAR(255) NOT NULL, " +
                "FILENAME VARCHAR(255) NOT NULL, " +
                "DATEEXECUTED TIMESTAMP NOT NULL, " +
                "ORDEREXECUTED BIGINT NOT NULL, " +
                "EXECTYPE VARCHAR(255) NOT NULL, " +
                "MD5SUM VARCHAR(255), " +
                "DESCRIPTION VARCHAR(255), " +
                "COMMENTS VARCHAR(255), " +
                "TAG VARCHAR(255), " +
                "LIQUIBASE VARCHAR(255), " +
                "CONTEXTS VARCHAR(255), " +
                "LABELS VARCHAR(255), " +
                "DEPLOYMENT_ID VARCHAR(255))";
        try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
            preparedStatement.executeUpdate();
        }
    }


    private void insertIntoChangeLogTable(Connection connection, ChangeLog changeLog) throws SQLException {
        String insertSQL = "INSERT INTO databasechangelog (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, EXECTYPE, MD5SUM, DESCRIPTION, COMMENTS, TAG, LIQUIBASE, CONTEXTS, LABELS, DEPLOYMENT_ID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, changeLog.getId());
            preparedStatement.setString(2, changeLog.getAuthor());
            preparedStatement.setString(3, changeLog.getFilename());
            preparedStatement.setTimestamp(4, java.sql.Timestamp.valueOf(changeLog.getDateTime()));
            preparedStatement.setLong(5, changeLog.getOrderExecuted());
            preparedStatement.setString(6, ExecType.EXECUTED.name());
            preparedStatement.setString(7, changeLog.getMd5Sum());
            preparedStatement.setString(8, changeLog.getDescription());
            preparedStatement.setString(9, changeLog.getComments());
            preparedStatement.setString(10, changeLog.getTag());
            preparedStatement.setString(11, changeLog.getLiquibase());
            preparedStatement.setString(12, changeLog.getContexts());
            preparedStatement.setString(13, changeLog.getLabels());
            preparedStatement.setString(14, changeLog.getDeploymentId());
            preparedStatement.executeUpdate();
        }
    }

}
