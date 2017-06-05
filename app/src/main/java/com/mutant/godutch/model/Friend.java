package com.mutant.godutch.model;

/**
 * Created by Mutant on 2017/5/26.
 */

public class Friend {

    // TODO use unique_uid
    int uid;
    String name;
    String proPicUrl;
    int paid;
    int total;

    public Friend() {
    }

    public Friend(int uid, String name, String proPicUrl) {
        this.uid = uid;
        this.name = name;
        this.proPicUrl = proPicUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProPicUrl() {
        return proPicUrl;
    }

    public void setProPicUrl(String proPicUrl) {
        this.proPicUrl = proPicUrl;
    }

    public int getPaid() {
        return paid;
    }

    public void setPaid(int paid) {
        this.paid = paid;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
