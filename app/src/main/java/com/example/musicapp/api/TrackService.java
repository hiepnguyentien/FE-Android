package com.example.musicapp.api;

import com.example.musicapp.models.Track;
import com.example.musicapp.models.UserResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TrackService {

    Gson gson = new GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    TrackService trackService = new Retrofit.Builder()
            .baseUrl("http://192.168.1.9:5271/track/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(TrackService.class);

    static TrackService getInstance() {
        return trackService;
    }

    @GET("all")
    Call<List<Track>> getListTrack();
    @GET("{id}")
    Call<Track> getTrackById(@Path("id") int id);
    @POST("track")
    Call<Track> addTrack(@Body Track track);
    @GET("playlist/{playlistId}")
    Call<List<Track>> getTrackFromPlaylist(@Path("playlistId") int playlistId);
}
