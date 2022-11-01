package com.android.gossipgeese.model;

public class RegisterModel {
    String id, name ,email, pass, image,dob,time;

    public RegisterModel() {
    }

    public RegisterModel(String id, String name, String email, String pass, String image, String dob,String time) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.image = image;
        this.dob = dob;
        this.time = time;
    }

    public RegisterModel(String id, String name, String email, String image,String time) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.image = image;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}