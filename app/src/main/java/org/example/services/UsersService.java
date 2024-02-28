package org.example.services;

import java.util.List;

import org.example.objects.User;
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

    public boolean createUser(User user) {
        return userRepo.createUser(user);
    }

    public String getUsers() {
        List<User> users = userRepo.find();
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

    private static interface UsersServiceHolder {
        public static final UsersService HOLDER_INSTANCE = new UsersService();
    }

}
