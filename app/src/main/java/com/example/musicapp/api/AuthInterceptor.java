package com.example.musicapp.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.musicapp.MyMusicApp;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private SharedPreferences sharedPreferences;

    // Constructor to get SharedPreferences
    public AuthInterceptor(Context context) {
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // Get the original request
        Request originalRequest = chain.request();

        // Retrieve the token from SharedPreferences
        String token;
        if (sharedPreferences.getString("token", null) != null){
            token = sharedPreferences.getString("token", null);
        } else {
            token = MyMusicApp.getAuthToken();
        }

        // If there's no token, proceed with the original request
        if (token == null) {
            return chain.proceed(originalRequest);
        }

        // Add the Authorization header with the token
        Request.Builder builder = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token);

        // Build the new request
        Request newRequest = builder.build();

        // Proceed with the new request
        return chain.proceed(newRequest);
    }
}