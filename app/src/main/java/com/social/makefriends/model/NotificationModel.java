package com.social.makefriends.model;

public class NotificationModel {
    private String SenderName;
    private String notificationMsg;

    public NotificationModel() {
    }

    public NotificationModel(String senderName, String notificationMsg) {
        SenderName = senderName;
        this.notificationMsg = notificationMsg;
    }

    public String getSenderName() {
        return SenderName;
    }

    public void setSenderName(String senderName) {
        SenderName = senderName;
    }

    public String getNotificationMsg() {
        return notificationMsg;
    }

    public void setNotificationMsg(String notificationMsg) {
        this.notificationMsg = notificationMsg;
    }
}
