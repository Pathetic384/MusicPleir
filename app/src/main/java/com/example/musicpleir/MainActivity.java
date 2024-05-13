package com.example.musicpleir;

import static com.example.musicpleir.MusicService.Artist_Name;
import static com.example.musicpleir.MusicService.Music_File;
import static com.example.musicpleir.MusicService.Music_Last_Played;
import static com.example.musicpleir.MusicService.Song_Name;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final int REQUEST_PERMISSION_CODE = 10;
    static ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    static ArrayList<MusicFiles> localMusicFiles = new ArrayList<>();
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    ProgressBar progressBar;
    FrameLayout bottom;
    static boolean shuffleBoolean = false, repeatBoolean = false;
    static ArrayList<String> albums = new ArrayList<>();
    public static boolean Show_Mini_Player = false;
    public static String Path_To_Mini = null;
    public static String Song_To_Mini = null;
    public static String Artist_To_Mini = null;
    public static boolean showable = false;
    FirebaseAuth auth;
    FirebaseUser user;
    public static ViewPager viewPager;
    public static TabLayout tabLayout;
    public static String userID;
    public static String userMail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //permission();
        ActivityCompat.requestPermissions( this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_MEDIA_AUDIO
                }, 1
        );


        progressBar = findViewById(R.id.progressBar);
        bottom = findViewById(R.id.frag_bottom);
        auth = FirebaseAuth.getInstance();
        //----------------
        user = auth.getCurrentUser();

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tab_layout);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new SongsFragment(), "Songs");
        viewPagerAdapter.addFragments(new AlbumFragment(), "Albums");
        viewPagerAdapter.addFragments(new SoundRecognitionFragment(), "Shazam");
        viewPagerAdapter.addFragments(new LocalSongFragment(), "Playlist");
        viewPagerAdapter.addFragments(new UserFragment(), "User");

        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        localMusicFiles = getAllLocalAudio(this);
        musicFiles = getAllAudio();
        getAllAlbum();

        if(user == null) {
//            Intent i = new Intent(getApplicationContext(), Login.class);
//            startActivity(i);
            userMail = "abc@gmail.com";
            userID ="4JUhPUv72dZDPHvPmXjVzdqSavG2";
            //finish();
        }
        else {
            userMail = user.getEmail();
            userID = user.getUid();
        }

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Intent i = new Intent(getApplicationContext(), Login.class);
//                startActivity(i);
//                finish();
//            }
//        });
    }

    void permission() {
        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permission, REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
            else {
                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, REQUEST_PERMISSION_CODE);
            }
        }
    }

    public void initViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new SongsFragment(), "Songs");
        viewPagerAdapter.addFragments(new AlbumFragment(), "Albums");
        viewPagerAdapter.addFragments(new SoundRecognitionFragment(), "Shazam");
        viewPagerAdapter.addFragments(new LocalSongFragment(), "Playlist");
        viewPagerAdapter.addFragments(new UserFragment(), "User");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }
        void addFragments(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public static void getAllAlbum () {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                collectAlbums((Map<String,Object>) snapshot.child(userID).getValue());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                progressBar.setVisibility(View.GONE);
//                initViewPager();
            }
        });

    }

    private static void collectAlbums(Map<String,Object> users) {

        ArrayList<String> albumList = new ArrayList<>();
        if(users == null)
            return;

        for(String key : users.keySet()) {
            albumList.add(key);
        }

        albums = albumList;
    }

    public ArrayList<MusicFiles> getAllAudio () {
        ArrayList<MusicFiles> tmp = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("songs");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tmp.clear();
                for (DataSnapshot dss : snapshot.getChildren()) {
                    MusicFiles getSongs = dss.getValue(MusicFiles.class);
                    if(getSongs.getSongsCategory() == null) getSongs.setSongsCategory("no title");
                    tmp.add(getSongs);
                }
                progressBar.setVisibility(View.GONE);
                initViewPager();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                initViewPager();
            }
        });
        Log.e("spôgfgggp", String.valueOf(tmp.size()));
        return tmp;
    }

    public ArrayList<MusicFiles> getAllLocalAudio (Context context) {
        ArrayList<MusicFiles> tmp2 = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ARTIST
        };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if(cursor != null) {
            while(cursor.moveToNext()) {
                String album = cursor.getString(0);
                if(Objects.equals(album, "")) album = "no album";
                String title = cursor.getString(1);
                if(Objects.equals(title, "")) title = "no title";
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                if(artist == null) artist = "no artist";

                MusicFiles musicFiles1 = new MusicFiles(album, title , artist, duration, path);
                if(!Objects.equals(musicFiles1.getSongLink(), "tone.mp3")) {
                    tmp2.add(musicFiles1);
                }
            }
            cursor.close();
        }

        return tmp2;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search_option);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        ArrayList<MusicFiles> myFiles = new ArrayList<>();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                for(MusicFiles song : musicFiles) {
                    if(song.getSongTitle().toLowerCase().contains(userInput)) {
                        myFiles.add(song);
                    }
                }
                SongsFragment.musicAdapter.updateList(myFiles);
            };
        }, 5);


        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(Music_Last_Played, MODE_PRIVATE);
        if(preferences == null) return;
        String path = preferences.getString(Music_File, null);
        String artist = preferences.getString(Artist_Name, null);
        String song_name = preferences.getString(Song_Name, null);
        if(path != null) {
            Show_Mini_Player = true;
            Path_To_Mini = path;
            Artist_To_Mini = artist;
            Song_To_Mini = song_name;
        }
        else {
            Show_Mini_Player = false;
            Path_To_Mini = null;
            Artist_To_Mini = null;
            Song_To_Mini = null;
        }
        if(Show_Mini_Player && showable) {
            bottom.setVisibility(View.VISIBLE);
        }
        else bottom.setVisibility(View.GONE);
    }
}