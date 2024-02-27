package org.example.objects;

public class User {
    private int id;
    private String firstname;
    private String lastname;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setFirstName(String name) {
        this.firstname = name;
    }

    public String getFirstName() {
        return this.firstname;
    }

    public void setLastName(String name) {
        this.lastname = name;
    }

    public String getLastName() {
        return this.lastname;
    }
}
