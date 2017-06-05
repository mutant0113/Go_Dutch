package com.mutant.godutch.model;

import java.util.List;

/**
 * Created by evanfang102 on 2017/3/30.
 */

public class Group {

    private String id;
    private String title;
    private String description;
    private int totalPaid;
    private List<Friend> friendsUid;

    public Group() {
    }

    public Group(String id, String title, String description, int totalPaid, List<Friend> friendsUid) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.totalPaid = totalPaid;
        this.friendsUid = friendsUid;
    }

    public Group(String title, String description, int totalPay, List<Friend> friendsUid) {
        this(null, title, description, totalPay, friendsUid);
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
}
