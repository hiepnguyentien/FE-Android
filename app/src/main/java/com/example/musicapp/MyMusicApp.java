package com.example.musicapp;

import android.app.Application;

public class MyMusicApp extends Application {
    private static String authToken;

    public static String getAuthToken() {
        return authToken;
    }

    public static void setAuthToken(String token) {
        authToken = token;
    }
}
