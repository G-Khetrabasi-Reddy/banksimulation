package org.bank.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

public class DBConfig {

    private static Properties prop = new Properties(); // Create Properties object to read key-value pairs

    static {
        try (InputStream input = DBConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) { // Load the properties file from resources


            if (input != null) {
                // Load all properties from the file
                prop.load(input);
                Class.forName(prop.getProperty("spring.datasource.driver-class-name"));
            } else {
                System.out.println("Sorry, unable to find application.properties");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Always create a new connection when called
    public static Connection getConnection() throws SQLException {
        String url = prop.getProperty("spring.datasource.url");
        String user = prop.getProperty("spring.datasource.username");
        String password = prop.getProperty("spring.datasource.password");
        return DriverManager.getConnection(url, user, password);
    }
}
