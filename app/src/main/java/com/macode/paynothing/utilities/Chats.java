package com.macode.paynothing.utilities;

public class Chats {

    private String message, userId, dateMessageSent;
    private Boolean seen;

    public Chats() {
    }

    public Chats(String message, String userId, String dateMessageSent, Boolean seen) {
        this.message = message;
        this.userId = userId;
        this.dateMessageSent = dateMessageSent;
        this.seen = seen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDateMessageSent() {
        return dateMessageSent;
    }

    public void setDateMessageSent(String dateMessageSent) {
        this.dateMessageSent = dateMessageSent;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }
}
