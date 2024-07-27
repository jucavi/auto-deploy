package es.emi.automaticdeploy.util;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class WarFileReaderUtils {

    public static List<Path> readWarFiles(String directoryPath) throws IOException {

        List<Path> warFiles = new ArrayList<>();
        Path dirPath = Paths.get(directoryPath);

        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            throw new IllegalArgumentException("The provided path is not a directory or does not exist.");
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.war")) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    warFiles.add(entry);
                }
            }
        }

        return warFiles;
    }
}

