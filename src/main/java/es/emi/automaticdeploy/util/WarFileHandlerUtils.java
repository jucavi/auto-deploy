package es.emi.automaticdeploy.util;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class WarFileHandlerUtils {

    public static Path unpack(String warFilePath, String tempDirectory) throws IOException {

        Path tempDir = Files.createTempDirectory(tempDirectory);
        System.out.println("Temporary directory created at: " + tempDir);

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(Paths.get(warFilePath)))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path entryPath = tempDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(zis, entryPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
        System.out.println("Unpacked " + warFilePath + " to " + tempDir);

        return tempDir;
    }

    public static void pack(String outputWarFilePath, String tempDirectory, boolean deleteDirectory) throws IOException {

        Path tempDir = Paths.get(tempDirectory);

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(Paths.get(outputWarFilePath)))) {

            Files.walkFileTree(tempDir, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                    zos.putNextEntry(new ZipEntry(tempDir.relativize(file).toString()));

                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

                    if (!tempDir.equals(dir)) {

                        zos.putNextEntry(new ZipEntry(tempDir.relativize(dir) + "/"));
                        zos.closeEntry();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }

        if (deleteDirectory) {

            FileUtils.deleteDirectory(tempDir.toFile());
            System.out.println("Temporary directory deleted at: " + tempDir);

        } else {
            System.out.println("Temporary directory retained at: " + tempDir);
        }
    }

}

