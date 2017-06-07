package com.mutant.godutch.model;

import com.firebase.client.ServerValue;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;

/**
 * Created by evanfang102 on 2017/5/26.
 */

public class Event {

    String title;
    String description;
    List<Friend> friends;
    String photo;
    int totalPaid;
    HashMap<String, Object> timestamp;

    public Event() {
    }

    public Event(String title, String description, List<Friend> friendsWhoPaid) {
        this.title = title;
        this.description = description;
        this.friends = friendsWhoPaid;
        this.timestamp = new HashMap<>();
        this.timestamp.put("timestamp", ServerValue.TIMESTAMP);
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

    public HashMap<String, Object> getTimestamp() {
        return timestamp;
    }

    @Exclude
    public long getTimestampCreated() {
        return (long) timestamp.get("timestamp");
    }

}
