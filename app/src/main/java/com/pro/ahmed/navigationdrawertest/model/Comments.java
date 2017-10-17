package com.pro.ahmed.navigationdrawertest.model;

/**
 * Created by hp on 10/10/2017.
 */
public class Comments {
    private String commentId;
    private String commentString;
    private String userId;
    private String videoId;

    public Comments() {
    }

    public Comments(String commentId, String commentString, String userId, String videoId) {
        this.commentId = commentId;
        this.commentString = commentString;
        this.userId = userId;
        this.videoId = videoId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentString() {
        return commentString;
    }

    public void setCommentString(String commentString) {
        this.commentString = commentString;
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
        return "Comments{" +
                "commentId='" + commentId + '\'' +
                ", commentString='" + commentString + '\'' +
                ", userId='" + userId + '\'' +
                ", videoId='" + videoId + '\'' +
                '}';
    }

}
