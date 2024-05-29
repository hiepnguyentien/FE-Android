package com.example.musicapp.models;
public class AuthResponse {
    private boolean succeed;
    private String data;  // This is the token
    private int userId;   // Add this field to store userId

    // Add getter and setter for userId
    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
