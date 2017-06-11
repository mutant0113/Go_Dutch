package com.mutant.godutch.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.firebase.client.ServerValue;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;

/**
 * Created by evanfang102 on 2017/5/26.
 */

public class Event implements Parcelable {

    String id;
    String title;
    String description;
    List<Friend> friendsShared;
    Friend friendWhoPaidFirst;
    String photo;
    int subtotal;
    int tax;
    int total;
    HashMap<String, Object> timestamp;

    public Event() {
    }

    public Event(String id, String title, String description, List<Friend> friendsShared) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.friendsShared = friendsShared;
        this.timestamp = new HashMap<>();
        this.timestamp.put("timestamp", ServerValue.TIMESTAMP);
    }

    public Event(String title, String description, int subtotal, int tax, int total, List<Friend> friendsNeedToPay) {
        this.title = title;
        this.description = description;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
        this.friendsShared = friendsNeedToPay;
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

    public List<Friend> getFriendsShared() {
        return friendsShared;
    }

    public void setFriendsShared(List<Friend> friendsShared) {
        this.friendsShared = friendsShared;
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

    public Friend getFriendWhoPaidFirst() {
        return friendWhoPaidFirst;
    }

    public void setFriendWhoPaidFirst(Friend friendWhoPaidFirst) {
        this.friendWhoPaidFirst = friendWhoPaidFirst;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeTypedList(friendsShared);
        dest.writeParcelable(this.friendWhoPaidFirst, 0);
        dest.writeString(this.photo);
        dest.writeInt(this.subtotal);
        dest.writeInt(this.tax);
        dest.writeInt(this.total);
        dest.writeSerializable(this.timestamp);
    }

    protected Event(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.friendsShared = in.createTypedArrayList(Friend.CREATOR);
        this.friendWhoPaidFirst = in.readParcelable(Friend.class.getClassLoader());
        this.photo = in.readString();
        this.subtotal = in.readInt();
        this.tax = in.readInt();
        this.total = in.readInt();
        this.timestamp = (HashMap<String, Object>) in.readSerializable();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
