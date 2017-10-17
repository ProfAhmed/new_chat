package com.pro.ahmed.navigationdrawertest.model;

/**
 * Created by hp on 12/09/2017.
 */

public class UserDetails {
    UserDetails userDetails;
    private String curr_id;
    private String userName;
    private String email;
    private String password;
    private String sex;
    private String birthDay;
    private String userId;
    private String userPhoto;
    private float latitude;
    private float longitude;
    private String address;
    private String status;
    private String userState;
    private float distance;

    public UserDetails() {

    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public UserDetails(String uri, String birthDay, String sex, String password, String email, String userName, String userId) {
        this.birthDay = birthDay;
        this.sex = sex;
        this.password = password;
        this.email = email;
        this.userName = userName;
        this.userPhoto = uri;
        this.userId = userId;
    }

    public UserDetails(String userName, String userPhoto, String userId, String curr_id, String userState, String status) {
        this.userName = userName;
        this.userPhoto = userPhoto;
        this.userId = userId;
        this.curr_id = curr_id;
        this.userState = userState;
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }


    public String getUserId() {
        return userId;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getStatus() {
        return status;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public String getCurr_id() {
        return curr_id;
    }

    public void setCurr_id(String curr_id) {
        this.curr_id = curr_id;
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", sex='" + sex + '\'' +
                ", birthDay='" + birthDay + '\'' +
                ", userID=" + userId +
                ", userPhoto=" + userPhoto +
                '}';
    }
}
