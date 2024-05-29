package com.example.musicapp.models;

public class AuthResponse {
    private boolean isSucceed;
    private String data;

    public boolean isSucceed() {
        return isSucceed;
    }

    public void setSucceed(boolean succeed) {
        isSucceed = succeed;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}