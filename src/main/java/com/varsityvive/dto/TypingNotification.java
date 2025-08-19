package com.varsityvive.dto;

public class TypingNotification {
    private Long senderId;
    private Long receiverId;
    private boolean typing;

    // Getters and setters
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    
    public boolean isTyping() { return typing; }
    public void setTyping(boolean typing) { this.typing = typing; }
}