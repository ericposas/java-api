package org.example.seeders;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.javafaker.Faker;

public class AddressMaker implements RowEntryMaker {

    public int iterationAmount;

    public AddressMaker(int iterationAmount) {
        this.iterationAmount = iterationAmount;
    }

    @Override
    public int getCountIterationAmount() {
        return iterationAmount;
    }

    @Override
    public int iterateCount(int count) {
        return count += iterationAmount;
    }

    @Override
    public void makeEntry(PreparedStatement stmt, int count, Faker faker) {
        try {
            stmt.setString(count, faker.address().streetAddress());
            stmt.setString(count + 1, faker.address().city());
            String state = faker.address().stateAbbr();
            stmt.setString(count + 2, faker.address().zipCodeByState(state));
            stmt.setString(count + 3, state);
            stmt.setString(count + 4, faker.address().countryCode());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
