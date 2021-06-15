package com.social.friends.model;

public class Request {
    String request_type;
    String senderUid;

    public Request() {
    }

    public Request(String request_type, String senderUid) {
        this.request_type = request_type;
        this.senderUid = senderUid;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String getSenderUid() { return senderUid; }

    public void setSenderUid(String senderUid) { this.senderUid = senderUid; }
}
