package org.example.seeders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.example.database.DB;

import com.github.javafaker.Faker;

public class Seeder {

    private static Connection db;

    public static String USERS = "USERS";
    public static String EMAILS = "EMAILS";
    public static String ADDRESSES = "ADDRESSES";

    public static void seedEntities(int iterateTo, String tableName, String[] columns) {
        // Seed some of X entity if none exist
        try {
            db = DB.connect();
            java.sql.Statement query = db.createStatement();
            ResultSet rs = query.executeQuery("SELECT * FROM " + tableName);
            if (!rs.isBeforeFirst()) {
                int end = iterateTo;
                Faker faker = new Faker();
                int columnsCount = (int) columns.length;
                String columnsToString = Arrays.asList(columns)
                        .stream()
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
                        var firstname = faker.name().firstName();
                        var lastname = faker.name().lastName();
                        stmt.setString(count, firstname);
                        stmt.setString(count + 1, lastname);
                        count += 2;
                    }
                    if (tableName.equals(EMAILS)) {
                        var firstAnimal = faker.animal().name();
                        var first = firstAnimal.split(" ").length > 1
                                ? firstAnimal.split(" ")[0] + firstAnimal.split(" ")[1]
                                : firstAnimal;
                        var lastAnimal = faker.animal().name();
                        var last = lastAnimal.split(" ").length > 1
                                ? lastAnimal.split(" ")[0] + lastAnimal.split(" ")[1]
                                : lastAnimal;
                        stmt.setString(count, first + "." + last + "@" + faker.internet().domainName());
                        count++;
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
                    int entity_id = random.nextInt(range) + 1;
                    if (entityMap.getOrDefault(entity_id, 0).equals(0)) {
                        entityMap.put(entity_id, 1);
                        entityIds.add(entity_id);
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
