package com.social.makefriends.model;

public class Comments {

    String CommentId;
    String ProfilePic;
    String UserName;
    String CurrentDate;
    String CurrentTime;
    String PostComment;
    String CurrentUserId;
    String PostId;
    String PostCommentCount;
    String PostUserId;

    public Comments(){}

    public Comments(String commentId, String image, String name, String currentDate, String currentTime, String postComment,
                    String currentUserId, String postId, String postCommentCount, String postUserId) {
        CommentId = commentId;
        ProfilePic = image;
        UserName = name;
        CurrentDate = currentDate;
        CurrentTime = currentTime;
        PostComment = postComment;
        CurrentUserId = currentUserId;
        PostId = postId;
        PostCommentCount = postCommentCount;
        PostUserId = postUserId;
    }

    public String getCommentId() {
        return CommentId;
    }

    public void setCommentId(String commentId) {
        CommentId = commentId;
    }

    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
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

    public String getPostComment() {
        return PostComment;
    }

    public void setPostComment(String postComment) {
        PostComment = postComment;
    }

    public String getCurrentUserId() {
        return CurrentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        CurrentUserId = currentUserId;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }

    public String getPostCommentCount() { return PostCommentCount; }

    public void setPostCommentCount(String postCommentCount) { PostCommentCount = postCommentCount; }

    public String getPostUserId() {
        return PostUserId;
    }

    public void setPostUserId(String postUserId) {
        PostUserId = postUserId;
    }
}
