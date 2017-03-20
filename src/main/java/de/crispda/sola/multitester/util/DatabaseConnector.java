package de.crispda.sola.multitester.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector {
    private static DatabaseConnector instance = null;

    private Connection connection;

    private DatabaseConnector() {
    }

    private void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:" + Paths.get("mysqlServerUrl"), Paths.get("mysqlUser"),
                Paths.get("mysqlPass"));
    }

    public static void execute(String query) throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnector();
            instance.connect();
        }

        if (instance.connection == null || instance.connection.isClosed()) {
            instance.connect();
        }

        if (instance.connection == null || instance.connection.isClosed())
            throw new RuntimeException("Not connected!");

        Statement stmt = instance.connection.createStatement();
        stmt.executeUpdate(query);
        stmt.close();
    }

    public static void close() {
        if (instance != null) {
            if (instance.connection != null) {
                try {
                    instance.connection.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }
}
