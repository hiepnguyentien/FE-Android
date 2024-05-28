package com.example.musicapp.api;

import com.example.musicapp.models.CommentResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CommentService {
    @GET("track/{id}")
    Call<List<CommentResponse>> getCmtById(@Path("id") int id);
}
