package es.emi.automaticdeploy.util;

public class URLUtils {

    // Method to concatenate URL parts
    public static String concatenate(String baseUrl, String... paths) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl);

        for (String path : paths) {
            if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
                urlBuilder.append('/');

            } else if (baseUrl.endsWith("/") && path.startsWith("/")) {
                path = path.substring(1);
            }
            baseUrl = urlBuilder.append(path).toString();
        }

        return baseUrl;
    }
}
