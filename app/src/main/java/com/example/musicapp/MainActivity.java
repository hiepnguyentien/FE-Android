package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.example.musicapp.api.TrackService;
import com.example.musicapp.adapter.TrackAdapter;
import com.example.musicapp.models.track.Track;

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