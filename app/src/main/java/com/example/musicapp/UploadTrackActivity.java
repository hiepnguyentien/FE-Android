package com.example.musicapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private static final String DEFAULT_ARTWORK_URL = "http://192.168.1.34:5271/track/artwork/default-artwork.jpg";

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
                String fileName = getFileName(selectedAudioUri);

                if (fileName != null && fileName.contains(" ")) {
                    Toast.makeText(this, "Please rename the file to remove spaces.", Toast.LENGTH_LONG).show();
                } else {
                    // Replace upload_track button with a TextView containing the file name
                    Button uploadTrackButton = findViewById(R.id.upload_track);
                    uploadTrackButton.setVisibility(View.GONE);
                    TextView audioFileNameTextView = findViewById(R.id.audio_file_name);
                    audioFileNameTextView.setVisibility(View.VISIBLE);
                    audioFileNameTextView.setText(fileName);
                }
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                selectedImageUri = data.getData();
                String fileName = getFileName(selectedImageUri);

                if (fileName != null && fileName.contains(" ")) {
                    Toast.makeText(this, "Please rename the image file to remove spaces.", Toast.LENGTH_LONG).show();
                } else {
                    // Replace select_aw button with a small ImageView containing the selected image
                    Button selectArtworkButton = findViewById(R.id.select_aw);
                    selectArtworkButton.setVisibility(View.GONE);
                    ImageView artworkImageView = findViewById(R.id.artwork_image_view);
                    artworkImageView.setVisibility(View.VISIBLE);
                    artworkImageView.setImageURI(selectedImageUri);
                }
            }
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private void uploadTrack() {
        EditText trackNameEditText = findViewById(R.id.edt_tName);
        String trackName = trackNameEditText.getText().toString();

        EditText descriptionEditText = findViewById(R.id.edt_description);
        String description = descriptionEditText.getText().toString();

        CheckBox privateCheckBox = findViewById(R.id.checkbox_private);
        boolean isPrivate = privateCheckBox.isChecked();

        if (selectedAudioUri == null) {
            // Handle if audio is not selected
            Toast.makeText(this, "Please select an audio file.", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedAudioPath = getRealPathFromURI(selectedAudioUri);
        String selectedImagePath = selectedImageUri != null ? getRealPathFromURI(selectedImageUri) : DEFAULT_ARTWORK_URL;

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
                    Toast.makeText(UploadTrackActivity.this, "Track uploaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Track upload failed
                    // Handle error response
                    Toast.makeText(UploadTrackActivity.this, "Track upload failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Track> call, Throwable t) {
                // Track upload failed due to network error or other reasons
                // Handle failure
                Toast.makeText(UploadTrackActivity.this, "Track upload failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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