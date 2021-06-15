package com.social.makefriends.model;

public class ExistsChatUser {
    private String ExistsUserId;
    private String FirstPosition;

    public ExistsChatUser() {
    }

    public ExistsChatUser(String existsUserId, String firstPosition) {
        ExistsUserId = existsUserId;
        FirstPosition = firstPosition;
    }

    public String getExistsUserId() {
        return ExistsUserId;
    }

    public void setExistsUserId(String existsUserId) {
        ExistsUserId = existsUserId;
    }

    public String getFirstPosition() {
        return FirstPosition;
    }

    public void setFirstPosition(String firstPosition) {
        FirstPosition = firstPosition;
    }
}
