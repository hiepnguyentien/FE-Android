package com.example.musicapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.models.track.Track;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {
    private final List<Track> mListTrack;

    public TrackAdapter(List<Track> mListTrack) {
        this.mListTrack = mListTrack;
    }

    @NonNull
//    @Override
//    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main, parent, false);
//        return new TrackViewHolder(view);
//    }
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track, parent, false);
        return new TrackViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        Track track = mListTrack.get(position);
        if(track == null){
            return;
        }
        holder.tv_track_name.setText(track.getTrackName());
        holder.tv_author.setText(track.getAuthor());
    }

    @Override
    public int getItemCount() {
        if (mListTrack != null) {
            return mListTrack.size();
        }
        return 0;
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder{
        private final TextView tv_track_name;
        private final TextView tv_author;
        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_track_name = itemView.findViewById(R.id.tv_track_name);
            tv_author = itemView.findViewById(R.id.tv_author);
        }
    }
}
