package com.repositories;

public class AuthorizedUser {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String token;

    public AuthorizedUser(Integer id, String username, String password, String email, String token) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.token = token;
    }

    public AuthorizedUser() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
