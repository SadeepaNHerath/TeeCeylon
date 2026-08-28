package org.example.util;

import org.example.entity.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HibernateUtil {
    private static final Logger LOGGER = Logger.getLogger(HibernateUtil.class.getName());
    private static final SessionFactory sessionFactory = createSessionFactory();

    private static SessionFactory createSessionFactory() {
        try {
            StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

            // Load properties from application.properties
            Properties appProps = new Properties();
            try (InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (input != null) {
                    appProps.load(input);
                }
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Could not load application.properties, falling back to defaults", ex);
            }

            Map<String, Object> settings = new HashMap<>();
            settings.put("hibernate.connection.driver_class", appProps.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
            settings.put("hibernate.connection.url", appProps.getProperty("db.url", "jdbc:mysql://localhost:3306/Tee-Ceylon?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true"));
            settings.put("hibernate.connection.username", appProps.getProperty("db.user", "root"));
            settings.put("hibernate.connection.password", appProps.getProperty("db.password", "12345"));
            settings.put("hibernate.dialect", appProps.getProperty("db.dialect", "org.hibernate.dialect.MySQL8Dialect"));
            settings.put("hibernate.show_sql", appProps.getProperty("db.show_sql", "true"));
            settings.put("hibernate.hbm2ddl.auto", appProps.getProperty("db.hbm2ddl.auto", "update"));

            registryBuilder.applySettings(settings);
            StandardServiceRegistry registry = registryBuilder.build();

            Metadata metadata = new MetadataSources(registry)
                    .addAnnotatedClass(EmployeeEntity.class)
                    .addAnnotatedClass(SupplierEntity.class)
                    .addAnnotatedClass(ProductEntity.class)
                    .addAnnotatedClass(CustomerEntity.class)
                    .addAnnotatedClass(OrderEntity.class)
                    .addAnnotatedClass(OrderDetailsEntity.class)
                    .addAnnotatedClass(PurchaseEntity.class)
                    .addAnnotatedClass(PurchaseDetailsEntity.class)
                    .getMetadataBuilder()
                    .applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE)
                    .build();

            return metadata.getSessionFactoryBuilder().build();
        } catch (Throwable ex) {
            LOGGER.log(Level.SEVERE, "Initial SessionFactory creation failed." + ex.getMessage(), ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session getSession() {
        try {
            return sessionFactory.openSession();
        } catch (HibernateException e) {
            LOGGER.log(Level.SEVERE, "Failed to open Hibernate session: " + e.getMessage(), e);
            return null;
        }
    }
}
