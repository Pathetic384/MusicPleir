package com.example.musicpleir;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    Button rcmBtn;
    static ArrayList<MusicFiles> albumSongs = new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;
    static DatabaseReference databaseReference;
    static ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        recyclerView = findViewById(R.id.recyclerView);
        albumPhoto = findViewById(R.id.albumPhoto);
        rcmBtn = findViewById(R.id.see_more);
        albumName = getIntent().getStringExtra("albumName");

        Runnable myRunnable = new Runnable(){
            public void run(){
                albumSongs = getMusicFiles(albumName);
            }
        };

        Thread thread = new Thread(myRunnable);
        thread.start();

        rcmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getApplicationContext(), RecommendDetails.class);
                i.putExtra("albumName", albumName);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(i);
            }
        });

    }

//    static void idk() {
//        if(!albumSongs.isEmpty()) {
//            try {
//                byte[] image = null;
//                if(!Objects.equals(MainActivity.userMail, "tester@gmail.com"))
//                    Util.getAlbumArt(albumSongs.get(0).getSongLink(), new MediaMetadataRetriever());
//                if (image != null) {
//                    Glide.with(this).load(image).into(albumPhoto);
//                } else {
//                    Glide.with(this).load(R.drawable.pic).into(albumPhoto);
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        else {
//            Glide.with(this).load(R.drawable.pic).into(albumPhoto);
//        }
//    }

    static ArrayList<MusicFiles> getMusicFiles(String albumName) {
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
                    Log.e("síisisis", String.valueOf(getSongs));
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return tmp;
    }

    @Override
    protected void onResume() {
        super.onResume();
       // if(!albumSongs.isEmpty()) {
            Log.e("sís", String.valueOf(albumSongs.size()));
            albumDetailsAdapter = new AlbumDetailsAdapter(this, albumSongs, albumName, 1);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
       // }
    }


}