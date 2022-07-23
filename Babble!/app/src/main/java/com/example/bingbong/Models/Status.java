package com.example.bingbong.Models;

public class Status {

    String imageurl;
    Long  timestamp;

    public Status() {
    }

    public Status(String imageurl, Long timestamp) {
        this.imageurl = imageurl;
        this.timestamp = timestamp;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
