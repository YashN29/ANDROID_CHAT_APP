package com.example.bingbong.Models;

import com.google.android.material.textfield.TextInputLayout;

public class User_Model {

    String uid;
    String name;
    String phonenumber;
    String profileImage;
    String token;

    public User_Model() {
    }

    public User_Model(String uid, String name, String phonenumber, String profileImage) {
        this.uid = uid;
        this.name = name;
        this.phonenumber = phonenumber;
        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
