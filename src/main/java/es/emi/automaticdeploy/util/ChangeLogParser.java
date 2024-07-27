package es.emi.automaticdeploy.util;

import es.emi.automaticdeploy.constant.ExecType;
import es.emi.automaticdeploy.entity.ChangeLog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangeLogParser {

    private static final String DESCRIPTION = "sql";
    private static final String LIQUIBASE = "4.28.0";
    private static final String LABELS = "legacy_migration";
    private static final ExecType EXECTYPE = ExecType.EXECUTED;
    private static final String DEPLOYMENT_ID = "1394853616";

    public static List<ChangeLog> parseChangeLog(String basePath, String filePath, Long orderExecuted) throws IOException {

        Path absolutePath = Paths.get(filePath);
        Path base = Paths.get(basePath);

        List<String> lines = Files.readAllLines(absolutePath);
        List<ChangeLog> changeLogs = new ArrayList<>();

        String id;
        String author;
        String filename = base.relativize(absolutePath).toString();

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("--changeset")) {

                Pattern pattern = Pattern.compile("([^:\\s]+):([^:\\s]+)");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String key = matcher.group(1).trim().toLowerCase();
                    String value = matcher.group(2).trim();

                    if (key.equals("ignore") && value.equals("true")) {
                        continue;
                    }

                    id = null;
                    author = null;
                    LocalDateTime dateTime = LocalDateTime.now();
                    String labels = LABELS;

                    if (key.equals("label")) {
                        labels = value;
                    } else {
                        author = key;
                        id = value;
                    }

                    changeLogs.add(
                            new ChangeLog(
                                    id,
                                    author,
                                    filename,
                                    dateTime,
                                    orderExecuted,
                                    EXECTYPE,
                                    null,
                                    DESCRIPTION,
                                    "",
                                    null,
                                    LIQUIBASE,
                                    null,
                                    labels,
                                    DEPLOYMENT_ID)
                    );

                    orderExecuted++;
                }
            }
        }

        return changeLogs;
    }
}
