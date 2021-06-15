package com.social.friends.model;

public class SearchFriendHistory {
    String Search_Name;
    String Search_UserName;
    String Search_ProfilePic;
    String Search_UserId;
    String TotalCount;

    public SearchFriendHistory() {
    }

    public SearchFriendHistory(String search_Name, String search_UserName, String search_ProfilePic, String search_UserId,String totalCount) {
        Search_Name = search_Name;
        Search_UserName = search_UserName;
        Search_ProfilePic = search_ProfilePic;
        Search_UserId = search_UserId;
        TotalCount = totalCount;
    }

    public String getSearch_Name() {
        return Search_Name;
    }

    public void setSearch_Name(String search_Name) {
        Search_Name = search_Name;
    }

    public String getSearch_UserName() {
        return Search_UserName;
    }

    public void setSearch_UserName(String search_UserName) {
        Search_UserName = search_UserName;
    }

    public String getSearch_ProfilePic() {
        return Search_ProfilePic;
    }

    public void setSearch_ProfilePic(String search_ProfilePic) { Search_ProfilePic = search_ProfilePic; }

    public String getSearch_UserId() {
        return Search_UserId;
    }

    public void setSearch_UserId(String search_UserId) {
        Search_UserId = search_UserId;
    }

    public String getTotalCount() { return TotalCount; }

    public void setTotalCount(String totalCount) { TotalCount = totalCount; }
}
