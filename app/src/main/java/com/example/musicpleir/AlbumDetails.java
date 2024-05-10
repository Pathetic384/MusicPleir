package com.example.musicpleir;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class AlbumDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView albumPhoto;
    String albumName;
    ArrayList<MusicFiles> albumSongs = new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        recyclerView = findViewById(R.id.recyclerView);
        albumPhoto = findViewById(R.id.albumPhoto);
        albumName = getIntent().getStringExtra("albumName");

        Runnable myRunnable = new Runnable(){
            public void run(){
                albumSongs = getMusicFiles();
            }
        };

        Thread thread = new Thread(myRunnable);
        thread.start();


    }

    void idk() {
        if(!albumSongs.isEmpty()) {
            try {
                byte[] image = getAlbumArt(albumSongs.get(0).getSongLink());
                if (image != null) {
                    Glide.with(this).load(image).into(albumPhoto);
                } else {
                    Glide.with(this).load(R.drawable.pic).into(albumPhoto);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            Glide.with(this).load(R.drawable.pic).into(albumPhoto);
        }
    }

    private ArrayList<MusicFiles> getMusicFiles() {
        ArrayList<MusicFiles> tmp = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("users/" + MainActivity.userID + "/" + albumName);
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tmp.clear();
                for (DataSnapshot dss : snapshot.getChildren()) {
                    MusicFiles getSongs = dss.getValue(MusicFiles.class);
                    if(getSongs.getSongsCategory() == null) getSongs.setSongsCategory("no title");
                    tmp.add(getSongs);
        //            Log.e("síisisis", String.valueOf(getSongs));
                }
                idk();
                albumSongs = tmp;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                 idk();
                albumSongs = tmp;
            }
        });
     //   Log.e("sís", String.valueOf(tmp.size()));
        albumSongs = tmp;
        return tmp;
    }

    @Override
    protected void onResume() {
        super.onResume();
       // if(!albumSongs.isEmpty()) {
            Log.e("sís", String.valueOf(albumSongs.size()));
            albumDetailsAdapter = new AlbumDetailsAdapter(this, albumSongs);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
       // }
    }

    private byte[] getAlbumArt (String uri) throws IOException {
        if(Objects.equals(uri, "")) return null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}