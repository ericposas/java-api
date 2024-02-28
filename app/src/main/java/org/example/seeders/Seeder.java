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

public class Seeder {

    private static Connection db;

    public static void attachUsersTo(int iterateTo, String tableName, String entityId) {
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
                String stmtString = "INSERT INTO " + tableName + " (" + entityId + ", user_id, isprimary) VALUES ";
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
