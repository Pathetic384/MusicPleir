package com.example.musicpleir;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class AlbumDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView albumPhoto;
    String albumName;
    ArrayList<MusicFiles> albumSongs = new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        recyclerView = findViewById(R.id.recyclerView);
        albumPhoto = findViewById(R.id.albumPhoto);
        albumName = getIntent().getStringExtra("albumName");
        int j = 0;
        for(int i=0;i< MainActivity.musicFiles.size();i++) {
            assert albumName != null;
            if(albumName.equals(MainActivity.musicFiles.get(i).getSongsCategory()) ) {
                albumSongs.add(j, MainActivity.musicFiles.get(i));
                j++;
            }
        }
        try {
            byte[] image = getAlbumArt(albumSongs.get(0).getAlbum_art());
            if(image!=null) {
                Glide.with(this).load(image).into(albumPhoto);
            }
            else {
                Glide.with(this).load(R.drawable.pic).into(albumPhoto);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!albumSongs.isEmpty()) {
            albumDetailsAdapter = new AlbumDetailsAdapter(this, albumSongs);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }
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