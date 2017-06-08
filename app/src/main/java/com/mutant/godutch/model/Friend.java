package com.mutant.godutch.model;

import android.support.annotation.IntDef;

/**
 * Created by Mutant on 2017/5/26.
 */

public class Friend {

    String uid;
    String name;
    String proPicUrl;
    int paid;
    int total;

    public static final int STATE_ACCEPTED = 0;
    public static final int STATE_NOT_BE_ACCEPTED = 1;
    public static final int STATE_BE_INVITED = 2;
    int state;

    public Friend() {
    }

    public Friend(String uid, String name, String proPicUrl) {
        this.uid = uid;
        this.name = name;
        this.proPicUrl = proPicUrl;
    }

    public String getName() {
        return name != null ? name : "";
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @IntDef({STATE_ACCEPTED, STATE_NOT_BE_ACCEPTED, STATE_BE_INVITED})
    public @interface State {
    }

    public void setState(@State int state) {
        this.state = state;
    }

    @State
    public int getState() {
        return state;
    }
}
