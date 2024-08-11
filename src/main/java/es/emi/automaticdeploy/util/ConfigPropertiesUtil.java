package es.emi.automaticdeploy.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.Properties;

public class ConfigPropertiesUtil {

    private static final Properties properties = new Properties();

    public static String getProperty(String propertyName) {

        try (InputStream input = ConfigPropertiesUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new InvalidParameterException();
            }

            properties.load(input);

        } catch (IOException | InvalidParameterException ex) {
            throw new InvalidParameterException("Sorry, unable to find config.properties");
        }

        return properties.getProperty(propertyName);
    }
}

