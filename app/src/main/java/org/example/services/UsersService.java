package org.example.services;

import java.util.List;

import org.example.objects.User;
import org.example.objects.UsersDetailsDTO;
import org.example.repos.UserRepo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class UsersService {

    private ObjectWriter objWriter;

    private UserRepo userRepo = UserRepo.getRepositoryInstance();

    public UsersService() {
        objWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    // Singleton pattern to only return one instantiation of the service
    public static UsersService getInstance() {
        return UsersServiceHolder.HOLDER_INSTANCE;
    }

    public boolean createUser(String payload) {
        ObjectMapper om = new ObjectMapper();
        try {
            User user = om.readValue(payload, User.class);
            return userRepo.createOne(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUsers() {
        List<User> users = userRepo.findAll();
        String usersToString = "";
        try {
            usersToString = objWriter.writeValueAsString(users);
        } catch (JsonProcessingException e) {
            System.err.println("could not translate User Objects to JSON");
        }
        return usersToString;
    }

    public String getUser(int id) {
        User user = userRepo.findOne(id);
        String userToString = "";
        try {
            userToString = objWriter.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            System.err.println("could not translate User Object to JSON");
        }
        return userToString;
    }

    public boolean deleteUser(int id) {
        return userRepo.deleteOne(id);
    }

    public String fetchUsersDetails(int id) {
        UsersDetailsDTO usersDetails = userRepo.findUserDetails(id);
        String usersDetailsToString = "";
        try {
            usersDetailsToString = objWriter.writeValueAsString(usersDetails);
        } catch (JsonProcessingException e) {
            System.err.println("Could not translate UserDetailsDTO object to JSON");
        }
        return usersDetailsToString;
    }

    private static interface UsersServiceHolder {
        public static final UsersService HOLDER_INSTANCE = new UsersService();
    }

}
