package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    public static Connection getDbConnection() {
        return ConnectionHolder.CONNECTION_HOLDER;
    }

    public static Connection connect() throws SQLException {
        try {
            // Get database credentials from DatabaseConfig class
            var jdbcUrl = DatabaseConfig.getDbUrl();
            var user = DatabaseConfig.getDbUsername();
            var password = DatabaseConfig.getDbPassword();
            // Open a connection
            if (jdbcUrl == null) {
                System.err.println("jdbcUrl is null");
            }
            return DriverManager.getConnection(jdbcUrl, user, password);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    private static interface ConnectionHolder {
        public static final Connection CONNECTION_HOLDER = (Connection) connect();

        public static Object connect() {
            try {
                var connection = DB.connect();
                if (connection != null) {
                    System.out.println("Connected to the PostgreSQL database");
                } else {
                    System.err.println("Connection could not be established.");
                }
                return connection;
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                return null;
            }
        }
    }

}
