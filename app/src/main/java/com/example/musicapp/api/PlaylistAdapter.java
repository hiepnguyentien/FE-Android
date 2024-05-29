package com.example.musicapp.api;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapp.R;
import com.example.musicapp.models.Playlist;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private final List<Playlist> mListPlaylist;
    private OnItemClickListener mListener;

    public PlaylistAdapter(List<Playlist> mListPlaylist){
        this.mListPlaylist = mListPlaylist;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = mListPlaylist.get(position);
        if (playlist == null){
            return;
        }
        holder.tv_playlist_item_name.setText(playlist.getPlaylistName());
        holder.tv_playlist_item_author.setText(playlist.getAuthorName());
        String artworkUrl = "http://192.168.1.34:5271/track/artwork/" + playlist.getArtWork();
        Glide.with(holder.itemView.getContext())
                .load(artworkUrl)
                .into(holder.iv_playlist_item_artwork);

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mListener !=null) {
                    mListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListPlaylist.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder{
        private final TextView tv_playlist_item_name;
        private final TextView tv_playlist_item_author;
        private final ImageView iv_playlist_item_artwork;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_playlist_item_name = itemView.findViewById(R.id.tv_playlist_item_name);
            tv_playlist_item_author = itemView.findViewById(R.id.tv_playlist_item_author);
            iv_playlist_item_artwork = itemView.findViewById(R.id.iv_playlist_item_artwork);
        }
    }
}
