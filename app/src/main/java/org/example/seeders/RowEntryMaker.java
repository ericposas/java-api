package org.example.seeders;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.javafaker.Faker;

public interface RowEntryMaker {
    /**
     * 
     * @return the iteration amount
     */
    public int getCountIterationAmount();

    /**
     * Take the count parameter and increment by the countIterator amount
     * 
     * @return the updated count (count += countIterator)
     */
    public int iterateCount(int count);

    /**
     * 
     * @param stmt  An SQL PreparedStatement
     * @param count Count iteration amount (should match number of entity table
     *              columns)
     * @param faker Instance of Faker
     */
    public void makeEntry(PreparedStatement stmt, int count, Faker faker) throws SQLException;
}
