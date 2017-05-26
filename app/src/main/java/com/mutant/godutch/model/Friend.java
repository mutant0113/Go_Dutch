package com.mutant.godutch.model;

/**
 * Created by Mutant on 2017/5/26.
 */

public class Friend {

    String name;
    String proPicUrl;

    public Friend(String name, String proPicUrl) {
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
}
