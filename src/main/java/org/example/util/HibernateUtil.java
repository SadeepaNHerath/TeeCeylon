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

import java.io.File;
import java.io.FileInputStream;
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

            // 1. Load default properties from bundled classpath resource
            Properties appProps = new Properties();
            try (InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (input != null) {
                    appProps.load(input);
                }
            } catch (Exception ex) {
                LOGGER.log(Level.WARNING, "Could not load bundled application.properties", ex);
            }

            // 2. Override with external application.properties in working directory if present
            File externalConfig = new File("application.properties");
            if (externalConfig.exists() && externalConfig.isFile()) {
                try (InputStream extInput = new FileInputStream(externalConfig)) {
                    appProps.load(extInput);
                    LOGGER.info("Loaded external configuration from: " + externalConfig.getAbsolutePath());
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Failed to read external application.properties", ex);
                }
            }

            // 3. Check System Environment variables (Aiven / Cloud / Container deployments)
            String envDbUrl = getEnv("AIVEN_MYSQL_URL", "DATABASE_URL", "DB_URL", "JDBC_DATABASE_URL");
            String envDbUser = getEnv("DB_USER", "AIVEN_MYSQL_USER", "DATABASE_USER");
            String envDbPassword = getEnv("DB_PASSWORD", "AIVEN_MYSQL_PASSWORD", "DATABASE_PASSWORD");
            String envSslMode = getEnv("DB_SSL_MODE", "AIVEN_SSL_MODE");

            String dbUrl = envDbUrl != null ? envDbUrl : appProps.getProperty("db.url", "jdbc:mysql://localhost:3306/Tee-Ceylon?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true");
            String dbUser = envDbUser != null ? envDbUser : appProps.getProperty("db.user", "root");
            String dbPassword = envDbPassword != null ? envDbPassword : appProps.getProperty("db.password", "12345");
            String driverClass = appProps.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            String dialect = appProps.getProperty("db.dialect", "org.hibernate.dialect.MySQL8Dialect");
            String showSql = appProps.getProperty("db.show_sql", "false");
            String hbm2ddl = appProps.getProperty("db.hbm2ddl.auto", "update");

            // Format MySQL URL for Aiven / Cloud SSL if needed
            if (envSslMode != null && !dbUrl.contains("sslMode=") && !dbUrl.contains("useSSL=")) {
                String separator = dbUrl.contains("?") ? "&" : "?";
                dbUrl = dbUrl + separator + "sslMode=" + envSslMode + "&allowPublicKeyRetrieval=true";
            }

            Map<String, Object> settings = new HashMap<>();
            settings.put("hibernate.connection.driver_class", driverClass);
            settings.put("hibernate.connection.url", dbUrl);
            settings.put("hibernate.connection.username", dbUser);
            settings.put("hibernate.connection.password", dbPassword);
            settings.put("hibernate.dialect", dialect);
            settings.put("hibernate.show_sql", showSql);
            settings.put("hibernate.hbm2ddl.auto", hbm2ddl);

            // Production & Cloud stability settings (Hikari/Connection validation)
            settings.put("hibernate.connection.autocommit", "false");
            settings.put("hibernate.connection.CharSet", "utf8mb4");
            settings.put("hibernate.connection.characterEncoding", "utf8mb4");
            settings.put("hibernate.connection.useUnicode", "true");

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
            LOGGER.log(Level.SEVERE, "Initial SessionFactory creation failed: " + ex.getMessage(), ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static String getEnv(String... varNames) {
        for (String name : varNames) {
            String value = System.getenv(name);
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
            String sysProp = System.getProperty(name);
            if (sysProp != null && !sysProp.trim().isEmpty()) {
                return sysProp.trim();
            }
        }
        return null;
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
