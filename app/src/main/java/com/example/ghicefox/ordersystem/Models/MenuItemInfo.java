package com.example.ghicefox.ordersystem.Models;

public class MenuItemInfo {
    private String profile;
    private String picture;

    public MenuItemInfo(String profile, String picture) {
        this.profile = profile;
        this.picture = picture;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
