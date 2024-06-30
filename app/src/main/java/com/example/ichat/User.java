package com.example.ichat;

public class User {
    private String email;
    private String displayName;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String email, String displayName) {
        this.email = email;
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
