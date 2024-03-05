package org.example.seeders.makers;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.example.objects.Address;
import org.example.seeders.Constants;

import com.github.javafaker.Faker;

public class AddressMaker extends BaseMaker<Address> {

    @Override
    public int getCountIterationAmount() {
        return Constants.ADDRESS_COLUMNS.length;
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
