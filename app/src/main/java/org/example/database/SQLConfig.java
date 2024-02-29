package org.example.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SQLConfig {

    public static Properties properties;

    static {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("/home/eric/testproject/app/src/main/resources/sql.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load sql.properties file");
        }
    }

    public static String getTableGenFilePath() {
        return properties.getProperty("sql.tablegenfilepath");
    }

}
