package com.example.musicpleir;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_NORMAL = 0;
    private static final int ITEM_TYPE_EMPTY = 1;
    private static final int EMPTY_ITEMS_COUNT = 3;
    private static final int MAX_SIZE = 20;
    private Context mContext;
    static ArrayList<MusicFiles> mFiles;

    MusicAdapter(Context context, ArrayList<MusicFiles> mFiles) {
        this.mContext = context;
        this.mFiles = new ArrayList<>(mFiles);
        sortMusicFilesByTitle();
    }
    // Phương thức sắp xếp danh sách mFiles theo tên bài hát
    private void sortMusicFilesByTitle() {
        Collections.sort(mFiles, new Comparator<MusicFiles>() {
            @Override
            public int compare(MusicFiles o1, MusicFiles o2) {
                return o1.getSongTitle().compareTo(o2.getSongTitle());
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= mFiles.size()) {
            return ITEM_TYPE_EMPTY;
        } else {
            return ITEM_TYPE_NORMAL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_EMPTY) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.empty_item_layout, parent, false);
            return new EmptyViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position > MAX_SIZE) return;
        if (holder.getItemViewType() == ITEM_TYPE_NORMAL) {
            MyViewHolder myHolder = (MyViewHolder) holder;
            myHolder.file_name.setText(mFiles.get(position).getSongTitle());
            myHolder.artist.setText(mFiles.get(position).getArtist());
            try {
                Glide.with(mContext).asBitmap().load(R.drawable.ic_music_note).into(myHolder.album_art);
                myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, PlayerActivity.class);
                        i.putExtra("position", myHolder.getAdapterPosition());
                        i.putExtra("sender", "fire");
                        mContext.startActivity(i);
                    }
                });
                myHolder.menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(mContext, v);
                        popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                        popupMenu.show();
                        popupMenu.setOnMenuItemClickListener((item -> {
                            switch (item.getItemId()) {
                                case R.id.download:
                                    downloading(mFiles.get(myHolder.getAdapterPosition()).songLink.trim(),
                                            mFiles.get(myHolder.getAdapterPosition()).songTitle,
                                            mFiles.get(myHolder.getAdapterPosition()).artist);
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
    }

    void downloading(String link, String name, String artist) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(link));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle(name);
        request.setDescription(artist);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("audio/mpeg");

        String fileName = System.currentTimeMillis() + ".mp3";
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
        }
    }

    @Override
    public int getItemCount() {
        if(mFiles.size() + EMPTY_ITEMS_COUNT > MAX_SIZE ) {
            return MAX_SIZE;
        }
        else  return mFiles.size() + EMPTY_ITEMS_COUNT;
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder {

        TextView file_name, artist;
        ImageView album_art, menu;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            album_art = itemView.findViewById(R.id.music_img);
            menu = itemView.findViewById(R.id.menuMore);
            artist = itemView.findViewById(R.id.music_artist_name);
        }
    }

    public static class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    void updateList(ArrayList<MusicFiles> musicFilesArrayList) {
        mFiles = new ArrayList<>(musicFilesArrayList);
        sortMusicFilesByTitle();  // Sắp xếp danh sách sau khi cập nhật
        notifyDataSetChanged();
    }

    public ArrayList<MusicFiles> getMusicFiles() {
        return mFiles;
    }

    public void updateMusicFiles(ArrayList<MusicFiles> newFiles) {
        sortMusicFilesByTitle();  // Sắp xếp danh sách sau khi cập nhật
        this.mFiles = new ArrayList<>(newFiles);
    }
}
