package org.example.seeders;

import org.example.seeders.makers.AddressMaker;
import org.example.seeders.makers.BaseMaker;
import org.example.seeders.makers.EmailMaker;
import org.example.seeders.makers.IRowEntryMaker;
import org.example.seeders.makers.PhoneNumberMaker;
import org.example.seeders.makers.UserMaker;

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

    public static IRowEntryMaker getMaker(String tableName) {
        if (tableName.equals(Constants.USERS)) {
            return new UserMaker();
        }
        if (tableName.equals(Constants.ADDRESSES)) {
            return new AddressMaker();
        }
        if (tableName.equals(Constants.EMAILS)) {
            return new EmailMaker();
        }
        if (tableName.equals(Constants.PHONENUMBERS)) {
            return new PhoneNumberMaker();
        }
        return new BaseMaker<>();
    }
}
