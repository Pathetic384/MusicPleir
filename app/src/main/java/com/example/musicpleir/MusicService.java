package com.example.musicpleir;

import static com.example.musicpleir.PlayerActivity.listSongs;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener{
    IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    Uri uri;
    int position = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        musicFiles = listSongs;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "method");
        return mBinder;
    }

    public class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPos = intent.getIntExtra("servicePosition", -1);
        if(myPos != -1) {
            playMedia(myPos);
        }
        return START_STICKY;
    }

    private void playMedia(int startPosition) {
        musicFiles = listSongs;
        position = startPosition;
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(musicFiles != null) {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        }
        else {
            createMediaPlayer(position);
            mediaPlayer.start();
        }
    }

    void start() {
        mediaPlayer.start();
    }
    boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
    void stop() {
        mediaPlayer.stop();
    }
    void release() {
        mediaPlayer.release();
    }
    int getDuration() {
        return mediaPlayer.getDuration();
    }
    void seekTo (int pos) {
        mediaPlayer.seekTo(pos);
    }
    int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }
    void createMediaPlayer(int pos) {
        uri = Uri.parse(musicFiles.get(pos).getSongLink());
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }
    void pause() {
        mediaPlayer.pause();
    }
    void onCompleted() {
        mediaPlayer.setOnCompletionListener(this);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}
