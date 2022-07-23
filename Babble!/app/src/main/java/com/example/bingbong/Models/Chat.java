package com.example.bingbong.Models;

public class Chat {

    String messages;
    String messageId;
    String sender;
    String imageUrl;

    long timestamp;
    long reaction = -1;

    public Chat() {
    }

    public Chat(String messages, String sender, long timestamp) {
        this.messages = messages;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getReaction() {
        return reaction;
    }

    public void setReaction(long reaction) {
        this.reaction = reaction;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
