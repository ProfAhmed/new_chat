package com.pro.ahmed.navigationdrawertest.model;

/**
 * Created by hp on 10/10/2017.
 */

public class Like {
    private String likeId;
    private String userId;
    private String videoId;

    public Like() {
    }

    public Like(String likeId, String userId,String videoId) {
        this.likeId = likeId;
        this.userId = userId;
        this.videoId = videoId;
    }

    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    @Override
    public String toString() {
        return "Like{" +
                "likeId='" + likeId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
