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
import android.os.AsyncTask;
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

import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.CodeVerifierUtil;
import net.openid.appauth.ResponseTypeValues;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        SoundRecognitionFragment.OnSongRecognizedListener {

    private SongsFragment songsFragment;
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
    public static boolean testing = false;
    private SearchView searchView;

    // Sound recognition fragment callback
    @Override
    public void onSongRecognized(String songTitle) {
        if (searchView != null) {
            searchView.setQuery(songTitle, true); // true để nộp truy vấn ngay lập tức
        }
    }


    // Recommended songs from Spotify API
    public static ArrayList<String> recommendedSongs = new ArrayList<>();
    private class RecommenderTask extends AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            return Recommender.recommend(SpotifyAuth.getAccessToken());
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            recommendedSongs = result;
            for (int i = 0; i < recommendedSongs.size(); ++ i) {
                System.out.println("Recommended Song" + ": " + recommendedSongs.get(i));
            }
            Toast.makeText(MainActivity.this, String.valueOf(recommendedSongs), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //permission();

        authenticateUser();


        progressBar = findViewById(R.id.progressBar);
        bottom = findViewById(R.id.frag_bottom);

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tab_layout);

        createViewPager();

        localMusicFiles = getAllLocalAudio(this);
        loadMusicFiles();
        getAllAlbum();

        new RecommenderTask().execute();
    }


    private void loadMusicFiles() {
        new LoadAudioTask(this).execute();
    }
    private class LoadAudioTask extends AsyncTask<Void, Void, ArrayList<MusicFiles>> {
        private Context context;

        public LoadAudioTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<MusicFiles> doInBackground(Void... voids) {
            ArrayList<MusicFiles> allAudio = new ArrayList<>();
            //allAudio.addAll(getAllLocalAudio(context));
            allAudio.addAll(getAllAudioFromFirebase());
            return allAudio;
        }

        @Override
        protected void onPostExecute(ArrayList<MusicFiles> musicFiles) {
            super.onPostExecute(musicFiles);
            progressBar.setVisibility(View.GONE);
            MainActivity.musicFiles = musicFiles;
            initViewPager();
        }
    }

    public static ArrayList<MusicFiles> getAllLocalAudio (Context context) {
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
                if (duration == null || duration.isEmpty()) continue;
                long durationLong = Long.parseLong(duration);
                if (durationLong < 60000) {
                    continue; // filter out songs shorter than 1 minute
                }
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                if(artist == null) artist = "no artist";

                MusicFiles musicFiles1 = new MusicFiles(album, title , artist, duration, path);
                if(!musicFiles1.getSongLink().trim().contains("tone")) {
                    tmp2.add(musicFiles1);
                }
            }
            // sort the songs by title
            tmp2.sort((o1, o2) -> o1.getSongTitle().compareTo(o2.getSongTitle()));
            cursor.close();
        }
        return tmp2;
    }
    private ArrayList<MusicFiles> getAllAudioFromFirebase() {
        ArrayList<MusicFiles> audioList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("songs");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dss : snapshot.getChildren()) {
                    MusicFiles getSongs = dss.getValue(MusicFiles.class);
                    if (getSongs != null) {
                        if (getSongs.getSongsCategory() == null) {
                            getSongs.setSongsCategory("no title");
                        }
                        audioList.add(getSongs);
                    }
                }
                updateMusicFiles(audioList);
                localMusicFiles = getAllLocalAudio(getApplicationContext());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                updateMusicFiles(null);
            }
        });
        return audioList;
    }

    private void updateMusicFiles(ArrayList<MusicFiles> newFiles) {
        if (newFiles != null) {
            MainActivity.musicFiles.addAll(newFiles);
        }
        progressBar.setVisibility(View.GONE);
        if (songsFragment != null) {
            songsFragment.onMusicDataLoaded(MainActivity.musicFiles);
        }
        initViewPager();
    }

    
    private void authenticateUser() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user == null) {
//            Intent i = new Intent(getApplicationContext(), Login.class);
//            startActivity(i);
            userMail = "tester@gmail.com";
            userID ="1dPHdOx5ivgQJhoMtUgs2otLW1v2";
            //finish();
        }
        else {
            userMail = user.getEmail();
            userID = user.getUid();
        }

        if(!Objects.equals(userMail, "tester@gmail.com")) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.READ_MEDIA_AUDIO
                    }, 1
            );
        }
    }

    private void createViewPager () {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        songsFragment = new SongsFragment();
        viewPagerAdapter.addFragments(songsFragment, "Songs");
        viewPagerAdapter.addFragments(new LocalSongFragment(this), "Playlist");
        viewPagerAdapter.addFragments(new AlbumFragment(), "Albums");
        viewPagerAdapter.addFragments(new SoundRecognitionFragment(), "Shazam");
        viewPagerAdapter.addFragments(new UserFragment(this), "User");

        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                localMusicFiles = getAllLocalAudio(this);
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
        viewPagerAdapter.addFragments(new LocalSongFragment(this), "Playlist");
        viewPagerAdapter.addFragments(new AlbumFragment(), "Albums");
        viewPagerAdapter.addFragments(new SoundRecognitionFragment(), "Shazam");
        viewPagerAdapter.addFragments(new UserFragment(this), "User");
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search_option);
        searchView = (SearchView) menuItem.getActionView();

        // Hiển thị thanh tìm kiếm mở rộng
        searchView.setIconifiedByDefault(false);

        // Ẩn biểu tượng kính lúp
        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        if (searchPlate != null) {
            int searchIconId = searchPlate.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
            View searchIcon = searchPlate.findViewById(searchIconId);
            if (searchIcon != null) {
                searchIcon.setVisibility(View.GONE);
            }
        }

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
        ArrayList<MusicFiles> myLocalFiles = new ArrayList<>();

        for (MusicFiles song : musicFiles) {
            if (song.getSongTitle().toLowerCase().contains(userInput)) {
                myFiles.add(song);
            }
        }
        SongsFragment.musicAdapter.updateList(myFiles);

        for (MusicFiles song : localMusicFiles) {
            if (song.getSongTitle().toLowerCase().contains(userInput)) {
                myLocalFiles.add(song);
            }
        }
        LocalSongFragment.musicAdapter2.updateList(myLocalFiles);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }


}