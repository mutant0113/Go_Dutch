package com.mutant.godutch.model;

import com.firebase.client.ServerValue;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;

/**
 * Created by evanfang102 on 2017/5/26.
 */

public class Event {

    String id;
    String title;
    String description;
    List<Friend> friends;
    String photo;
    int subtotal;
    int tax;
    int total;
    HashMap<String, Object> timestamp;

    public Event() {
    }

    public Event(String id, String title, String description, List<Friend> friendsNeedToPay) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.friends = friendsNeedToPay;
        this.timestamp = new HashMap<>();
        this.timestamp.put("timestamp", ServerValue.TIMESTAMP);
    }

    public Event(String title, String description, int subtotal, int tax, int total, List<Friend> friendsNeedToPay) {
        this.title = title;
        this.description = description;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
        this.friends = friendsNeedToPay;
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

    public int getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }

    public HashMap<String, Object> getTimestamp() {
        return timestamp;
    }

    @Exclude
    public long getTimestampCreated() {
        return (long) timestamp.get("timestamp");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTax() {
        return tax;
    }

    public int getTotal() {
        return total;
    }
}
