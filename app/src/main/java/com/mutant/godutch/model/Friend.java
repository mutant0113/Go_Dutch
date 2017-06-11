package com.mutant.godutch.model;

import android.support.annotation.IntDef;

/**
 * Created by Mutant on 2017/5/26.
 */

public class Friend {

    String uid;
    String name;
    String proPicUrl;
    int needToPay;

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

    public int getNeedToPay() {
        return needToPay;
    }

    public void setNeedToPay(int needToPay) {
        this.needToPay = needToPay;
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
