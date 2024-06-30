package com.example.ichat;

public class Chat {
    private String chatName;
    private String lastMessage;
    private String timestamp;
    private String profileImageUrl;

    public Chat() {
        // Default constructor required for calls to DataSnapshot.getValue(Chat.class)
    }

    public Chat(String chatName, String lastMessage, String timestamp, String profileImageUrl) {
        this.chatName = chatName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.profileImageUrl = profileImageUrl;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
