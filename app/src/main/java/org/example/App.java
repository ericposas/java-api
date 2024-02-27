/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;

import org.example.controllers.StartController;
import org.example.controllers.UsersController;
import org.example.database.DB;

import com.github.javafaker.Faker;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;

public class App {

    private static Connection db;
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        RoutingHandler routingHandler = Handlers.routing()
                .addAll(StartController.handlers())
                .addAll(UsersController.handlers());

        HttpHandler rootHandler = new AccessLogHandler(
                ExceptionHandlers.wrap(routingHandler),
                (message) -> log.info(message),
                "combined",
                App.class.getClassLoader());

        Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(rootHandler)
                .build()
                .start();

        // Generate tables
        try {
            db = DB.connect();
            java.sql.Statement create = db.createStatement();
            create.executeQuery("create table IF NOT EXISTS USERS (\r\n" + //
                    "    id SERIAL PRIMARY KEY,\r\n" + //
                    "    firstname varchar(255),\r\n" + //
                    "    lastname varchar(255)\r\n" + //
                    ");\r\n" + //
                    "\r\n" + //
                    "create table IF NOT EXISTS ADDRESSES (\r\n" + //
                    "    id SERIAL PRIMARY KEY,\r\n" + //
                    "    line1 varchar(255),\r\n" + //
                    "    line2 varchar(255),\r\n" + //
                    "    city varchar(255),\r\n" + //
                    "    postalcode varchar(255),\r\n" + //
                    "    stateprovince varchar(255),\r\n" + //
                    "    countryid varchar(255),\r\n" + //
                    "    UNIQUE(line1)\r\n" + //
                    ");\r\n" + //
                    "\r\n" + //
                    "create table IF NOT EXISTS USERSADDRESSES (\r\n" + //
                    "    user_id int,\r\n" + //
                    "    address_id int,\r\n" + //
                    "    isprimary BOOLEAN,\r\n" + //
                    "    FOREIGN KEY (user_id) REFERENCES USERS(id),\r\n" + //
                    "    FOREIGN KEY (address_id) REFERENCES ADDRESSES(id),\r\n" + //
                    "    UNIQUE(address_id)\r\n" + //
                    ");").close();
            System.out.println("Generated tables");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Seed some users if none exist
        try {
            db = DB.connect();
            java.sql.Statement query = db.createStatement();
            ResultSet rs = query.executeQuery("SELECT * FROM USERS");
            if (!rs.isBeforeFirst()) {
                int end = 100;
                Faker faker = new Faker();
                String stmtString = "INSERT INTO USERS (firstname, lastname) VALUES ";
                for (int i = 0; i < end; i++) {
                    if (i == end - 1) {
                        stmtString += "(?,?);";
                    } else {
                        stmtString += "(?,?),";
                    }
                }
                PreparedStatement stmt = db.prepareStatement(stmtString);
                var count = 1;
                for (int j = 0; j < end; j++) {
                    stmt.setString(count, faker.name().firstName());
                    stmt.setString(count + 1, faker.name().lastName());
                    count += 2;
                }
                stmt.executeQuery();
                System.out.println("Generated " + end + " users");
            }
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Seed some addresses if none exist
        try {
            db = DB.connect();
            java.sql.Statement query = db.createStatement();
            ResultSet rs = query.executeQuery("SELECT * FROM ADDRESSES");
            if (!rs.isBeforeFirst()) {
                int end = 100;
                Faker faker = new Faker();
                String stmtString = "INSERT INTO ADDRESSES (line1, city, postalcode, stateprovince, countryid) VALUES ";
                for (int i = 0; i < end; i++) {
                    if (i == end - 1) {
                        stmtString += "(?,?,?,?,?);";
                    } else {
                        stmtString += "(?,?,?,?,?),";
                    }
                }
                PreparedStatement stmt = db.prepareStatement(stmtString);
                var count = 1;
                for (int j = 0; j < end; j++) {
                    stmt.setString(count, faker.address().streetAddress());
                    stmt.setString(count + 1, faker.address().city());
                    String state = faker.address().stateAbbr();
                    stmt.setString(count + 2, faker.address().zipCodeByState(state));
                    stmt.setString(count + 3, state);
                    stmt.setString(count + 4, faker.address().countryCode());
                    count += 5;
                }
                stmt.executeQuery();
                System.out.println("Generated " + end + " addresses");
            }
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Randomly attach users to addresses if no fields in UsersAddresses exist
        try {
            db = DB.connect();
            java.sql.Statement query = db.createStatement();
            ResultSet rs = query.executeQuery("SELECT * FROM USERSADDRESSES");
            if (!rs.isBeforeFirst()) {
                int count = 1;
                int end = 100;
                String stmtString = "INSERT INTO USERSADDRESSES (address_id, user_id, isprimary) VALUES ";
                for (int i = 0; i < end; i++) {
                    stmtString += "(?,?,?)";
                    if (i == end - 1) {
                        stmtString += ";";
                    } else {
                        stmtString += ",";
                    }
                }
                HashMap<Integer, Integer> usermap = new HashMap<>();
                HashMap<Integer, Integer> addressmap = new HashMap<>();
                PreparedStatement stmt = db.prepareStatement(stmtString);
                for (int j = 0; j < end; j++) {
                    Random random = new Random();
                    int range = end - 1 + 1;
                    int address_id = random.nextInt(range) + 1;
                    int user_id = random.nextInt(range) + 1;
                    if (addressmap.getOrDefault(address_id, 0).equals(0)) {
                        addressmap.put(address_id, 1);
                        if (usermap.getOrDefault(user_id, 0).equals(0)) {
                            usermap.put(user_id, 1);
                            stmt.setInt(count, address_id);
                            stmt.setInt(count + 1, user_id);
                            stmt.setBoolean(count + 2, true);
                        } else {
                            usermap.put(user_id, usermap.get(user_id) + 1);
                            stmt.setInt(count, address_id);
                            stmt.setInt(count + 1, user_id);
                            stmt.setBoolean(count + 2, false);
                        }
                    } else {
                        stmt.setNull(count, 0);
                        stmt.setNull(count + 1, 0);
                        stmt.setNull(count + 2, 0);
                    }
                    count += 3;
                }
                stmt.executeQuery();
            }
            db.close();
        } catch (

        SQLException e) {
            e.printStackTrace();
        }

    }

}
