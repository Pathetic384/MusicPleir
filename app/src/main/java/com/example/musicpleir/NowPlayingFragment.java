package com.example.musicpleir;

import static android.content.Context.MODE_PRIVATE;
import static com.example.musicpleir.MainActivity.Artist_To_Mini;
import static com.example.musicpleir.MainActivity.Path_To_Mini;
import static com.example.musicpleir.MainActivity.Show_Mini_Player;
import static com.example.musicpleir.MainActivity.Song_To_Mini;
import static com.example.musicpleir.MusicService.Artist_Name;
import static com.example.musicpleir.MusicService.Music_File;
import static com.example.musicpleir.MusicService.Music_Last_Played;
import static com.example.musicpleir.MusicService.Song_Name;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

public class NowPlayingFragment extends Fragment implements ServiceConnection {

    ImageView nextBtn, albumArt, prevBtn;
    TextView artist, songName;
    FloatingActionButton playPauseBtn;
    View view;
    MusicService musicService;
    public NowPlayingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        artist = view.findViewById(R.id.song_artist_mini);
        songName = view.findViewById(R.id.song_name_mini);
        albumArt = view.findViewById(R.id.bottom_art);
        nextBtn = view.findViewById(R.id.skip_next_bottom);
        prevBtn = view.findViewById(R.id.skip_prev_bottom);
        playPauseBtn = view.findViewById(R.id.play_pause_mini);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "12", Toast.LENGTH_SHORT).show();
                if(musicService != null) {
                    try {musicService.nextBtnClicked();} catch (IOException e) {}
                    btnClicked();
                }
            }
        });
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "12", Toast.LENGTH_SHORT).show();
                if(musicService != null) {
                    try {musicService.prevBtnClicked();} catch (IOException e) {}
                    btnClicked();
                }
            }
        });
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "122", Toast.LENGTH_SHORT).show();
                if(musicService != null) {
                    try {musicService.playPauseBtnClicked();} catch (IOException e) {}
                    if(musicService.isPlaying()) {
                        playPauseBtn.setImageResource(R.drawable.ic_pause);
                    }
                    else playPauseBtn.setImageResource(R.drawable.ic_play);
                }
            }
        });

        return view;
    }

    void btnClicked() {
        if(getActivity() != null) {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(Music_Last_Played, MODE_PRIVATE).edit();
            editor.putString(Music_File, PlayerActivity.listSongs.get(musicService.position).getSongLink());
            editor.putString(Artist_Name, PlayerActivity.listSongs.get(musicService.position).getArtist());
            editor.putString(Song_Name, PlayerActivity.listSongs.get(musicService.position).getSongTitle());
            editor.apply();
            SharedPreferences preferences = getActivity().getSharedPreferences(Music_Last_Played, MODE_PRIVATE);
            String path = preferences.getString(Music_File, null);
            String artist_name = preferences.getString(Artist_Name, null);
            String song_name = preferences.getString(Song_Name, null);
            if(path != null) {
                Show_Mini_Player = true;
                Path_To_Mini = path;
                Artist_To_Mini = artist_name;
                Song_To_Mini = song_name;
            }
            else {
                Show_Mini_Player = false;
                Path_To_Mini = null;
                Artist_To_Mini = null;
                Song_To_Mini = null;
            }
            if(Show_Mini_Player) {
                if (Path_To_Mini != null) {
                    try {
                        byte[] art = Util.getAlbumArt(Path_To_Mini, new MediaMetadataRetriever());
                        if (art != null) {
                            Glide.with(getContext()).load(art).into(albumArt);
                        } else {
                            Glide.with(getContext()).load(R.drawable.pic).into(albumArt);
                        }
                        artist.setText(Artist_To_Mini);
                        songName.setText(Song_To_Mini);
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!MainActivity.showable) return;
        if(Show_Mini_Player) {
            if(Path_To_Mini != null) {
                try {
                    byte[] art = Util.getAlbumArt(Path_To_Mini, new MediaMetadataRetriever());
                    if(art != null) {
                        Glide.with(getContext()).load(art).into(albumArt);
                    }
                    else {
                        Glide.with(getContext()).load(R.drawable.pic).into(albumArt);
                    }
                    artist.setText(Artist_To_Mini);
                    songName.setText(Song_To_Mini);
                    Intent i = new Intent(getContext(), MusicService.class);
                    if( getContext() != null) {
                        getContext().bindService(i, this, Context.BIND_AUTO_CREATE);
                    }
                } catch (IOException e) {}
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if(!MainActivity.showable) return;
        if(getContext() != null) {
            getContext().unbindService(this);
        }
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if(!MainActivity.showable) return;
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }

}