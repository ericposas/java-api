package org.example.objects;

import java.util.List;

import lombok.Data;

@Data
public class UsersAddressesDTO {
    public User user;
    public List<Address> addresses;
}
