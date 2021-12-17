package com.social.makefriends.model;

public class Status {
    private String statusUrl;
    private Boolean seen;
    private Boolean userSeen;
    private String statusId;
    private long lastUpdate;

    public Status() {

    }

    public Status(String statusUrl, Boolean seen, Boolean userSeen, String statusId, long lastUpdate) {
        this.statusUrl = statusUrl;
        this.seen = seen;
        this.userSeen = userSeen;
        this.statusId = statusId;
        this.lastUpdate = lastUpdate;
    }

    public String getStatusUrl() {
        return statusUrl;
    }

    public void setStatusUrl(String statusUrl) {
        this.statusUrl = statusUrl;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Boolean getUserSeen() {
        return userSeen;
    }

    public void setUserSeen(Boolean userSeen) {
        this.userSeen = userSeen;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
