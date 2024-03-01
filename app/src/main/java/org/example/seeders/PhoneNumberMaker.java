package org.example.seeders;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import com.github.javafaker.Faker;

public class PhoneNumberMaker extends AddressMaker {

    private final static String HOME = "HOME";
    private final static String WORK = "WORK";
    private final static String MOBILE = "MOBILE";

    public PhoneNumberMaker(int iterationAmount) {
        super(iterationAmount);
    }

    @Override
    public void makeEntry(PreparedStatement stmt, int count, Faker faker) {
        try {
            var types = new String[] { MOBILE, HOME, WORK };
            var range = (types.length - 1) + 1;
            Random random = new Random();
            var randInt = random.nextInt(range) + 1;
            var phonenumber = faker.phoneNumber().cellPhone();
            stmt.setString(count, phonenumber);
            stmt.setString(count + 1, types[randInt - 1]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
