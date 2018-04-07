package com.haibeey.android.medmanager.model;

/**
 * Created by haibeey on 4/5/2018.
 */

public class User {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String ImagePath;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return getEmail()+" "+getFirstName()+" "+getImagePath()+" "+getLastName();
    }
}
