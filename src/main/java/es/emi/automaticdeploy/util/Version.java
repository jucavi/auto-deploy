package es.emi.automaticdeploy.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version> {

    private static final String VERSION_REGEXP = "^\\d{1,2}\\.\\d{1,2}(\\.\\d{1,2})?(\\.\\d{1,2})?$";
    private static final Pattern VERSION_PATTERN = Pattern.compile(VERSION_REGEXP);

    private int major;
    private int minor;
    private int patch;
    private int build;

    public Version(String version) {
        if (!isValidVersion(version.trim())) {
            throw new IllegalArgumentException("Invalid version format");
        }
        parseVersion(version.trim());
    }

    private boolean isValidVersion(String version) {
        Matcher matcher = VERSION_PATTERN.matcher(version);
        return matcher.matches();
    }

    private void parseVersion(String version) {
        String[] parts = version.split("\\.");
        major = Integer.parseInt(parts[0]);
        minor = Integer.parseInt(parts[1]);
        patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        build = parts.length > 3 ? Integer.parseInt(parts[3]) : 0;
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
