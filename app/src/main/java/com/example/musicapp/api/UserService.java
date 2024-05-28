package com.example.musicapp.api;

import com.example.musicapp.models.CommentResponse;
import com.example.musicapp.models.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserService {
    @GET("user/{id}")
    Call<List<UserResponse>> getUserById(@Path("id") int id);
}
