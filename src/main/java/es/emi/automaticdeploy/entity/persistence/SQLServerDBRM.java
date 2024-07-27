package es.emi.automaticdeploy.entity.persistence;

import es.emi.automaticdeploy.util.URLUtils;

public class SQLServerDBRM extends DatabaseProperties {

    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String DIALECT = "org.hibernate.dialect.SQLServerDialect";

    /**
     * Constructor
     *
     * @param builder builder
     */
    private SQLServerDBRM(SQLServerDBRMBuilder builder) {
        super(builder);
    }


    /**
     * Builder
     */
    public static class SQLServerDBRMBuilder extends DatabasePropertiesBuilder {

        @Override
        public DatabaseProperties build() {
            return new SQLServerDBRM(this);
        }
    }


    @Override
    public String getUrl() {
        String PREFIX = "jdbc:sqlserver://";
        setDatabase(getDatabase() == null ? ";databaseName=master" : ";databaseName=" + getDatabase());

        return URLUtils.concatenate(PREFIX, getBaseUrl(), getDatabase());
    }

    @Override
    public String getDriver() {
        return DRIVER;
    }

    @Override
    public String getDialect() {
        return DIALECT;
    }
}
