package com.mutant.godutch.model;

import java.util.List;

/**
 * Created by evanfang102 on 2017/3/30.
 */

public class Group {

    private String id;
    private String title;
    private String description;
    private String photoUrl;
    private int totalPaid;
    private List<Friend> friendsUid;

    public Group() {
    }

    public Group(String id, String title, String description, String photoUrl, int totalPaid, List<Friend> friendsUid) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.photoUrl = photoUrl;
        this.totalPaid = totalPaid;
        this.friendsUid = friendsUid;
    }

    public Group(String title, String description, String photoUrl, int totalPaid, List<Friend> friendsUid) {
        this(null, title, description, photoUrl, totalPaid, friendsUid);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getTotalPaid() {
        return totalPaid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Friend> getFriendsUid() {
        return friendsUid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
