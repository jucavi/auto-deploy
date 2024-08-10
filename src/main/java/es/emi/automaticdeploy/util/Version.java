package es.emi.automaticdeploy.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version> {

    private static final String VERSION_REGEXP = "^\\d{1,2}\\.\\d{1,2}(\\.\\d{1,2})?(\\.\\d{1,2})?$";
    private static final Pattern VERSION_PATTERN = Pattern.compile(VERSION_REGEXP);

    private final int major;
    private final int minor;
    private final int patch;
    private final int build;

    public Version(int major, int minor, int patch, int build) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.build = build;
    }

    public static boolean isValidVersion(String version) {
        Matcher matcher = VERSION_PATTERN.matcher(version);
        return matcher.matches();
    }

    public static Version parse(String version) throws NumberFormatException{

        String[] parts = version.trim().split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        int build = parts.length > 3 ? Integer.parseInt(parts[3]) : 0;

        return new Version(major, minor, patch, build);
    }

    @Override
    public int compareTo(Version other) {

        if (this.major != other.major) {
            return Integer.compare(this.major, other.major);
        }

        if (this.minor != other.minor) {
            return Integer.compare(this.minor, other.minor);
        }

        if (this.patch != other.patch) {
            return Integer.compare(this.patch, other.patch);
        }

        return Integer.compare(this.build, other.build);
    }
}
