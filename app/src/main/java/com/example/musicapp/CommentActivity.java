package com.example.musicapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.api.ApiClient;
import com.example.musicapp.api.CmtAdapter;
import com.example.musicapp.api.CommentService;
import com.example.musicapp.models.CommentResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView rcvComments;
    private List<CommentResponse> commentList;
    private int trackId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments);

        rcvComments = findViewById(R.id.rcv_comments);
        rcvComments.setLayoutManager(new LinearLayoutManager(this));
        rcvComments.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        commentList = new ArrayList<>();
        trackId = getIntent().getIntExtra("track_id", -1);

        if (isLoggedIn()) {
            fetchCommentsForTrack();
        } else {
            Toast.makeText(this, "Please login to view comments", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        return token != null;
    }

    private void fetchCommentsForTrack() {
        CommentService commentService = ApiClient.getClient(this).create(CommentService.class);
        commentService.getCmtById(trackId).enqueue(new Callback<List<CommentResponse>>() {
            @Override
            public void onResponse(Call<List<CommentResponse>> call, Response<List<CommentResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    commentList = response.body();
                    CmtAdapter adapter = new CmtAdapter(commentList);
                    rcvComments.setAdapter(adapter);
                } else {
                    Log.e("CommentActivity", "Response failed: " + response.code() + " - " + response.message());
                    Toast.makeText(CommentActivity.this, "Failed to load comments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CommentResponse>> call, Throwable t) {
                Log.e("CommentActivity", "Request failed", t);
                Toast.makeText(CommentActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }
}