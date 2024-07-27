package es.emi.automaticdeploy.entity.persistence;

import es.emi.automaticdeploy.util.URLUtils;

public class PostgreSQLDBRM extends DatabaseProperties {

    private static final String DRIVER = "org.postgresql.Driver";
    private static final String DIALECT = "org.hibernate.dialect.PostgreSQLDialect";

    /**
     * Constructor
     *
     * @param builder builder
     */
    private PostgreSQLDBRM(PostgreSQLDBRMBuilder builder) {
        super(builder);
    }


    /**
     * Builder
     */
    public static class PostgreSQLDBRMBuilder extends DatabasePropertiesBuilder {

        @Override
        public DatabaseProperties build() {
            return new PostgreSQLDBRM(this);
        }
    }


    @Override
    public String getUrl() {
        String PREFIX = "jdbc:postgresql://";
        setDatabase(getDatabase() == null ? "postgres" : getDatabase());

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
