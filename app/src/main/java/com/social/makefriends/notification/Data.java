package com.social.makefriends.notification;

public class Data {
    private String senderNotificationUserId;
    private String notificationBody;
    private String notificationTitle;
    private String receiverNotificationUserId;
    private String notificationType; //Like Chatting Type,Request Type

    public Data() {
    }

    public Data(String senderNotificationUserId, String notificationBody, String notificationTitle, String receiverNotificationUserId, String notificationType) {
        this.senderNotificationUserId = senderNotificationUserId;
        this.notificationBody = notificationBody;
        this.notificationTitle = notificationTitle;
        this.receiverNotificationUserId = receiverNotificationUserId;
        this.notificationType = notificationType;
    }

    public String getSenderNotificationUserId() {
        return senderNotificationUserId;
    }

    public void setSenderNotificationUserId(String senderNotificationUserId) {
        this.senderNotificationUserId = senderNotificationUserId;
    }

    public String getNotificationBody() {
        return notificationBody;
    }

    public void setNotificationBody(String notificationBody) {
        this.notificationBody = notificationBody;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getReceiverNotificationUserId() {
        return receiverNotificationUserId;
    }

    public void setReceiverNotificationUserId(String receiverNotificationUserId) {
        this.receiverNotificationUserId = receiverNotificationUserId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}

