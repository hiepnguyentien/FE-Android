package com.example.musicapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.api.ApiClient;
import com.example.musicapp.api.TrackAdapter;
import com.example.musicapp.api.TrackService;
import com.example.musicapp.api.UserService;
import com.example.musicapp.models.Track;
import com.example.musicapp.models.UserResponse;
import com.example.musicapp.util.DialogUtil;
import com.google.android.material.tabs.TabLayout;

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rcvTrack.setLayoutManager(layoutManager);
        callApiGetTrack();
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 3) {
                    if (isLoggedIn()) {
                        startActivity(new Intent(MainActivity.this, UserActivity.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void openCommentActivity(int trackId) {
        Intent intent;
        intent = new Intent(MainActivity.this, CommentActivity.class);
        intent.putExtra("track_id", trackId);
        startActivity(intent);
    }

    private boolean isLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token != null || MyMusicApp.getAuthToken() != null) {
            return true;
        } else {
            return false;
        }
    }

    private void callApiGetTrack(){
        TrackServices.TrackServices.getListTrack().enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                if (response.isSuccessful()) {
                    mListTrack = response.body();
                    TrackAdapter trackAdapter = new TrackAdapter(mListTrack);
                    trackAdapter.setOnItemClickListener(new TrackAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Track selectedTrack = mListTrack.get(position);
                            int trackId = selectedTrack.getId();
                            openCommentActivity(trackId);
                        }
                    });
                    rcvTrack.setAdapter(trackAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load tracks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }
}