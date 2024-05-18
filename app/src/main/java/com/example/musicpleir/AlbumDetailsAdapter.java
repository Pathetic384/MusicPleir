package com.example.musicpleir;

import static com.example.musicpleir.AlbumDetails.albumSongs;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyHolder> {
    private Context mContext;
    public static ArrayList<MusicFiles> albumFiles;
    View view;
    String albumName;
    int loc;

    public AlbumDetailsAdapter(Context mContext, ArrayList<MusicFiles> albumFiles, String albumName, int loc) {
        this.albumFiles = albumFiles;
        this.mContext = mContext;
        this.albumName = albumName;
        this.loc = loc;
    }

    @NonNull
    @Override
    public AlbumDetailsAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumDetailsAdapter.MyHolder holder, int position) {
        holder.album_name.setText(albumFiles.get(holder.getAdapterPosition()).getSongTitle());
        try {
            byte[] image = null;
            if(!Objects.equals(MainActivity.userMail, "tester@gmail.com"))
                Util.getAlbumArt(albumFiles.get(holder.getAdapterPosition()).getSongLink() ,new MediaMetadataRetriever());
            if (image != null) {
                Glide.with(mContext).asBitmap().load(image).into(holder.album_image);
            } else {
                Glide.with(mContext).asBitmap().load(R.drawable.pic).into(holder.album_image);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, PlayerActivity.class);
                    if(loc == 1) {
                        i.putExtra("sender", "albumDetails");
                    }
                    else {
                        i.putExtra("sender", "rcmDetails");
                    }
                    i.putExtra("position", holder.getAdapterPosition());
                    mContext.startActivity(i);
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.del, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener((item -> {
                    switch(item.getItemId()) {
                        case R.id.delete:
                            delete(holder.getAdapterPosition());
                            Toast.makeText(mContext, "del clicked", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return true;
                }));
            }
        });
    }

    private void delete(int position) {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(MainActivity.userID).child(albumName).child(albumSongs.get(position).songTitle)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        albumSongs = new ArrayList<>();
                        albumSongs = AlbumDetails.getMusicFiles(albumName);
                        updateList();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView album_name;
        ImageView album_image, menu;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            album_image = itemView.findViewById(R.id.music_img);
            album_name = itemView.findViewById(R.id.music_file_name);
            menu = itemView.findViewById(R.id.menuMore);
        }
    }
    void updateList() {
//        albumFiles = new ArrayList<>();
//        albumFiles.addAll(musicFilesArrayList);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
        //notifyDataSetChanged();
    }
}
