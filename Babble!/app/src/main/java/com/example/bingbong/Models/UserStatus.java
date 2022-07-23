package com.example.bingbong.Models;

import java.util.ArrayList;

public class UserStatus {

    String name;
    String profileimage;
    Long lastupdated;
    ArrayList<Status> statuses;

    public UserStatus() {
    }

    public UserStatus(String name, String profileimage, Long lastupdated, ArrayList<Status> statuses) {
        this.name = name;
        this.profileimage = profileimage;
        this.lastupdated = lastupdated;
        this.statuses = statuses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public Long getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(Long lastupdated) {
        this.lastupdated = lastupdated;
    }

    public ArrayList<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }
}
