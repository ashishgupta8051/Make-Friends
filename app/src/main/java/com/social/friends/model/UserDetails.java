package com.social.friends.model;

public class UserDetails {
    String UserName;
    String UserEmail;
    String UserDob;
    String UserAddress;
    String UserBio;
    String UserProfileImageUrl;
    String UsersName;
    String UserValue;
    String UserUid;
    String OnlineDate;
    String OnlineTime;
    String OnlineStatus;
    String ChatBackgroundWall;
    String UserPassword;

    public UserDetails(){}

    public UserDetails(String userName, String userEmail, String userDob, String userAddress, String userBio,String userProfileImageUrl,
                       String usersName, String userValue, String userUid, String onlineDate, String onlineTime, String onlineStatus,
                       String chatBackgroundWall, String userPassword) {
        UserName = userName;
        UserEmail = userEmail;
        UserDob = userDob;
        UserAddress = userAddress;
        UserBio = userBio;
        UserProfileImageUrl = userProfileImageUrl;
        UsersName = usersName;
        UserValue = userValue;
        UserUid = userUid;
        OnlineDate = onlineDate;
        OnlineTime = onlineTime;
        OnlineStatus = onlineStatus;
        ChatBackgroundWall = chatBackgroundWall;
        UserPassword = userPassword;
    }

    public String getUserName() { return UserName; }

    public void setUserName(String userName) { UserName = userName; }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getUserDob() {
        return UserDob;
    }

    public void setUserDob(String userDob) {
        UserDob = userDob;
    }

    public String getUserAddress() {
        return UserAddress;
    }

    public void setUserAddress(String userAddress) {
        UserAddress = userAddress;
    }

    public String getUserBio() {
        return UserBio;
    }

    public void setUserBio(String userBio) {
        UserBio = userBio;
    }

    public String getUserProfileImageUrl() {
        return UserProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) { UserProfileImageUrl = userProfileImageUrl; }

    public String getUsersName() { return UsersName; }

    public void setUsersName(String usersName) { UsersName = usersName; }

    public String getUserValue() { return UserValue; }

    public void setUserValue(String userValue) { UserValue = userValue; }

    public String getUserUid() { return UserUid; }

    public void setUserUid(String userUid) { UserUid = userUid; }

    public String getOnlineDate() {
        return OnlineDate;
    }

    public void setOnlineDate(String onlineDate) {
        OnlineDate = onlineDate;
    }

    public String getOnlineTime() {
        return OnlineTime;
    }

    public void setOnlineTime(String onlineTime) {
        OnlineTime = onlineTime;
    }

    public String getOnlineStatus() {
        return OnlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        OnlineStatus = onlineStatus;
    }

    public String getChatBackgroundWall() {
        return ChatBackgroundWall;
    }

    public void setChatBackgroundWall(String chatBackgroundWall) {
        ChatBackgroundWall = chatBackgroundWall;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String userPassword) {
        UserPassword = userPassword;
    }
}
