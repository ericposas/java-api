package org.example.seeders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.example.database.DB;

import com.github.javafaker.Faker;

public class Seeder {

    private static Connection db;

    public static String USERS = "USERS";
    public static String EMAILS = "EMAILS";
    public static String ADDRESSES = "ADDRESSES";
    public static String PHONENUMBERS = "PHONENUMBERS";

    public static void seedEntities(int iterateTo, String tableName, String[] columns) {
        // Seed some of X entity if none exist
        try {
            db = DB.connect();
            java.sql.Statement query = db.createStatement();
            ResultSet rs = query.executeQuery("SELECT * FROM " + tableName);
            if (!rs.isBeforeFirst()) {
                int end = iterateTo;
                Faker faker = new Faker(new Locale("us"));
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
                UserMaker um = new UserMaker(2);
                AddressMaker am = new AddressMaker(5);
                EmailMaker em = new EmailMaker(1);
                PhoneNumberMaker pm = new PhoneNumberMaker(2);
                for (int j = 0; j < end; j++) {
                    if (tableName.equals(USERS)) {
                        um.makeEntry(stmt, count, faker);
                        count = um.iterateCount(count);
                    }
                    if (tableName.equals(ADDRESSES)) {
                        am.makeEntry(stmt, count, faker);
                        count = am.iterateCount(count);
                    }
                    if (tableName.equals(EMAILS)) {
                        em.makeEntry(stmt, count, faker);
                        count = em.iterateCount(count);
                    }
                    if (tableName.equals(PHONENUMBERS)) {
                        pm.makeEntry(stmt, count, faker);
                        count = pm.iterateCount(count);
                    }
                }
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
                    int range = (end - 1) + 1;
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
                    int range = (end - 1) + 1;
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
                System.out.println("Randomly attached n " + entityIdColumn.split("_")[0] + " entities " + "to User(s)");
            }
            db.close();
        } catch (SQLException e) {
            if (e.getErrorCode() != 0) {
                e.printStackTrace();
            }
        }
    }

}
