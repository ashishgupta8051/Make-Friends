package com.social.makefriends.model;

public class UserPost {
    String postId;
    String PostType;
    String postImage;
    String PostCount;
    String UserId;

    public UserPost(){}

    public UserPost(String postId,String postType, String postImage, String postCount, String userId) {
        this.postId = postId;
        PostType = postType;
        this.postImage = postImage;
        PostCount = postCount;
        UserId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostType() {
        return PostType;
    }

    public void setPostType(String postType) {
        PostType = postType;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostCount() { return PostCount; }

    public void setPostCount(String postCount) { PostCount = postCount; }

    public String getUserId() { return UserId; }

    public void setUserId(String userId) { UserId = userId; }
}
