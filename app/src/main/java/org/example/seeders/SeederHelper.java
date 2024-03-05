package org.example.seeders;

public class SeederHelper {
    public static final String USERS = "USERS";
    public static final String EMAILS = "EMAILS";
    public static final String ADDRESSES = "ADDRESSES";
    public static final String PHONENUMBERS = "PHONENUMBERS";

    public static final String[] USER_COLUMNS = new String[] { "firstname", "middlename", "lastname" };
    public static final String[] EMAIL_COLUMNS = new String[] { "email" };
    public static final String[] ADDRESS_COLUMNS = new String[] { "line1", "city", "stateprovince", "postalcode",
            "countryid" };
    public static final String[] PHONENUMBER_COLUMNS = new String[] { "phonenumber", "phonetype" };

    public static String[] getColumnsForEntity(String entityType) {
        switch (entityType) {
            case USERS:
                return USER_COLUMNS;
            case EMAILS:
                return EMAIL_COLUMNS;
            case ADDRESSES:
                return ADDRESS_COLUMNS;
            case PHONENUMBERS:
                return PHONENUMBER_COLUMNS;
            default:
                return USER_COLUMNS;
        }
    }
}
