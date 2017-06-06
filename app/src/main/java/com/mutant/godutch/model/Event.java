package com.mutant.godutch.model;

import com.firebase.client.ServerValue;
import com.google.firebase.database.Exclude;

import java.util.List;
import java.util.Map;

/**
 * Created by evanfang102 on 2017/5/26.
 */

public class Event {

    String title;
    String description;
    List<Friend> friends;
    String photo;
    int totalPaid;
    Map<String, String> timestamp;

    public Event() {
    }

    public Event(String title, String description, List<Friend> friendWithPays) {
        this.title = title;
        this.description = description;
        this.friends = friendWithPays;
        this.timestamp = ServerValue.TIMESTAMP;
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

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

    @Exclude
    public Map<String, String> getTimestamp() {
        return timestamp;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(int totalPaid) {
        this.totalPaid = totalPaid;
    }

}
