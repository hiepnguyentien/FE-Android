package com.example.musicapp.api;

import com.example.musicapp.models.Playlist;
import com.example.musicapp.models.Track;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface PlaylistService {

    //http://localhost:5271/playlist/all
    Gson gson = new GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    PlaylistService playlistService = new Retrofit.Builder()
            .baseUrl("http://192.168.1.9:5271/playlist/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(PlaylistService.class);
    @GET("all")
    Call<List<Playlist>> getListPlaylist();
}
