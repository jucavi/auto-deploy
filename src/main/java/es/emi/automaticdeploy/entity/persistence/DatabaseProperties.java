package es.emi.automaticdeploy.entity.persistence;

public abstract class DatabaseProperties {

    private static final String DRIVER = "";
    private static final String DIALECT = "";

    private String baseUrl;
    private String database;
    private String user;
    private String password;

    public DatabaseProperties(DatabasePropertiesBuilder builder) {
        this.baseUrl = builder.baseUrl;
        this.database = builder.database;
        this.user = builder.user;
        this.password = builder.password;
    }


    public abstract static class DatabasePropertiesBuilder {

        // required parameters
        private String baseUrl;
        private String database;
        private String user;
        private String password;


        public DatabasePropertiesBuilder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public DatabasePropertiesBuilder setDatabase(String database) {
            this.database = database;
            return this;
        }

        public DatabasePropertiesBuilder setUser(String user) {
            this.user = user;
            return this;
        }

        public DatabasePropertiesBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public abstract DatabaseProperties build();
    }

    public abstract String getUrl();
    public abstract String getDriver();
    public abstract String getDialect();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
