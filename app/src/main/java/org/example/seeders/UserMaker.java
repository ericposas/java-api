package org.example.seeders;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.javafaker.Faker;

public class UserMaker extends AddressMaker {

    public UserMaker(int iterationAmount) {
        super(iterationAmount);
    }

    @Override
    public void makeEntry(PreparedStatement stmt, int count, Faker faker) {
        try {
            var firstname = faker.name().firstName();
            var lastname = faker.name().lastName();
            stmt.setString(count, firstname);
            stmt.setString(count + 1, lastname);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
