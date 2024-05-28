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
import com.example.musicapp.models.CommentResponse;

import java.util.List;

public class CmtAdapter extends RecyclerView.Adapter<CmtAdapter.CmtViewHolder> {
    private List<CommentResponse> listCmt;

    public CmtAdapter(List<CommentResponse> listCmt) {
        this.listCmt = listCmt;
    }

    @NonNull
    @Override
    public CmtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CmtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CmtViewHolder holder, int position) {
        CommentResponse cmt = listCmt.get(position);
        if (cmt == null) {
            return;
        }
        holder.txt_username.setText(cmt.getUsername());
        holder.txt_cmt.setText(cmt.getContent());
        holder.txt_date.setText(cmt.getCreatedAt());

        Glide.with(holder.img_avatar.getContext())
                .load(cmt.getUser())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.img_avatar);
    }

    @Override
    public int getItemCount() {
        if (listCmt != null) {
            return listCmt.size();
        }
        return 0;
    }

    public class CmtViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_avatar;
        private TextView txt_username, txt_cmt, txt_date;

        public CmtViewHolder(View itemView) {
            super(itemView);
            img_avatar = itemView.findViewById(R.id.img_avatar);
            txt_username = itemView.findViewById(R.id.txt_username);
            txt_cmt = itemView.findViewById(R.id.txt_cmt);
            txt_date = itemView.findViewById(R.id.txt_date);
        }
    }
}