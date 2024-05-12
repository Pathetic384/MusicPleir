package com.example.musicpleir;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
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

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private Context mContext;
    static ArrayList<MusicFiles> mFiles;

    MusicAdapter(Context context, ArrayList<MusicFiles> mFiles) {
        this.mContext = context;
        this.mFiles = mFiles;
    }

    @NonNull
    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.MyViewHolder holder, int position) {
        holder.file_name.setText(mFiles.get(position).getSongTitle());
        try {
            byte[] image = Util.getAlbumArt(mFiles.get(position).getSongLink());
            if(image!=null) {
                Glide.with(mContext).asBitmap().load(image).into(holder.album_art);
                Log.e("yoyo", String.valueOf(image));
            }
            else {
                Glide.with(mContext).asBitmap().load(R.drawable.pic).into(holder.album_art);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, PlayerActivity.class);
                    i.putExtra("position", holder.getAdapterPosition());
                    mContext.startActivity(i);
                }
            });
            holder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(mContext, v);
                    popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener((item -> {
                        switch(item.getItemId()) {
                            case R.id.download:
                                downloading(mFiles.get(holder.getAdapterPosition()).songLink.trim(), mFiles.get(holder.getAdapterPosition()).songTitle
                                        , mFiles.get(holder.getAdapterPosition()).artist);
                                Toast.makeText(mContext, "Download clicked", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }));
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void downloading(String link, String name, String artist) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(link));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle(name);
        request.setDescription(artist);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, String.valueOf(System.currentTimeMillis()));

        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        if(downloadManager != null) {
            downloadManager.enqueue(request);
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
        notifyDataSetChanged();
        }
}
