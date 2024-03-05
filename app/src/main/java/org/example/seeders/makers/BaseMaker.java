package org.example.seeders.makers;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.javafaker.Faker;

public class BaseMaker implements RowEntryMaker {

    @Override
    public int getCountIterationAmount() {
        return 0;
    }

    @Override
    public int iterateCount(int count) {
        return count += getCountIterationAmount();
    }

    @Override
    public void makeEntry(PreparedStatement stmt, int count, Faker faker) throws SQLException {
    }

}
