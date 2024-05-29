package com.example.musicapp.api;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapp.ListeningActivity;
import com.example.musicapp.R;
import com.example.musicapp.models.Track;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {
    private final List<Track> mListTrack;
    private OnItemClickListener mListener;
    private Context mContext; // Add a reference to the context

    // Constructor with context parameter
    public TrackAdapter(Context context, List<Track> mListTrack) {
        this.mContext = context;
        this.mListTrack = mListTrack;
    }

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Method to set the item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        Track track = mListTrack.get(position);
        if (track == null) {
            return;
        }
        holder.tv_track_name.setText(track.getTrackName());
        holder.tv_author.setText(track.getAuthor());
        String artworkUrl = "http://192.168.1.34:5271/track/artwork/" + track.getArtWork();
        Glide.with(holder.itemView.getContext())
                .load(artworkUrl)
                .into(holder.iv_artwork);

        // Set click listener for the play button
        holder.media_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ListeningActivity.class);
                intent.putExtra("track", track);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListTrack.size();
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_track_name;
        private final TextView tv_author;
        private final ImageView iv_artwork;
        private final ImageView media_play;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_track_name = itemView.findViewById(R.id.tv_track_name);
            tv_author = itemView.findViewById(R.id.tv_author);
            iv_artwork = itemView.findViewById(R.id.iv_artwork);
            media_play = itemView.findViewById(R.id.media_play);
        }
    }
}