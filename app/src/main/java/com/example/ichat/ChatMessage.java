// ChatMessage.java
package com.example.ichat;

public class ChatMessage {
    private String userId;
    private String username;
    private String userPhoto;
    private String message;
    private long timestamp;

    public ChatMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(ChatMessage.class)
    }

    public ChatMessage(String userId, String username, String userPhoto, String message) {
        this.userId = userId;
        this.username = username;
        this.userPhoto = userPhoto;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
