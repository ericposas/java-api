package org.example.seeders.makers;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import org.example.seeders.Constants;

import com.github.javafaker.Faker;

public class PhoneNumberMaker extends BaseMaker {

    @Override
    public int getCountIterationAmount() {
        return Constants.PHONENUMBER_COLUMNS.length;
    }

    @Override
    public void makeEntry(PreparedStatement stmt, int count, Faker faker) {
        try {
            var range = (Constants.PHONE_TYPES.length - 1) + 1;
            Random random = new Random();
            var randInt = random.nextInt(range) + 1;
            var phonenumber = faker.phoneNumber().cellPhone();
            stmt.setString(count, phonenumber);
            stmt.setString(count + 1, Constants.PHONE_TYPES[randInt - 1]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
