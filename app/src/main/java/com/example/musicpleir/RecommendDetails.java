package com.example.musicpleir;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecommendDetails extends AppCompatActivity {
    RecyclerView recyclerView;
    String albumName;
    Button back, reload;
    TextView more;
    static ArrayList<MusicFiles> albumSongs = new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend_details);
        recyclerView = findViewById(R.id.recyclerView);
        back = findViewById(R.id.back_from_rcm);
        reload = findViewById(R.id.reload);
        more = findViewById(R.id.rcm_songs);
        albumName = getIntent().getStringExtra("albumName");

        albumSongs = MainActivity.rcmSongs;
        more.setText(MainActivity.txt);
        //Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();

        Runnable myRunnable = new Runnable(){
            public void run(){
                //albumSongs = LocalMusicAdapter.mFiles;
            }
        };

        Thread thread = new Thread(myRunnable);
        thread.start();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }



    @Override
    protected void onResume() {
        super.onResume();
        // if(!albumSongs.isEmpty()) {
        Log.e("sís", String.valueOf(albumSongs.size()));
        albumDetailsAdapter = new AlbumDetailsAdapter(this, albumSongs, albumName, 2);
        recyclerView.setAdapter(albumDetailsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        // }
    }

}
