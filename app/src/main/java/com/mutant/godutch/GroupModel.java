package com.mutant.godutch;

/**
 * Created by evanfang102 on 2017/3/30.
 */

public class GroupModel {

    private String title;
    private String description;
    private int totalPay;
    private String[] friendsId;

    public GroupModel(String title, String description, int totalPay, String[] friendsId) {
        this.title = title;
        this.description = description;
        this.totalPay = totalPay;
        this.friendsId = friendsId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getTotalPay() {
        return totalPay;
    }

    public String[] getFriendsId() {
        return friendsId;
    }
}
