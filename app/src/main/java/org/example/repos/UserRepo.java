package org.example.repos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.example.database.DB;
import org.example.objects.User;

public class UserRepo {

    private Connection db = DB.getDbConnection();

    public boolean createUser(User user) {
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

    public User findOne(int id) {
        User user = new User();
        try {
            db = DB.connect();
            PreparedStatement stmt = db.prepareStatement(
                    "SELECT * FROM Users WHERE id = ?");
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

    public List<User> find() {
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
            System.err.println(e.getMessage());
            List<User> emptyList = new ArrayList<>();
            return emptyList;
        }
    }

    public static UserRepo getRepositoryInstance() {
        return UserRepositoryHolder.USER_REPOSITORY_HOLDER;
    }

    private static interface UserRepositoryHolder {
        public static UserRepo USER_REPOSITORY_HOLDER = new UserRepo();
    }

}
