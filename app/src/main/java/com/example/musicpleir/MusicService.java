package com.example.musicpleir;


import static com.example.musicpleir.PlayerActivity.listSongs;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener{
    private static final String CHANNEL_ID = "channel2";
    private static final String NOTIFICATION_CHANNEL_ID_SERVICE = "123";
    private static final String NOTIFICATION_CHANNEL_ID_INFO = "456";
    IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    //ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    Uri uri;
    int position = -1;
    ActionPlaying actionPlaying;
    MediaSessionCompat mediaSessionCompat;
    public static final String Music_Last_Played = "Last_Played";
    public static final String Music_File = "Stored_Music";
    public static final String Artist_Name = "Artist Name";
    public static final String Song_Name = "Song Name";

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "my audio");
        //musicFiles = listSongs;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_SERVICE, "App Service", NotificationManager.IMPORTANCE_DEFAULT));
            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_INFO, "Download Info", NotificationManager.IMPORTANCE_DEFAULT));
        }

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
        String actionName = intent.getStringExtra("ActionName");
        //musicFiles = listSongs;
        if(myPos != -1) {
            playMedia(myPos);
        }
        if(actionName!= null && actionPlaying!=null) {
            switch(actionName) {
                case "playPause":
                    Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                    try {playPauseBtnClicked();} catch (Exception e) {}
                    break;
                case "next":
                    Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
                    try {nextBtnClicked();} catch (IOException e) {}
                    break;
                case "previous":
                    Toast.makeText(this, "3", Toast.LENGTH_SHORT).show();
                    try {prevBtnClicked();} catch (IOException e) {}
                    break;
            }
        }
        return START_STICKY;
    }

    private void playMedia(int startPosition) {
        //musicFiles = listSongs;
        position = startPosition;
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(listSongs != null) {
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
        //musicFiles = listSongs;
        Log.d("ádkjhsdfnjkjklsdfji", String.valueOf(listSongs));
        position = pos;
        Log.e("pos", String.valueOf(position));
        Log.e("12123", String.valueOf(listSongs.get(position)));
        Log.e("12123", String.valueOf(listSongs.get(position).getSongLink()));
        uri = Uri.parse(listSongs.get(position).getSongLink());
        Log.e("12123333", String.valueOf(uri));
        SharedPreferences.Editor editor = getSharedPreferences(Music_Last_Played, MODE_PRIVATE).edit();
        editor.putString(Music_File, uri.toString());
        editor.putString(Artist_Name, listSongs.get(position).getArtist());
        editor.putString(Song_Name, listSongs.get(position).getSongTitle());
        editor.apply();
//        mediaPlayer = new MediaPlayer();
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
        if(actionPlaying != null) {
            try {
                actionPlaying.nextBtnClicked();
                if(mediaPlayer != null) {
                    createMediaPlayer(position);
                    start();
                    onCompleted();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    void setCallBack(ActionPlaying actionPlaying) {
        this.actionPlaying = actionPlaying;
    }

    void showNotification (int playPauseBtn) throws IOException {
        Intent prevI = new Intent(this, NotificationReceiver.class).setAction("ACTION_PREVIOUS");
        PendingIntent prevIntent = PendingIntent.getBroadcast(this, 0, prevI, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent pauseI = new Intent(this, NotificationReceiver.class).setAction("ACTION_PLAY");
        PendingIntent pauseIntent = PendingIntent.getBroadcast(this, 0, pauseI, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent nextI = new Intent(this, NotificationReceiver.class).setAction("ACTION_NEXT");
        PendingIntent nextIntent = PendingIntent.getBroadcast(this, 0, nextI, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        byte[] picture = null;
        picture = getAlbumArt(listSongs.get(position).getSongLink());
        Bitmap thumb = null;
        if(picture != null) {
            thumb = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        }
        else {
            thumb = BitmapFactory.decodeResource(getResources(), R.drawable.pic);
        }
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_play)
                .setLargeIcon(thumb)
                .setContentTitle(listSongs.get(position).getSongTitle())
                .setContentText(listSongs.get(position).getArtist())
                .addAction(R.drawable.ic_skip_previous, "Previous", prevIntent)
                .addAction(R.drawable.ic_play, "Pause", pauseIntent)
                .addAction(R.drawable.ic_skip_next, "Next", nextIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        Notification notification = notificationBuilder.build();

        startForeground(2, notification);



//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//        Notification notification = notificationBuilder.setOngoing(true)
//                .setSmallIcon(R.drawable.ic_play)
//                .setContentTitle("App is running in background")
//                .setPriority(NotificationManager.IMPORTANCE_MIN)
//                .setCategory(Notification.CATEGORY_SERVICE)
//                .build();
//        startForeground(2, notification);

    }

    private byte[] getAlbumArt (String uri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    void nextBtnClicked() throws IOException {
        actionPlaying.nextBtnClicked();
    }
    void prevBtnClicked() throws IOException {
        actionPlaying.prevBtnClicked();
    }
    void playPauseBtnClicked() throws IOException {
        actionPlaying.playPauseBtnClicked();
    }
}
