package org.example.seeders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.example.database.DB;

import com.github.javafaker.Faker;

public class Seeder {

    public static String USERS = "USERS";
    public static String ADDRESSES = "ADDRESSES";

    private static Connection db;

    public static void generateTables() {
        // Generate tables
        try {
            db = DB.connect();
            java.sql.Statement create = db.createStatement();
            create.executeQuery("CREATE TABLE IF NOT EXISTS USERS (\r\n" + //
                    "    id SERIAL PRIMARY KEY,\r\n" + //
                    "    firstname VARCHAR(255),\r\n" + //
                    "    lastname VARCHAR(255)\r\n" + //
                    ");\r\n" + //
                    "\r\n" + //
                    "CREATE TABLE IF NOT EXISTS ADDRESSES (\r\n" + //
                    "    id SERIAL PRIMARY KEY,\r\n" + //
                    "    line1 VARCHAR(255),\r\n" + //
                    "    line2 VARCHAR(255),\r\n" + //
                    "    city VARCHAR(255),\r\n" + //
                    "    postalcode VARCHAR(255),\r\n" + //
                    "    stateprovince VARCHAR(255),\r\n" + //
                    "    countryid VARCHAR(255),\r\n" + //
                    "    UNIQUE(line1)\r\n" + //
                    ");\r\n" + //
                    "\r\n" + //
                    "CREATE TABLE IF NOT EXISTS USERSADDRESSES (\r\n" + //
                    "    user_id INT,\r\n" + //
                    "    address_id INT,\r\n" + //
                    "    isprimary BOOLEAN,\r\n" + //
                    "    FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE,\r\n" + //
                    "    FOREIGN KEY (address_id) REFERENCES ADDRESSES(id) ON DELETE CASCADE,\r\n" + //
                    "    UNIQUE(address_id),\r\n" + //
                    "    PRIMARY KEY(user_id, address_id)\r\n" + //
                    ");\r\n" + //
                    "\r\n" + //
                    "CREATE INDEX IF NOT EXISTS firstname_search ON USERS(firstname);\r\n" + //
                    "\r\n" + //
                    "CREATE INDEX IF NOT EXISTS lastname_search ON USERS(lastname);\r\n" + //
                    "\r\n" + //
                    "CREATE INDEX IF NOT EXISTS lowercase_firstname_search ON USERS(lower(firstname));\r\n" + //
                    "\r\n" + //
                    "CREATE INDEX IF NOT EXISTS lowercase_lastname_search ON USERS(lower(lastname));").close();
            System.out.println("Generated tables");
        } catch (SQLException e) {
            if (e.getErrorCode() != 0) {
                e.printStackTrace();
            } else {
                System.out.println("Generated tables");
            }
        }
    }

    public static void seedEntities(int iterateTo, String tableName, List<String> columns) {
        // Seed some of X entity if none exist
        try {
            db = DB.connect();
            java.sql.Statement query = db.createStatement();
            ResultSet rs = query.executeQuery("SELECT * FROM " + tableName);
            if (!rs.isBeforeFirst()) {
                int end = iterateTo;
                Faker faker = new Faker();
                int columnsCount = (int) columns.stream().count();
                String columnsToString = columns.stream()
                        .map(Object::toString)
                        .collect(java.util.stream.Collectors.joining(","));
                String stmtString = "INSERT INTO " + tableName
                        + " (" + columnsToString
                        + ") VALUES ";
                String columnParams = "(";
                for (int c = 0; c < columnsCount; c++) {
                    columnParams += "?";
                    if (c != columnsCount - 1) {
                        columnParams += ",";
                    }
                }
                columnParams += ")";
                for (int i = 0; i < end; i++) {
                    if (i == end - 1) {
                        stmtString += columnParams + ";";
                    } else {
                        stmtString += columnParams + ",";
                    }
                }
                PreparedStatement stmt = db.prepareStatement(stmtString);
                var count = 1;
                for (int j = 0; j < end; j++) {
                    if (tableName.equals(ADDRESSES)) {
                        stmt.setString(count, faker.address().streetAddress());
                        stmt.setString(count + 1, faker.address().city());
                        String state = faker.address().stateAbbr();
                        stmt.setString(count + 2, faker.address().zipCodeByState(state));
                        stmt.setString(count + 3, state);
                        stmt.setString(count + 4, faker.address().countryCode());
                        count += 5;
                    }
                    if (tableName.equals(USERS)) {
                        stmt.setString(count, faker.name().firstName());
                        stmt.setString(count + 1, faker.name().lastName());
                        count += 2;
                    }
                }
                stmt.executeQuery();
                System.out.println("Generated " + end + tableName.toLowerCase());
            }
            db.close();
            System.out.println("Seeded " + tableName);
        } catch (SQLException e) {
            if (e.getErrorCode() != 0) {
                e.printStackTrace();
            } else {
                System.out.println("Seeded " + tableName);
            }
        }
    }

    public static void attachUsersTo(int iterateTo, String tableName, String entityIdColumn) {
        // Randomly attach users to addresses if no fields in UsersAddresses exist
        try {
            db = DB.connect();
            java.sql.Statement query = db.createStatement();
            ResultSet rs = query.executeQuery("SELECT * FROM " + tableName);
            if (!rs.isBeforeFirst()) {
                int count = 1;
                int end = iterateTo;
                Map<Integer, Integer> userMap = new HashMap<>();
                Map<Integer, Integer> entityMap = new HashMap<>();
                List<Integer> entityIds = new ArrayList<>();
                for (int k = 0; k < end; k++) {
                    java.util.Random random = new Random();
                    int range = end - 1 + 1;
                    int address_id = random.nextInt(range) + 1;
                    if (entityMap.getOrDefault(address_id, 0).equals(0)) {
                        entityMap.put(address_id, 1);
                        entityIds.add(address_id);
                    }
                }
                String stmtString = "INSERT INTO " + tableName + " (" + entityIdColumn
                        + ", user_id, isprimary) VALUES ";
                for (int i = 0; i < entityIds.size(); i++) {
                    stmtString += "(?,?,?)";
                    if (i == entityIds.size() - 1) {
                        stmtString += ";";
                    } else {
                        stmtString += ",";
                    }
                }
                PreparedStatement stmt = db.prepareStatement(stmtString);
                for (int j = 0; j < entityIds.size(); j++) {
                    Random random = new Random();
                    int range = end - 1 + 1;
                    int user_id = random.nextInt(range) + 1;
                    if (userMap.getOrDefault(user_id, 0).equals(0)) {
                        userMap.put(user_id, 1);
                        stmt.setInt(count, entityIds.get(j));
                        stmt.setInt(count + 1, user_id);
                        stmt.setBoolean(count + 2, true);
                    } else {
                        userMap.put(user_id, userMap.get(user_id) + 1);
                        stmt.setInt(count, entityIds.get(j));
                        stmt.setInt(count + 1, user_id);
                        stmt.setBoolean(count + 2, false);
                    }
                    count += 3;
                }
                stmt.executeQuery();
            }
            db.close();
            System.out.println("Randomly attached n " + entityIdColumn.split("_")[0] + " entities " + "to User(s)");
        } catch (SQLException e) {
            if (e.getErrorCode() != 0) {
                e.printStackTrace();
            } else {
                System.out.println("Randomly attached n " + entityIdColumn.split("_")[0] + " entities " + "to User(s)");
            }
        }
    }

}
