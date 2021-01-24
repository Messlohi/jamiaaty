package com.example.jamiaaty;

public class NotificationModel {

    String fromName, message,fromId;

    public NotificationModel(String fromName, String message, String fromId) {
        this.fromName = fromName;
        this.message = message;
        this.fromId = fromId;
    }

    public NotificationModel() {
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
