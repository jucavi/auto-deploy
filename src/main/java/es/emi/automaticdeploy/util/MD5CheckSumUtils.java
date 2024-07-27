package es.emi.automaticdeploy.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5CheckSumUtils {

    /**
     * Calculates and return a MD5Sum of a file
     *
     * @param filePath file path
     * @return MD5Sum of a file
     * @throws IOException              if an IO error occurs
     * @throws NoSuchAlgorithmException if MD5 cryptographic algorithm not found
     */
    public static String getMD5Checksum(Path filePath) throws IOException, NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("MD5");

        // Read the file as stream of bytes
        try (FileInputStream fis = new FileInputStream(filePath.toString())) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            // Update the MessageDigest with the bytes from the file
            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
        }

        // Get the hash's bytes
        byte[] mdBytes = md.digest();

        // Convert the byte array into a hexadecimal string
        StringBuilder sb = new StringBuilder();
        for (byte b : mdBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    /**
     * Calculates and return a MD5Sum of a text
     *
     * @param text text
     * @return MD5Sum of a file
     * @throws NoSuchAlgorithmException if MD5 cryptographic algorithm not found
     */
    private static String getMD5Checksum(String text) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(text.trim().replace(" ", "").getBytes());
        byte[] digest = md.digest();

        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Checks if the MD5 checksum of a file matches a given checksum
     */
    public static boolean checkMD5Checksum(Path filePath, String expectedMD5)
            throws IOException, NoSuchAlgorithmException {

        String fileMD5 = getMD5Checksum(filePath);
        return fileMD5.equalsIgnoreCase(expectedMD5);
    }

    /**
     * Checks if the MD5 checksum of a text matches a given checksum
     */
    public static boolean checkMD5Checksum(String text, String expectedMD5)
            throws IOException, NoSuchAlgorithmException {

        String fileMD5 = getMD5Checksum(text);
        return fileMD5.equalsIgnoreCase(expectedMD5);
    }
}

