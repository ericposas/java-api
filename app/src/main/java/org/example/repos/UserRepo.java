package org.example.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.example.database.DB;
import org.example.objects.Address;
import org.example.objects.Emailaddress;
import org.example.objects.Phonenumber;
import org.example.objects.User;
import org.example.objects.UsersAddressesDTO;
import org.example.objects.UsersDetailsDTO;

public class UserRepo {

    private Connection db = DB.getDbConnection();

    public boolean createOne(User user) {
        try {
            db = DB.connect();
            if (user.getFirstname() != null && user.getLastname() != null) {
                PreparedStatement stmt = db.prepareStatement("INSERT INTO USERS (firstname, lastname) VALUES (?,?);");
                stmt.setString(1, user.getFirstname());
                stmt.setString(2, user.getLastname());
                stmt.executeUpdate();
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteOne(int id) {
        try {
            db = DB.connect();
            PreparedStatement stmt = db.prepareStatement("DELETE FROM USERS WHERE id = ?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User findOne(int id) {
        User user = new User();
        try {
            db = DB.connect();
            PreparedStatement stmt = db.prepareStatement("SELECT * FROM Users WHERE id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                user.setId(rs.getInt("id"));
                user.setFirstname(rs.getString("firstname"));
                user.setLastname(rs.getString("lastname"));
            }
            db.close();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return user;
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try {
            db = DB.connect();
            Statement stmt = db.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM Users;");
            while (result.next()) {
                User user = new User();
                user.setId(result.getInt("id"));
                user.setFirstname(result.getString("firstname"));
                user.setLastname(result.getString("lastname"));
                users.add(user);
            }
            db.close();
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            return users;
        }
    }

    public UsersAddressesDTO findAddressesForUser(int id) {
        UsersAddressesDTO usersaddresses = new UsersAddressesDTO();
        try {
            db = DB.connect();
            String query = "select u.*, a.*, ua.*\r\n" + //
                    "from users u\r\n" + //
                    "join usersaddresses ua on ua.user_id = u.id\r\n" + //
                    "join addresses a on a.id = ua.address_id\r\n" + //
                    "where u.id = ?";
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            User user = new User();
            List<Address> addresses = new ArrayList<>();
            while (result.next()) {
                Address address = new Address();
                address.setId(result.getInt("address_id"));
                address.setIsprimary(result.getBoolean("isprimary"));
                address.setLine1(result.getString("line1"));
                address.setLine2(result.getString("line2"));
                address.setCity(result.getString("city"));
                address.setStateprovince(result.getString("stateprovince"));
                address.setPostalcode(result.getString("postalcode"));
                address.setCountryid(result.getString("countryid"));
                addresses.add(address);
                if (user.getFirstname() == null) {
                    user.setId(result.getInt("user_id"));
                    user.setFirstname(result.getString("firstname"));
                    user.setLastname(result.getString("lastname"));
                }
            }
            usersaddresses.setAddresses(addresses);
            usersaddresses.setUser(user);
            return usersaddresses;
        } catch (SQLException e) {
            e.printStackTrace();
            return usersaddresses;
        }
    }

    public UsersDetailsDTO findUserDetails(int id) {
        UsersDetailsDTO userDetails = new UsersDetailsDTO();
        try {
            db = DB.connect();
            User user = new User();
            String userQuery = "select * from users where id = ?";
            PreparedStatement uStmt = db.prepareStatement(userQuery);
            uStmt.setInt(1, id);
            ResultSet uRs = uStmt.executeQuery();
            while (uRs.next()) {
                user.setId(uRs.getInt("id"));
                user.setFirstname(uRs.getString("firstname"));
                user.setLastname(uRs.getString("lastname"));
                user.setMiddlename(uRs.getString("middlename"));
            }
            String addressQuery = "select u.*, ua.*, a.* from users u join usersaddresses ua on ua.user_id = u.id join addresses a on a.id = ua.address_id where u.id = ?";
            PreparedStatement stmt = db.prepareStatement(addressQuery);
            stmt.setInt(1, id);
            ResultSet addressRs = stmt.executeQuery();
            List<Address> addresses = new ArrayList<>();
            while (addressRs.next()) {
                Address address = new Address();
                address.setId(addressRs.getInt("address_id"));
                address.setIsprimary(addressRs.getBoolean("isprimary"));
                address.setLine1(addressRs.getString("line1"));
                address.setLine2(addressRs.getString("line2"));
                address.setCity(addressRs.getString("city"));
                address.setStateprovince(addressRs.getString("stateprovince"));
                address.setPostalcode(addressRs.getString("postalcode"));
                address.setCountryid(addressRs.getString("countryid"));
                addresses.add(address);
            }
            String phoneQuery = "select u.*, up.*, p.* from users u join usersphonenumbers up on up.user_id = u.id left join phonenumbers p on p.id = up.phonenumber_id where u.id = ?;";
            PreparedStatement pStmt = db.prepareStatement(phoneQuery);
            pStmt.setInt(1, id);
            ResultSet pRs = pStmt.executeQuery();
            List<Phonenumber> numbers = new ArrayList<>();
            while (pRs.next()) {
                Phonenumber phone = new Phonenumber();
                phone.setId(pRs.getInt("phonenumber_id"));
                phone.setIsprimary(pRs.getBoolean("isprimary"));
                phone.setPhonenumber(pRs.getString("phonenumber"));
                phone.setPhonetype(pRs.getString("phonetype"));
                numbers.add(phone);
            }
            String emailQuery = "select e.*, ue.*, u.* from users u join usersemails ue on ue.user_id = u.id join emails e on e.id = ue.email_id where u.id = ?;";
            PreparedStatement eStmt = db.prepareStatement(emailQuery);
            eStmt.setInt(1, id);
            ResultSet eRs = eStmt.executeQuery();
            List<Emailaddress> emailaddresses = new ArrayList<>();
            while (eRs.next()) {
                Emailaddress email = new Emailaddress();
                email.setId(eRs.getInt("email_id"));
                email.setIsprimary(eRs.getBoolean("isprimary"));
                email.setEmail(eRs.getString("email"));
                emailaddresses.add(email);
            }
            userDetails.setAddresses(addresses);
            userDetails.setPhonenumbers(numbers);
            userDetails.setEmailaddresses(emailaddresses);
            userDetails.setUser(user);
            return userDetails;
        } catch (SQLException e) {
            e.printStackTrace();
            return userDetails;
        }
    }

    public static UserRepo getRepositoryInstance() {
        return UserRepositoryHolder.USER_REPOSITORY_HOLDER;
    }

    private static interface UserRepositoryHolder {
        public static UserRepo USER_REPOSITORY_HOLDER = new UserRepo();
    }

}
