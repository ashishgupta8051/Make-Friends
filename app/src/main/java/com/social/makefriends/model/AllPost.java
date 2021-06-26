package com.social.makefriends.model;

public class AllPost {
    String Key;
    String UserName;
    String UserProfilePic;
    String CurrentDate;
    String CurrentTime;
    String PostImage;
    String Caption;
    String CurrentUserId;
    String CountPost;
    String UsersName;

    public AllPost(){}

    public AllPost(String key, String userName, String userProfilePic, String currentDate, String currentTime, String postImage,
                   String caption, String currentUserId, String countPost, String usersName) {
        Key = key;
        UserName = userName;
        UserProfilePic = userProfilePic;
        CurrentDate = currentDate;
        CurrentTime = currentTime;
        PostImage = postImage;
        Caption = caption;
        CurrentUserId = currentUserId;
        CountPost = countPost;
        UsersName = usersName;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserProfilePic() {
        return UserProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        UserProfilePic = userProfilePic;
    }

    public String getCurrentDate() {
        return CurrentDate;
    }

    public void setCurrentDate(String currentDate) {
        CurrentDate = currentDate;
    }

    public String getCurrentTime() {
        return CurrentTime;
    }

    public void setCurrentTime(String currentTime) {
        CurrentTime = currentTime;
    }

    public String getPostImage() {
        return PostImage;
    }

    public void setPostImage(String postImage) {
        PostImage = postImage;
    }

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String caption) {
        Caption = caption;
    }

    public String getCurrentUserId() { return CurrentUserId; }

    public void setCurrentUserId(String currentUserId) { CurrentUserId = currentUserId; }

    public String getCountPost() { return CountPost; }

    public void setCountPost(String countPost) { CountPost = countPost; }

    public String getUsersName() { return UsersName; }

    public void setUsersName(String usersName) { UsersName = usersName; }
}
