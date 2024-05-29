package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.api.PlaylistAdapter;
import com.example.musicapp.api.PlaylistService;
import com.example.musicapp.api.TrackAdapter;
import com.example.musicapp.api.TrackService;
import com.example.musicapp.models.Playlist;
import com.example.musicapp.models.Track;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistActivity extends AppCompatActivity {
    private RecyclerView rcvPlaylist;
    private PlaylistAdapter playlistAdapter;
    private List<Playlist> mListPlaylist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        rcvPlaylist = findViewById(R.id.rcv_playlist);
        mListPlaylist = new ArrayList<>();
        playlistAdapter = new PlaylistAdapter(mListPlaylist);
        rcvPlaylist.setLayoutManager(new LinearLayoutManager(this));
        rcvPlaylist.setAdapter(playlistAdapter);

        callApiGetPlaylist();

        playlistAdapter.setOnItemClickListener(position -> {
            Playlist playlist = mListPlaylist.get(position);
            openTrackListActivity(playlist.getId(), playlist.getPlaylistName());
        });
    }

    private void callApiGetPlaylist() {
        PlaylistService.playlistService.getListPlaylist().enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mListPlaylist.clear();
                    mListPlaylist.addAll(response.body());
                    playlistAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(PlaylistActivity.this, "Failed to load playlists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Toast.makeText(PlaylistActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openTrackListActivity(int playlistId, String playListName) {
        Intent intent = new Intent(PlaylistActivity.this, TrackListActivity.class);
        intent.putExtra("playlist", playlistId);
        intent.putExtra("playlistName", playListName);
        startActivity(intent);
    }
}
