package com.example.musicapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.api.TrackService;
import com.example.musicapp.models.Track;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadTrackActivity extends AppCompatActivity {

    private static final int PICK_AUDIO_REQUEST_CODE = 123;
    private static final int PICK_IMAGE_REQUEST_CODE = 456;

    private Uri selectedAudioUri;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_track);

        Button uploadTrackButton = findViewById(R.id.upload_track);
        uploadTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        Button selectArtworkButton = findViewById(R.id.select_aw);
        selectArtworkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        Button uploadButton = findViewById(R.id.btn_upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTrack();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Artwork"), PICK_IMAGE_REQUEST_CODE);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg");
        startActivityForResult(Intent.createChooser(intent, "Select MP3 file"), PICK_AUDIO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                selectedAudioUri = data.getData();
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
            }
        }
    }

    private void uploadTrack() {
        EditText trackNameEditText = findViewById(R.id.edt_tName);
        String trackName = trackNameEditText.getText().toString();

        EditText descriptionEditText = findViewById(R.id.edt_description);
        String description = descriptionEditText.getText().toString();

        CheckBox privateCheckBox = findViewById(R.id.checkbox_private);
        boolean isPrivate = privateCheckBox.isChecked();

        if (selectedAudioUri == null || selectedImageUri == null) {
            // Handle if audio or image is not selected
            return;
        }

        String selectedAudioPath = getRealPathFromURI(selectedAudioUri);
        String selectedImagePath = getRealPathFromURI(selectedImageUri);

        // Create a Track object with all the necessary information
        Track track = new Track();
        track.setTrackName(trackName);
        track.setDescription(description);
        track.setPrivate(isPrivate);
        track.setFileName(selectedAudioPath);
        track.setArtWork(selectedImagePath);

        // Call your TrackService to upload the track
        TrackService trackService = TrackService.getInstance();
        Call<Track> call = trackService.addTrack(track);
        call.enqueue(new Callback<Track>() {
            @Override
            public void onResponse(Call<Track> call, Response<Track> response) {
                if (response.isSuccessful()) {
                    // Track uploaded successfully
                    // Handle success response
                } else {
                    // Track upload failed
                    // Handle error response
                }
            }

            @Override
            public void onFailure(Call<Track> call, Throwable t) {
                // Track upload failed due to network error or other reasons
                // Handle failure
            }
        });
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return uri.getPath();
    }
}