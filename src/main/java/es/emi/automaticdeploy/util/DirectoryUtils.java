package es.emi.automaticdeploy.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DirectoryUtils {

    public static List<Path> getAllFiles(String directoryPath) throws IOException {

        List<Path> files = new ArrayList<>();
        Path dirPath = Paths.get(directoryPath);

        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            throw new IllegalArgumentException("The provided path is not a directory or does not exist.");
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    files.add(entry);
                }
            }
        }

        return files;
    }

    public static List<Path> getAllDirectories(String directoryPath) throws IOException {

        List<Path> directories = new ArrayList<>();
        Path dirPath = Paths.get(directoryPath);

        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            throw new IllegalArgumentException("The provided path is not a directory or does not exist.");
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    directories.add(entry);
                }
            }
        }

        return directories;
    }
}