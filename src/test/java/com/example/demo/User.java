package com.example.demo;

public class User {
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private int userId;
    private String lastName;
    private String firstName;
    private String email;

    public User(int userid, String lastname, String firstname, String email) {
        this.userId = userid;
        this.lastName = lastname;
        this.firstName = firstname;
        this.email = email;
    }
}
