package com.android.gossipgeese.model;

import java.io.Serializable;

public class CurrentUserModel implements Serializable {
    public String firstName, lastName, email, token, id;

    public CurrentUserModel() {
    }

    public CurrentUserModel(String firstName, String lastName, String email, String token) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.token = token;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
