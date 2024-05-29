package com.example.musicapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;



import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapp.api.ApiClient;
import com.example.musicapp.api.CmtAdapter;
import com.example.musicapp.api.CommentService;
import com.example.musicapp.api.TrackService;
import com.example.musicapp.models.CommentResponse;
import com.example.musicapp.models.Track;
import com.example.musicapp.util.DialogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListeningActivity extends AppCompatActivity {

    private SeekBar songBar;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Track track;
    private TextView mName;
    private ImageView ivArtwork, back, pause_play, skip;
    private RecyclerView rcvComments;
    private List<CommentResponse> commentList;
    private int trackId;
    private EditText edt_addcmt;
    private List<Track> trackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listening);

        songBar = findViewById(R.id.songBar);
        mName = findViewById(R.id.mName);
        ivArtwork = findViewById(R.id.iv_artwork);
        back = findViewById(R.id.backLis);
        pause_play = findViewById(R.id.pause_play);
        skip = findViewById(R.id.next);
        edt_addcmt = findViewById(R.id.txt_addcmt);

        edt_addcmt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Handle action when "Enter" key is pressed
                    String commentText = edt_addcmt.getText().toString().trim();
                    if (!commentText.isEmpty()) {
                        // Add the comment to the RecyclerView
                        addComment(commentText);
                        // Clear the EditText after adding the comment
                        edt_addcmt.setText("");
                        return true; // Consume the event
                    }
                }
                return false; // Return false if not consumed
            }
        });

        mediaPlayer = new MediaPlayer(); // Initialize MediaPlayer

        pause_play.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                pause_play.setImageResource(R.drawable.ic_play);
            } else if (mediaPlayer != null) {
                mediaPlayer.start();
                pause_play.setImageResource(R.drawable.baseline_pause_24);
            }
        });

        skip.setOnClickListener(v -> {
            trackId++;
            Log.d("ListeningActivity", "Next button clicked. Incremented trackId: " + trackId);
            fetchAndPlayNextTrack(trackId);
        });

        back.setOnClickListener(v -> {
            finish();
        });

        // Receive track information from Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("track")) {
            track = (Track) intent.getSerializableExtra("track");
            if (track != null) {
                updateUI(track);
                playTrack(track);
                trackId = track.getId(); // Initialize trackId with the current track's ID
                Log.d("ListeningActivity", "Initial trackId set from intent: " + trackId);
            }
        } else {
            trackId = getIntent().getIntExtra("track_id", -1);
            Log.d("ListeningActivity", "Initial trackId from intent extra: " + trackId);
        }

        // Set up SeekBar listener
        songBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    handler.removeCallbacks(updateSeekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                    updateSeekBar();
                }
            }
        });

        rcvComments = findViewById(R.id.rcv_comments);
        rcvComments.setLayoutManager(new LinearLayoutManager(this));
        rcvComments.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        commentList = new ArrayList<>();

        if (isLoggedIn()) {
            fetchCommentsForTrack();
        } else {
            DialogUtil.showDialog(this, "haven't login?", "please login to view comments");
        }
    }

    private boolean isLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        return token != null || MyMusicApp.getAuthToken() != null;
    }

    private void fetchAndPlayNextTrack(int trackId) {
        Log.d("ListeningActivity", "Fetching next track with trackId: " + trackId);
        TrackService.trackService.getTrackById(trackId).enqueue(new Callback<Track>() {
            @Override
            public void onResponse(Call<Track> call, Response<Track> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Track nextTrack = response.body();
                    playTrack(nextTrack);
                    updateUI(nextTrack); // Update UI with details of the next track
                } else {
                    Toast.makeText(ListeningActivity.this, "Failed to fetch next track", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Track> call, Throwable t) {
                Log.e("ListeningActivity", "Failed to fetch next track", t);
                Toast.makeText(ListeningActivity.this, "Failed to fetch next track", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addComment(String commentText) {
        if (isLoggedIn()) {
            // User is logged in, proceed with adding the comment
            CommentService commentService = ApiClient.getClient(this).create(CommentService.class);
            commentService.addCmt(trackId).enqueue(new Callback<CommentResponse>() {
                @Override
                public void onResponse(Call<CommentResponse> call, Response<CommentResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Add the comment to the RecyclerView
                        CommentResponse commentResponse = response.body();
                        commentList.add(commentResponse);
                        // Notify the adapter about the new comment
                        rcvComments.getAdapter().notifyDataSetChanged();
                    } else {
                        // Log response details for debugging
                        String errorMessage = "Failed to add comment: ";
                        if (response.errorBody() != null) {
                            try {
                                errorMessage += response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            errorMessage += response.code() + " - " + response.message();
                        }
                        Log.e("ListeningActivity", errorMessage);
                        Toast.makeText(ListeningActivity.this, "Failed to add comment", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CommentResponse> call, Throwable t) {
                    Log.e("ListeningActivity", "Failed to add comment", t);
                    Toast.makeText(ListeningActivity.this, "Failed to add comment", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // User is not logged in, prompt them to log in
            // You can display a login screen or any other appropriate UI here
            DialogUtil.showDialog(this, "Login Required", "Please login to add a comment");
        }
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
                    Toast.makeText(ListeningActivity.this, "Failed to load comments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CommentResponse>> call, Throwable t) {
                Log.e("CommentActivity", "Request failed", t);
                Toast.makeText(ListeningActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playTrack(Track track) {
        String trackUrl = "http://192.168.1.9:5271/track/media/" + track.getFileName();

        // Reset the media player before setting a new data source
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        } else {
            mediaPlayer = new MediaPlayer(); // Re-initialize if null
        }

        try {
            mediaPlayer.setDataSource(trackUrl);
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                // Set the maximum value of the SeekBar to the duration of the song
                songBar.setMax(mp.getDuration());
                updateSeekBar();
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("ListeningActivity", "Error occurred while playing audio. What: " + what + ", Extra: " + extra);
                Toast.makeText(ListeningActivity.this, "Error playing track", Toast.LENGTH_SHORT).show();
                return true;
            });
            mediaPlayer.prepareAsync(); // Prepare asynchronously
        } catch (IOException e) {
            Log.e("ListeningActivity", "IOException while preparing MediaPlayer", e);
            Toast.makeText(this, "Error preparing track", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSeekBar() {
        if (mediaPlayer != null) {
            songBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(updateSeekBar, 1000);
        }
    }

    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
        }
    };

    private void updateUI(Track track) {
        // Set track name
        mName.setText(track.getTrackName());

        // Load and set artwork image
        String artworkUrl = "http://192.168.1.9:5271/track/artwork/" + track.getArtWork();
        Glide.with(this).load(artworkUrl).into(ivArtwork);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            handler.removeCallbacks(updateSeekBar);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
