package com.example.musicapp.Api;

import com.example.musicapp.Models.Track.Track;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface TrackServices {

    //http://localhost:5271/track/all
    Gson gson = new GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    TrackServices TrackServices = new Retrofit.Builder()
            .baseUrl("http://192.168.1.34:5271/track/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(TrackServices.class);
    @GET("all")
    Call<List<Track>> getListTrack();
}
