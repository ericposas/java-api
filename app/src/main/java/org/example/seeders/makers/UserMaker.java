package org.example.seeders.makers;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import org.example.seeders.Constants;

import com.github.javafaker.Faker;

public class UserMaker extends BaseMaker {

    @Override
    public int getCountIterationAmount() {
        return Constants.USER_COLUMNS.length;
    }

    @Override
    public void makeEntry(PreparedStatement stmt, int count, Faker faker) {
        try {
            var firstname = faker.name().firstName();
            var lastname = faker.name().lastName();
            Random random = new Random();
            var randomNum = random.nextInt((10 - 1) + 1) + 1;
            var middlename = randomNum % 2 == 0 ? faker.name().firstName() : faker.funnyName().name().split(" ")[0];
            stmt.setString(count, firstname);
            stmt.setString(count + 1, middlename);
            stmt.setString(count + 2, lastname);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
