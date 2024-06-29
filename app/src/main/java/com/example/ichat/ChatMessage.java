package com.example.ichat;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ChatMessage {
    private String userId;
    private String username;
    private String userPhoto;
    private String message;
    @ServerTimestamp
    private Date timestamp;

    public ChatMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(ChatMessage.class)
    }

    public ChatMessage(String userId, String username, String userPhoto, String message) {
        this.userId = userId;
        this.username = username;
        this.userPhoto = userPhoto;
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
