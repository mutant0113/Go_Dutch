package com.mutant.godutch.model;

import java.util.List;

/**
 * Created by evanfang102 on 2017/5/26.
 */

public class Event {

    String title;
    String description;
    List<FriendWithPay> friendWithPays;

    public Event(String title, String description, List<FriendWithPay> friendWithPays) {
        this.title = title;
        this.description = description;
        this.friendWithPays = friendWithPays;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FriendWithPay> getFriendWithPays() {
        return friendWithPays;
    }

    public void setFriendWithPays(List<FriendWithPay> friendWithPays) {
        this.friendWithPays = friendWithPays;
    }
}
