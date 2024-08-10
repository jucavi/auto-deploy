package es.emi.automaticdeploy.util;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrefixFilenameComparator implements Comparator<Path> {

    private final Pattern PATTERN = Pattern.compile("^\\d+");

    @Override
    public int compare(Path path1, Path path2) {

        String filename1 = path1.getFileName().toString().trim();
        String filename2 = path2.getFileName().toString().trim();

        Integer num1 = getPrefix(filename1);
        Integer num2 = getPrefix(filename2);

        if (num1 != null && num2 != null) {
            return num1.compareTo(num2);
        }

        // only one path has a numeric prefix
        // it should come first
        if (num1 != null) {
            return -1;
        }
        if (num2 != null) {
            return 1;
        }

        // else compare them alphabetically
        return path1.compareTo(path2);
    }

    private Integer getPrefix(String path) {

        Matcher matcher = PATTERN.matcher(path);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        } else {
            return null;
        }
    }
}