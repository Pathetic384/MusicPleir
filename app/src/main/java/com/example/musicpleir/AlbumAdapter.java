package com.example.musicpleir;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyHolder> {
    private Context mContext;
    private ArrayList<String> albumNames;
    //private ArrayList<MusicFiles> albumFiles;
    View view;

    public AlbumAdapter(Context mContext, ArrayList<String> albumNames) {
        this.albumNames = albumNames;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AlbumAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.album_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.MyHolder holder, int position) {
        //holder.album_name.setText(albumFiles.get(position).getSongsCategory());
        holder.album_name.setText(albumNames.get(position));
        try {
            //byte[] image = getAlbumArt(albumFiles.get(position).getSongLink());
            //if (image != null) {
               // Glide.with(mContext).asBitmap().load(image).into(holder.album_image);
          //  } else {
                Glide.with(mContext).asBitmap().load(R.drawable.pic).into(holder.album_image);
           // }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i =new Intent(mContext, AlbumDetails.class);
                    i.putExtra("albumName", albumNames.get(holder.getAdapterPosition()));
                    mContext.startActivity(i);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return albumNames.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView album_name;
        ImageView album_image;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            album_image = itemView.findViewById(R.id.album_image);
            album_name = itemView.findViewById(R.id.album_name);
        }
    }

    void updateList(ArrayList<String> musicFilesArrayList) {
        albumNames = new ArrayList<>();
        albumNames.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}
