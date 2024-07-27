package es.emi.automaticdeploy.util;

import es.emi.automaticdeploy.entity.ChangeLog;
import es.emi.automaticdeploy.entity.persistence.DatabaseProperties;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.HashMap;
import java.util.Map;

public class HibernateUtils {

    private final static Map<String, SessionFactory> sessionFactoryMap = new HashMap<>();

    private static SessionFactory buildSessionFactory(DatabaseProperties dbProperties) {

        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", dbProperties.getDriver());
        configuration.setProperty("hibernate.connection.url", dbProperties.getUrl());
        configuration.setProperty("hibernate.connection.username", dbProperties.getUser());
        configuration.setProperty("hibernate.connection.password", dbProperties.getPassword());
        configuration.setProperty("hibernate.dialect", dbProperties.getDialect());
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.addAnnotatedClass(ChangeLog.class);

        return configuration.buildSessionFactory();
    }

    public static SessionFactory getSessionFactory(DatabaseProperties dbProperties) {

        String key = dbProperties.getUrl();
        if (!sessionFactoryMap.containsKey(key)) {
            sessionFactoryMap.put(key, buildSessionFactory(dbProperties));
        }
        return sessionFactoryMap.get(key);
    }

    public static void shutdown() {
        sessionFactoryMap.values().forEach(SessionFactory::close);
    }
}