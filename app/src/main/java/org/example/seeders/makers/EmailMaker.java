package org.example.seeders.makers;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.example.objects.Emailaddress;
import org.example.seeders.Constants;

import com.github.javafaker.Faker;

public class EmailMaker extends BaseMaker<Emailaddress> {

    @Override
    public int getCountIterationAmount() {
        return Constants.EMAIL_COLUMNS.length;
    }

    @Override
    public void makeEntry(PreparedStatement stmt, int count, Faker faker) {
        try {
            var firstAnimal = faker.animal().name();
            var first = firstAnimal.split(" ").length > 1
                    ? firstAnimal.split(" ")[0] + firstAnimal.split(" ")[1]
                    : firstAnimal;
            first = first.trim();
            var lastAnimal = faker.animal().name();
            var last = lastAnimal.split(" ").length > 1
                    ? lastAnimal.split(" ")[0] + lastAnimal.split(" ")[1]
                    : lastAnimal;
            last = last.trim();
            stmt.setString(count, first + "." + last + "@" + faker.internet().domainName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
