package com.social.makefriends.model;

public class FavPost {

    String favPostId;

    public FavPost() {
    }

    public FavPost(String favPostId) {
        this.favPostId = favPostId;
    }

    public String getFavPostId() {
        return favPostId;
    }

    public void setFavPostId(String favPostId) {
        this.favPostId = favPostId;
    }

}
