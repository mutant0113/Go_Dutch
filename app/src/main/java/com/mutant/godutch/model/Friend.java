package com.mutant.godutch.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

/**
 * Created by Mutant on 2017/5/26.
 */

public class Friend implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.name);
        dest.writeString(this.proPicUrl);
        dest.writeInt(this.needToPay);
        dest.writeInt(this.state);
    }

    protected Friend(Parcel in) {
        this.uid = in.readString();
        this.name = in.readString();
        this.proPicUrl = in.readString();
        this.needToPay = in.readInt();
        this.state = in.readInt();
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {
        public Friend createFromParcel(Parcel source) {
            return new Friend(source);
        }

        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };
}
