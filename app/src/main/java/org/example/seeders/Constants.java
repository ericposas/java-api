package org.example.seeders;

public class Constants {
    public final static String HOME = "HOME";
    public final static String WORK = "WORK";
    public final static String MOBILE = "MOBILE";
    public final static String[] PHONE_TYPES = new String[] { Constants.MOBILE, Constants.HOME, Constants.WORK };

    public static final String USERS = "USERS";
    public static final String EMAILS = "EMAILS";
    public static final String ADDRESSES = "ADDRESSES";
    public static final String PHONENUMBERS = "PHONENUMBERS";

    public static final String[] USER_COLUMNS = new String[] { "firstname", "middlename", "lastname" };
    public static final String[] EMAIL_COLUMNS = new String[] { "email" };
    public static final String[] ADDRESS_COLUMNS = new String[] { "line1", "city", "stateprovince", "postalcode",
            "countryid" };
    public static final String[] PHONENUMBER_COLUMNS = new String[] { "phonenumber", "phonetype" };
}
