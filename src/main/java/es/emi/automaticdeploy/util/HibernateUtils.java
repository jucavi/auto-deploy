package es.emi.automaticdeploy.util;

import es.emi.automaticdeploy.entity.ChangeLog;
import es.emi.automaticdeploy.entity.ChangeLogLock;
import es.emi.automaticdeploy.entity.persistence.DatabaseProperties;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtils {

    private static SessionFactory sessionFactory;

//    public static SessionFactory getSessionFactory(DatabaseProperties dbProperties) {
//        if (sessionFactory == null) {
//            try {
//
//                Configuration configuration = getConfiguration(dbProperties);
//
//                // Annotated classes
//                configuration.addAnnotatedClass(ChangeLog.class);
//                configuration.addAnnotatedClass(ChangeLogLock.class);
//
//                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
//                        .applySettings(configuration.getProperties()).build();
//
//                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
//            } catch (Exception e) {
//                throw new RuntimeException("There was an error building the factory");
//            }
//        }
//        return sessionFactory;
//    }

    public static SessionFactory getSessionFactory(DatabaseProperties dbProperties) {

        try {

            Configuration configuration = getConfiguration(dbProperties);

            // Annotated classes
            configuration.addAnnotatedClass(ChangeLog.class);
            configuration.addAnnotatedClass(ChangeLogLock.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            throw new RuntimeException("There was an error building the factory");
        }

        return sessionFactory;
    }

    private static Configuration getConfiguration(DatabaseProperties dbProperties) {

        Configuration configuration = new Configuration();

        configuration.setProperty("hibernate.connection.driver_class", dbProperties.getDriver());
        configuration.setProperty("hibernate.connection.url", dbProperties.getUrl());
        configuration.setProperty("hibernate.connection.username", dbProperties.getUser());
        configuration.setProperty("hibernate.connection.password", dbProperties.getPassword());
        configuration.setProperty("hibernate.dialect", dbProperties.getDialect());
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.format_sql", "true");
        configuration.setProperty("hibernate.use_sql_comments", "true");
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");

        return configuration;
    }
}