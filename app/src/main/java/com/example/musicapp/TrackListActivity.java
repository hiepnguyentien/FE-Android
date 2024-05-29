package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.api.TrackAdapter;
import com.example.musicapp.api.TrackService;
import com.example.musicapp.models.Track;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackListActivity extends AppCompatActivity {
    private RecyclerView rcvTrack;
    private TextView tvPlaylistName;
    private List<Track> mListTrack;
    private TrackAdapter trackAdapter;

    @Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_track_list);

    rcvTrack = findViewById(R.id.rcv_tracklist);
    tvPlaylistName = findViewById(R.id.tv_tracklist_playlistname);
    mListTrack = new ArrayList<>();
    trackAdapter = new TrackAdapter(mListTrack);
    rcvTrack.setLayoutManager(new LinearLayoutManager(this));
    rcvTrack.setAdapter(trackAdapter);

    int playlistId = getIntent().getIntExtra("playlist", -1);
    String playlistName = getIntent().getStringExtra("playlistName");
    if (playlistId != -1) {
        tvPlaylistName.setText(playlistName);
        callApiGetTrackFromPlaylist(playlistId);
    } else {
        Toast.makeText(this, "Invalid playlist ID", Toast.LENGTH_SHORT).show();
    }

    trackAdapter.setOnItemClickListener(position -> {
        Track selectedTrack = mListTrack.get(position);
        int trackId = selectedTrack.getId();
        openCommentActivity(trackId);
    });
} 

    private void callApiGetTrackFromPlaylist(int playlistId) {
        TrackService.trackService.getTrackFromPlaylist(playlistId).enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                if (response.isSuccessful()) {
                    mListTrack.clear();
                    mListTrack.addAll(response.body());
                    trackAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(TrackListActivity.this, "Failed to load tracks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                Toast.makeText(TrackListActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openCommentActivity(int trackId) {
        Intent intent = new Intent(TrackListActivity.this, CommentActivity.class);
        intent.putExtra("track_id", trackId);
        startActivity(intent);
    }
}
