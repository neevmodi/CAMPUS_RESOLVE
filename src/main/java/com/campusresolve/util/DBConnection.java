package com.campusresolve.util;

import com.campusresolve.exception.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class DBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private DBConnection() { }
    public static Connection open() {
        String url = env("CAMPUS_DB_URL", "jdbc:mysql://localhost:3306/campus_resolve?serverTimezone=UTC");
        String user = requiredEnv("CAMPUS_DB_USER");
        String password = requiredEnv("CAMPUS_DB_PASSWORD");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException exception) {
            throw new DatabaseException("MySQL JDBC driver is not available to the application.", exception);
        } catch (SQLException exception) {
            LOGGER.warning(() -> "Campus Resolve database connection failed (SQLState="
                    + exception.getSQLState() + ", errorCode=" + exception.getErrorCode() + ").");
            throw new DatabaseException("Unable to connect to the Campus Resolve database.", exception);
        }
    }
    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
    private static String requiredEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new DatabaseException("Database configuration is incomplete. Set " + name + " for Tomcat before starting it.", null);
        }
        return value;
    }
}
