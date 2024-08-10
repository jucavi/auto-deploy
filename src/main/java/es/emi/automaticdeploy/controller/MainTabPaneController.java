package es.emi.automaticdeploy.controller;

import es.emi.automaticdeploy.constant.DataSource;
import es.emi.automaticdeploy.entity.ChangeLog;
import es.emi.automaticdeploy.entity.ChangeLogLock;
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
    public Tab tabDeploy;
    @FXML
    public Label labelMigrationStatus;
    @FXML
    public Tab tabVersioning;
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
                tabVersioning.setDisable(false);
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

        tabDeploy.setDisable(!(cbxVersion.isDisable() && checkAnyCheckboxSelected()));
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
        Version version = null;

        try {
            version = Version.parse(cbxVersion.getValue());

        } catch (Exception e) {
            labelConnectionStatus.setText(e.getMessage());
            return;
        }

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


            // Database associated with war folder
            dbp.setDatabase("changeset_test");
            // if not database return;

            try {

                SessionFactory sessionFactory = HibernateUtils.getSessionFactory(dbp);
                Session session = sessionFactory.openSession();
                Transaction transaction = session.beginTransaction();

                List<Path> folderContent = DirectoryUtils.getAllDirectories(changesetPath.toString());
                // SORT BY VERSION NEEDED!!!
                List<Path> versionFolders = folderContent.stream()
                        .filter(f -> Version.isValidVersion(f.getFileName().toString().trim()))
                        .sorted((f1, f2) -> {
                            Version v1 = Version.parse(f1.toString().trim());
                            Version v2 = Version.parse(f2.toString().trim());

                            return v1.compareTo(v2);
                        })
                        .toList();

                // order executed counter
                long orderExecuted = 1L;

                for (Path versionFolder : versionFolders) {
                    Version currentVersion = null;

                    try {
                        currentVersion = Version.parse(versionFolder.getFileName().toString());

                    } catch (Exception e)  {
                        continue;
                    }

                    if (currentVersion.compareTo(version) > 0) {
                        continue;
                    }

                    PrefixFilenameComparator prefixComparator = new PrefixFilenameComparator();
                    List<Path> files = DirectoryUtils.getAllFiles(versionFolder.toString());

                    // SORT BY NAME NEEDED!!!
                    files.sort(prefixComparator);

                    session.save(new ChangeLogLock(1L, false, null, null));

                    for (Path file : files) {

                        try {
                            List<ChangeLog> changeLogs = ChangeLogParser.parseChangeLog(basePath.toString(), file.toString(), orderExecuted);

                            for (ChangeLog changeLog : changeLogs) {
                                session.save(changeLog);
                            }

                            orderExecuted += changeLogs.size();

                            tabDeploy.setDisable(false);

                        } catch (Exception e) {
                            labelMigrationStatus.setText(e.getMessage());
                        }
                    }
                }

                transaction.commit();
                session.close();
                showAlert("Success", "ChangeLog tables created and populated successfully.");

                btnSetupMigration.setDisable(true);
            } catch (Exception e) {
                labelMigrationStatus.setText(e.getMessage());
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
