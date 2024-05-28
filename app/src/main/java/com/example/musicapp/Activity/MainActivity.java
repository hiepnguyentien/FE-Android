package com.example.musicapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.musicapp.R;
import com.example.musicapp.Api.TrackService;
import com.example.musicapp.Adapter.TrackAdapter;
import com.example.musicapp.Models.Track.Track;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rcvTrack;
    private List<Track> mListTrack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rcvTrack = findViewById(R.id.rcv_track);

        mListTrack = new ArrayList<>();

        callApiGetTrack();
    }

    private void callApiGetTrack(){
        TrackService.trackService.getListTrack().enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                mListTrack = response.body();
                TrackAdapter trackAdapter = new TrackAdapter(mListTrack);
                rcvTrack.setAdapter(trackAdapter);
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }

}