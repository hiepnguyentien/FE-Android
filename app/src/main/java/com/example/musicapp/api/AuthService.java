package com.example.musicapp.api;

import com.example.musicapp.models.AuthResponse;
import com.example.musicapp.models.SignInModel;
import com.example.musicapp.models.SignUpModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create();
    AuthService authService = new Retrofit.Builder()
            .baseUrl("http://192.168.1.34:5271/auth/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(AuthService.class);

    @POST("signin")
    Call<AuthResponse> login(@Body SignInModel loginRequest);

    @POST("signup")
    Call<AuthResponse> register(@Body SignUpModel registerRequest);
}
