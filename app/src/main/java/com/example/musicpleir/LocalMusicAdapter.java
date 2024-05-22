package com.example.musicpleir;

import static com.example.musicpleir.Util.getAlbumArt;

import android.app.DownloadManager;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.MyViewHolder>{
    private Context mContext;
    static ArrayList<MusicFiles> mFiles;

    LocalMusicAdapter(Context context, ArrayList<MusicFiles> mFiles) {
        this.mContext = context;
        this.mFiles = mFiles;
    }

    @NonNull
    @Override
    public LocalMusicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new LocalMusicAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalMusicAdapter.MyViewHolder holder, int position) {
        holder.file_name.setText(mFiles.get(holder.getAdapterPosition()).getSongTitle());

        try {
            if(mFiles.get(holder.getAdapterPosition()).getSongLink() == null) return;
            byte[] image = getAlbumArt(mFiles.get(position).getSongLink(), new MediaMetadataRetriever());
            if(image!=null) {
                Glide.with(mContext).asBitmap().load(image).into(holder.album_art);
            }
            else {
                Glide.with(mContext).asBitmap().load(R.drawable.ic_music_note).into(holder.album_art);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, PlayerActivity.class);
                    i.putExtra("position", holder.getAdapterPosition());
                    i.putExtra("sender", "local");
                    Log.e("4345345345", String.valueOf(holder.getAdapterPosition()));
                    mContext.startActivity(i);
                }
            });
            holder.menu.setVisibility(View.GONE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder {

        TextView file_name;
        ImageView album_art, menu;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            album_art = itemView.findViewById(R.id.music_img);
            menu = itemView.findViewById(R.id.menuMore);
        }
    }

    void updateList(ArrayList<MusicFiles> musicFilesArrayList) {
        mFiles = new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
        //notifyDataSetChanged();

    }

}
