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
    private List<Friend> friends;

    public Group() {
    }

    public Group(String id, String title, String description, String photoUrl, int totalPaid, List<Friend> friends) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.photoUrl = photoUrl;
        this.totalPaid = totalPaid;
        this.friends = friends;
    }

    public Group(String title, String description, String photoUrl, int totalPaid, List<Friend> friends) {
        this(null, title, description, photoUrl, totalPaid, friends);
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

    public List<Friend> getFriends() {
        return friends;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
