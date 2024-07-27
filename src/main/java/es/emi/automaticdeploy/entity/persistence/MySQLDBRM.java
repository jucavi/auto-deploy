package es.emi.automaticdeploy.entity.persistence;

import es.emi.automaticdeploy.util.URLUtils;

public class MySQLDBRM extends DatabaseProperties {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DIALECT = "org.hibernate.dialect.MySQLDialect";

    /**
     * Constructor
     *
     * @param builder builder
     */
    private MySQLDBRM(MySQLDBRMBuilder builder) {
        super(builder);
    }


    /**
     * Builder
     */
    public static class MySQLDBRMBuilder extends DatabasePropertiesBuilder {

        @Override
        public DatabaseProperties build() {
            return new MySQLDBRM(this);
        }
    }


    @Override
    public String getUrl() {
        String PREFIX = "jdbc:mysql://";
        setDatabase(getDatabase() == null ? "information_schema" : getDatabase());

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
