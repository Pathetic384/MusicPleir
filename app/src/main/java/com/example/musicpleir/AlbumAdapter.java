package com.example.musicpleir;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyHolder> {
    private Context mContext;
    private ArrayList<String> albumNames;
    Button delete;
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

    private void deleteSelect(int position) {
        if(Objects.equals(albumNames.get(position), "base_album")) {
            Toast.makeText(mContext, "You can not delete base album", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(MainActivity.userID).child(albumNames.get(position))
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        MainActivity.getAllAlbum();
                        ArrayList<String> newAlbum = MainActivity.albums;
                        updateList(newAlbum);

                    }
                });
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
                Glide.with(mContext).asBitmap().load(R.mipmap.ic_launcher).into(holder.album_image);
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
        delete = view.findViewById(R.id.delete_album);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelect(holder.getAdapterPosition());
            }
        });
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
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
        //notifyDataSetChanged();
    }
}
