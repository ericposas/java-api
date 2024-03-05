package org.example.seeders;

public class SeederHelper {
    public static String[] getColumnsForEntity(String entityType) {
        switch (entityType) {
            case Constants.USERS:
                return Constants.USER_COLUMNS;
            case Constants.EMAILS:
                return Constants.EMAIL_COLUMNS;
            case Constants.ADDRESSES:
                return Constants.ADDRESS_COLUMNS;
            case Constants.PHONENUMBERS:
                return Constants.PHONENUMBER_COLUMNS;
            default:
                return Constants.USER_COLUMNS;
        }
    }
}
