package com.social.makefriends.model;

import java.util.ArrayList;

public class UserStatus {
    private String userName;
    private String name;
    private String profileImage;
    private String userId;
    private long lastUpdate;
    private ArrayList<Status> statusList;

    public UserStatus() {
    }

    public UserStatus(String userName,String name,String profileImage, String userId, long lastUpdate, ArrayList<Status> statusList) {
        this.userName = userName;
        this.name = name;
        this.profileImage = profileImage;
        this.userId = userId;
        this.lastUpdate = lastUpdate;
        this.statusList = statusList;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public ArrayList<Status> getStatusList() {
        return statusList;
    }

    public void setStatusList(ArrayList<Status> statusList) {
        this.statusList = statusList;
    }
}
