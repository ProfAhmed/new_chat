package com.pro.ahmed.navigationdrawertest.model;

/**
 * Created by hp on 05/10/2017.
 */

public class Video {
    private String id;
    private String userId;
    private String videoPath;

    public Video() {
    }

    public Video(String id, String userId, String videoPath) {
        this.id = id;
        this.userId = userId;
        this.videoPath = videoPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id='" + id + '\'' +
                ", userID=" + userId +
                ", videoPath=" + videoPath +
                '}';
    }
}
