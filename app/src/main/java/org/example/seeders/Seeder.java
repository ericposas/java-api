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
import org.jibx.schema.codegen.extend.DefaultNameConverter;
import org.jibx.schema.codegen.extend.NameConverter;

import com.github.javafaker.Faker;

public class Seeder {

    private static Connection db;

    public static final String USERS = "USERS";
    public static final String EMAILS = "EMAILS";
    public static final String ADDRESSES = "ADDRESSES";
    public static final String PHONENUMBERS = "PHONENUMBERS";

    private static String[] USER_COLUMNS = new String[] { "firstname", "middlename", "lastname" };
    private static String[] EMAIL_COLUMNS = new String[] { "email" };
    private static String[] ADDRESS_COLUMNS = new String[] { "line1", "city", "stateprovince", "postalcode",
            "countryid" };
    private static String[] PHONENUMBER_COLUMNS = new String[] { "phonenumber", "phonetype" };

    private static String[] getColumnsForEntity(String entityType) {
        switch (entityType) {
            case USERS:
                return USER_COLUMNS;
            case EMAILS:
                return EMAIL_COLUMNS;
            case ADDRESSES:
                return ADDRESS_COLUMNS;
            case PHONENUMBERS:
                return PHONENUMBER_COLUMNS;
            default:
                return USER_COLUMNS;
        }
    }

    private static void processEntries(String tableName, PreparedStatement stmt, Faker faker, int end, int count,
            int columnsCount) {
        if (tableName.equals(USERS)) {
            UserMaker um = new UserMaker(columnsCount);
            for (int j = 0; j < end; j++) {
                um.makeEntry(stmt, count, faker);
                count = um.iterateCount(count);
            }
        }
        if (tableName.equals(ADDRESSES)) {
            AddressMaker am = new AddressMaker(columnsCount);
            for (int j = 0; j < end; j++) {
                am.makeEntry(stmt, count, faker);
                count = am.iterateCount(count);
            }
        }
        if (tableName.equals(EMAILS)) {
            EmailMaker em = new EmailMaker(columnsCount);
            for (int j = 0; j < end; j++) {
                em.makeEntry(stmt, count, faker);
                count = em.iterateCount(count);
            }
        }
        if (tableName.equals(PHONENUMBERS)) {
            PhoneNumberMaker pm = new PhoneNumberMaker(columnsCount);
            for (int j = 0; j < end; j++) {
                pm.makeEntry(stmt, count, faker);
                count = pm.iterateCount(count);
            }
        }
    }

    private static java.util.HashMap<String, String> processInsertStatement(String tableName, int end) {
        String[] columns = getColumnsForEntity(tableName);
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
        java.util.HashMap<String, String> map = new HashMap<>();
        map.put("insertStatement", stmtString);
        map.put("columnCount", String.valueOf(columnsCount));
        return map;
    }

    public static void seedEntities(int iterateTo, String tableName) {
        // Seed some of X entity if none exist
        try {
            db = DB.connect();
            java.sql.Statement query = db.createStatement();
            ResultSet rs = query.executeQuery("SELECT * FROM " + tableName);
            if (!rs.isBeforeFirst()) {
                var count = 1;
                int end = iterateTo;
                Faker faker = new Faker(new Locale("us"));
                java.util.HashMap<String, String> processedInsertResult = processInsertStatement(tableName, end);
                PreparedStatement stmt = db.prepareStatement(processedInsertResult.get("insertStatement"));
                processEntries(tableName, stmt, faker, end, count,
                        Integer.parseInt(processedInsertResult.get("columnCount")));
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
        if (!tableName.equals(ADDRESSES)) {
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
