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
import org.example.seeders.makers.IRowEntryMaker;
import org.jibx.schema.codegen.extend.DefaultNameConverter;
import org.jibx.schema.codegen.extend.NameConverter;

import com.github.javafaker.Faker;

public class Seeder {

    private static Connection db;

    public static void seedAll() {
        // IMPORTANT: Seeder.seedEntities has logic that needs custom if blocks to work
        // with various Entities!
        seedEntities(100, Constants.USERS);
        seedEntities(300, Constants.EMAILS);
        seedEntities(200, Constants.ADDRESSES);
        seedEntities(200, Constants.PHONENUMBERS);
    }

    private static void processEntries(String tableName, PreparedStatement stmt, int end) {
        int count = 1;
        Faker faker = new Faker();
        IRowEntryMaker maker = SeederHelper.getMaker(tableName);
        try {
            for (int j = 0; j < end; j++) {
                maker.makeEntry(stmt, count, faker);
                count = maker.iterateCount(count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String processInsertStatement(String tableName, int end) {
        String[] columns = SeederHelper.getColumnsForEntity(tableName);
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
        return stmtString;
    }

    public static void seedEntities(int iterateTo, String tableName) {
        // Seed some of X entity if none exist
        try {
            db = DB.connect();
            java.sql.Statement query = db.createStatement();
            ResultSet rs = query.executeQuery("SELECT * FROM " + tableName);
            if (!rs.isBeforeFirst()) {
                int end = iterateTo;
                String insertStatement = processInsertStatement(tableName, end);
                PreparedStatement stmt = db.prepareStatement(insertStatement);
                processEntries(tableName, stmt, end);
                stmt.executeQuery();
                System.out.println("Generated " + end + tableName.toLowerCase());
                System.out.println("Seeded " + tableName);
            }
            db.close();
        } catch (SQLException e) {
            if (e.getErrorCode() != 0) {
                e.printStackTrace();
            }
        }
        if (!tableName.equals(Constants.USERS)) {
            attachEntitiesToUsers(tableName);
        }
    }

    public static void attachEntitiesToUsers(String tableName) {
        // Randomly attach users to addresses if no fields in UsersAddresses exist
        try {
            db = DB.connect();
            // first get the total count of users in the database
            java.sql.Statement usersQ = db.createStatement();
            ResultSet uRs = usersQ.executeQuery("SELECT COUNT(id) FROM USERS");
            int usersCount = 0;
            while (uRs.next()) {
                usersCount = uRs.getInt("count");
            }
            // ResultSet .isBeforeFirst() checks if there are any results from the select
            // query
            java.sql.Statement query = db.createStatement();
            ResultSet rs = query.executeQuery("SELECT * FROM USERS" + tableName);
            if (!rs.isBeforeFirst()) {
                // get the total count of the chosen entities in the database
                java.sql.Statement entityQ = db.createStatement();
                ResultSet entRs = entityQ.executeQuery("SELECT COUNT(id) FROM " + tableName);
                int entityCount = 0;
                while (entRs.next()) {
                    entityCount = entRs.getInt("count");
                }
                int count = 1;
                Map<Integer, Integer> userMap = new HashMap<>();
                Map<Integer, Integer> entityMap = new HashMap<>();
                List<Integer> entityIds = new ArrayList<>();
                for (int k = 0; k < entityCount; k++) {
                    java.util.Random random = new Random();
                    int range = (entityCount - 1) + 1;
                    int entity_id = random.nextInt(range) + 1;
                    if (entityMap.getOrDefault(entity_id, 0).equals(0)) {
                        entityMap.put(entity_id, 1);
                        entityIds.add(entity_id);
                    }
                }
                NameConverter nameTools = new DefaultNameConverter();
                String entId = nameTools.depluralize(tableName.toLowerCase()) + "_id";
                String stmtString = "INSERT INTO USERS" + tableName + " (" + entId + ", user_id, isprimary) VALUES ";
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
                    int range = (usersCount - 1) + 1;
                    Integer user_id = random.nextInt(range) + 1;
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
                System.out.println("Randomly attached n " + tableName + " entities " + "to User(s)");
            }
            db.close();
        } catch (SQLException e) {
            if (e.getErrorCode() != 0) {
                e.printStackTrace();
            }
        }
    }

}
