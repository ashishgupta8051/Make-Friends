package com.social.friends.model;

public class Friends {
    String status;
    String friendUid;
    String friend_name;
    String friend_userName;

    public Friends() {
    }

    public Friends(String status, String friendUid, String friend_name, String friend_userName) {
        this.status = status;
        this.friendUid = friendUid;
        this.friend_name = friend_name;
        this.friend_userName = friend_userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFriendUid() {
        return friendUid;
    }

    public void setFriendUid(String friendUid) {
        this.friendUid = friendUid;
    }

    public String getFriend_name() { return friend_name; }

    public void setFriend_name(String friend_name) { this.friend_name = friend_name; }

    public String getFriend_userName() { return friend_userName; }

    public void setFriend_userName(String friend_userName) { this.friend_userName = friend_userName; }
}
