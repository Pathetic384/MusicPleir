package com.example.musicpleir;


import static com.example.musicpleir.MainActivity.musicFiles;
import static com.example.musicpleir.MainActivity.userMail;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements  ActionPlaying, ServiceConnection {

    public TextView song_name, artist_name, duration_played, duration_total;
    public ImageView cover_art, nextBtn, prevBtn, backBtn, shuffleBtn, repeatBtn, addAlbum;
    public FloatingActionButton playpauseBtn;
    public SeekBar seekBar;
    static int position = -1;
    static Uri uri;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;
    MusicService musicService;
    static String selectedAlbum;
    public static boolean loading = false;
    Button lyricsButton;
    Dialog dialog;
    static String sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.showable = true;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();
        initViews();
        try {
            getIntentMethod();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(musicService != null && fromUser) {
                    musicService.seekTo(progress * 1000);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(musicService != null) {
                int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                seekBar.setProgress(mCurrentPosition);
                duration_played.setText(Util.formattedTime(mCurrentPosition));
                //Log.e("playy", String.valueOf(musicService.isPlaying()));
                }
                handler.postDelayed(this, 1000);
            }
        });
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.shuffleBoolean) {
                    MainActivity.shuffleBoolean = false;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_off);
                }
                else {
                    MainActivity.shuffleBoolean = true;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.repeatBoolean) {
                    MainActivity.repeatBoolean = false;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_off);
                }
                else {
                    MainActivity.repeatBoolean = true;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_on);
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFeedbackDialog(Gravity.CENTER);
            }
        });
        lyricsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openLyricsDialog(Gravity.CENTER);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    void openLyricsDialog(int gravity) throws Exception {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.lyrics_dialog);

        Window window = dialog.getWindow();
        if (window == null) return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(true);

        Button back = dialog.findViewById(R.id.back);
        TextView lyrics = dialog.findViewById(R.id.lyric_dialog);
        ProgressBar progressBar = dialog.findViewById(R.id.lyrics_progress);

        progressBar.setVisibility(View.VISIBLE);

        if (back == null || lyrics == null) {
            throw new NullPointerException("Dialog views not properly initialized.");
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        String search = "";
        if(listSongs.get(position).getArtist() != null) {
            search = listSongs.get(position).songTitle.trim() + " " + listSongs.get(position).getArtist().trim();
        }
        else search = listSongs.get(position).songTitle.trim();


        if(!Objects.equals(MainActivity.userMail, "tester@gmail.com")) {

            Lyrics.lyrics(search, new Lyrics.LyricsCallback() {
                @Override
                public void onLyricsRetrieved(final String songlyrics) {
                    // Ensure UI update on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lyrics.setText(songlyrics);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

            });
        }
        else {
            lyrics.setText("[Intro]\n" + "Ooh\n" + "Na-na, yeah\n" + "\n" + "[Verse 1]\n" + "I saw you dancing in a crowded room (Uh)\n" + "You look so happy when I'm not with you\n" + "But then you saw me, caught you by surprise\n" + "A single teardrop falling from your eye\n" + "\n" + "[Refrain]\n" + "I don't know why I run away\n" +
                    "I make you cry when I run away\n" + "\n" + "[Verse 2]\n" + "You could've asked me why I broke your heart\n" +
                    "You could've told me that you fell apart\n" + "But you walked past me like I wasn't there\n" + "And just pretended like you didn't care\n" + "\n" + "[Refrain]\n" +
                    "I don't know why I run away\n" + "I make you cry when I run away\n" + "\n" + "[Pre-Chorus]\n" + "Take me back 'cause I wanna stay\n" + "Save your tears for another\n" + "\n" + "[Chorus]\n" + "Save your tears for another day\n" + "Save your tears for another day (So)\n" + "\n" + "[Verse 3]\n" + "I made you think that I would always stay\n" + "I said some things that I should never say\n" +
                    "Yeah, I broke your heart like someone did to mine\n" + "And now you won't love me for a second time\n" + "\n" + "[Refrain]\n" + "I don't know why I run away, oh, girl\n" + "Said, I make you cry when I run away\n" + "\n" + "[Pre-Chorus]\n" + "Girl, take me back 'cause I wanna stay\n" + "Save your tears for another\n" + "I realize that I'm much too late\n" +
                    "And you deserve someone better\n" + "\n" + "[Chorus]\n" + "Save your tears for another day (Oh yeah)\n" + "Save your tears for another day (Yeah)\n" + "\n" + "[Refrain]\n" + "I don't know why I run away\n" + "I'll make you cry when I run away\n" + "\n" + "[Chorus]\n" + "Save your tears for another day\n" + "Ooh, girl, I said (Ah)\n" + "Save your tears for another day (Ah)\n" + "\n" + "[Outro]\n" + "Save your tears for another day (Ah)\n" + "Save your tears for another day (Ah)");
            progressBar.setVisibility(View.GONE);
        }

        dialog.show();
    }



    void openFeedbackDialog(int gravity) {
        if(Objects.equals(sender, "local")) {
            Toast.makeText(PlayerActivity.this, "Local songs can't be added to albums", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog);

        Window window = dialog.getWindow();
        if(window == null) return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(true);

        Button saveBtn = dialog.findViewById(R.id.heyya);
        Button back = dialog.findViewById(R.id.back);
        Spinner spinner = dialog.findViewById(R.id.albumSpinner);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("users").child(MainActivity.userID).child(selectedAlbum)
                        .child(listSongs.get(position).songTitle).setValue(listSongs.get(position)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(PlayerActivity.this, "Song added", Toast.LENGTH_SHORT).show();
                            }
                        });
                //Toast.makeText(PlayerActivity.this, "aaaccc", Toast.LENGTH_SHORT).show();

                if(!Objects.equals(userMail, "tester@gmail.com") && Register.rcm) {
                    Addsong addsong = new Addsong(AuthenticateSpotify.oauth2.accessToken, AuthenticateSpotify.oauth2.PLAYLIST_ID);
                    addsong.addSongToPlaylist(listSongs.get(position).songTitle);
                }
                if(Register.rcm) {
                    new MainActivity.RecommenderTask().execute();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                selectedAlbum = item;
                Toast.makeText(PlayerActivity.this, item + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, MainActivity.albums);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner.setAdapter(adapter);

        dialog.show();
    }

    @Override
    protected void onResume() {
        Intent i = new Intent(this, MusicService.class);
        bindService(i,this, BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(dialog!= null) dialog.dismiss();
        unbindService(this);
    }

    private void prevThreadBtn() {
        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            prevBtnClicked();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        };
        prevThread.start();
    }

    void MediaThread(int i) {
        musicService.stop();
        musicService.release();
        PlayerActivity.this.runOnUiThread( () -> {
            if(musicService != null) {
                //int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                seekBar.setProgress(0);
            }
        } );
        if(i==0) {
            musicService.createMediaPlayer(position);
            musicService.onCompleted();
        }
        try {
            musicService.showNotification(R.drawable.ic_pause);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(i==0) {
            musicService.start();
        }
        seekBar.setMax(musicService.getDuration() / 1000);
        loading = false;
    }

    public void prevBtnClicked() throws IOException {

        loading = true;
        playpauseBtn.setImageResource(R.drawable.ic_pause);
        if(MainActivity.shuffleBoolean && !MainActivity.repeatBoolean) {
            position = getRandom(listSongs.size() - 1);
        }
        else if(!MainActivity.shuffleBoolean && !MainActivity.repeatBoolean) {
            position = ( (position - 1) <0 ) ? (listSongs.size() - 1) : (position-1);
        }
        uri = Uri.parse(listSongs.get(position).getSongLink());
        metaData(uri, new MediaMetadataRetriever());
        song_name.setText(listSongs.get(position).getSongTitle());
        artist_name.setText(listSongs.get(position).getArtist());

        Runnable myRunnable = new Runnable(){
            public void run(){
                MediaThread(0);
            }
        };
        Thread thread = new Thread(myRunnable);
        thread.start();
    }

    private void nextThreadBtn() {
        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            nextBtnClicked();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        };
        nextThread.start();
    }


    public void nextBtnClicked() throws IOException {
        loading = true;
        playpauseBtn.setImageResource(R.drawable.ic_pause);
            if(MainActivity.shuffleBoolean && !MainActivity.repeatBoolean) {
                position = getRandom(listSongs.size() - 1);
            }
            else if(!MainActivity.shuffleBoolean && !MainActivity.repeatBoolean) {
                position = ((position + 1) % listSongs.size());
            }
            uri = Uri.parse(listSongs.get(position).getSongLink());
            metaData(uri, new MediaMetadataRetriever());
            song_name.setText(listSongs.get(position).getSongTitle());
            artist_name.setText(listSongs.get(position).getArtist());

            Runnable myRunnable = new Runnable(){
                public void run(){
                    MediaThread(0);
                }
            };
            Thread thread = new Thread(myRunnable);
            thread.start();

    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i+1);
    }

    private void playThreadBtn() {
        playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playpauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            playPauseBtnClicked();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        };
        playThread.start();
    }

    public void playPauseBtnClicked() throws IOException {
        if(musicService.isPlaying()) {
            playpauseBtn.setImageResource(R.drawable.ic_play);
            musicService.showNotification(R.drawable.ic_play);
            NowPlayingFragment.playPauseBtn.setImageResource(R.drawable.ic_play);
            musicService.pause();
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread( () -> {
                if(musicService != null) {
                    int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                }
            } );
        }
        else {
            musicService.showNotification(R.drawable.ic_pause);
            playpauseBtn.setImageResource(R.drawable.ic_pause);
            NowPlayingFragment.playPauseBtn.setImageResource(R.drawable.ic_pause);
            musicService.start();
            seekBar.setMax(musicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread( () -> {
                if(musicService != null) {
                    int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                }
            } );
        }
    }

    @Override
    public void endClicked() throws IOException {
        loading = true;
        playpauseBtn.setImageResource(R.drawable.ic_pause);
        if(MainActivity.shuffleBoolean && !MainActivity.repeatBoolean) {
            position = getRandom(listSongs.size() - 1);
        }
        else if(!MainActivity.shuffleBoolean && !MainActivity.repeatBoolean) {
            position = ((position + 1) % listSongs.size());
        }
        uri = Uri.parse(listSongs.get(position).getSongLink());
        metaData(uri, new MediaMetadataRetriever());
        song_name.setText(listSongs.get(position).getSongTitle());
        artist_name.setText(listSongs.get(position).getArtist());

        Runnable myRunnable = new Runnable(){
            public void run(){
                MediaThread(1);
            }
        };
        Thread thread = new Thread(myRunnable);
        thread.start();
    }


    private void getIntentMethod() throws IOException {
        position = getIntent().getIntExtra("position",-1);
        Log.d("PlayerActivity", "Received position: " + position);
        sender = getIntent().getStringExtra("sender");
        if(sender!= null && sender.equals("albumDetails")) {
            listSongs = AlbumDetailsAdapter.albumFiles;
        }
        else if(sender != null && sender.equals("local")) {
            listSongs = LocalMusicAdapter.mFiles;
        }
        else if (sender != null && sender.equals("fire")){
            listSongs = MusicAdapter.mFiles;
        }
        else if (sender != null && sender.equals("rcmDetails")){
            listSongs = RecommendDetails.albumSongs;
        }
        if(listSongs != null && position >= 0 && position < listSongs.size()) { // Check if position is valid
            playpauseBtn.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listSongs.get(position).getSongLink());
        }
        else {
            throw new RuntimeException("Invalid position");
        }
        Intent i = new Intent(this,MusicService.class);
        i.putExtra("servicePosition", position);
        startService(i);
    }


    private void initViews() {
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.song_artist);
        duration_played = findViewById(R.id.durationPlayed);
        duration_total = findViewById(R.id.durationTotal);
        cover_art = findViewById(R.id.cover_art);
        nextBtn = findViewById(R.id.id_next);
        prevBtn = findViewById(R.id.id_prev);
        backBtn = findViewById(R.id.back_btn);
        shuffleBtn = findViewById(R.id.id_shuffle);
        repeatBtn = findViewById(R.id.id_repeat);
        playpauseBtn = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekBar);
        addAlbum = findViewById(R.id.addAlbum);
        lyricsButton = findViewById(R.id.lyrics);

    }

    private void metaData (Uri uri, MediaMetadataRetriever retriever) {
        if(Objects.equals(MainActivity.userMail, "tester@gmail.com")) return;
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt( listSongs.get(position).getSongDuration() ) / 1000;
        duration_total.setText(Util.formattedTime(durationTotal));
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if(art != null) {
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            ImageAnimation(this, cover_art, bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if(swatch != null) {
                        ImageView gradient = findViewById(R.id.imageViewGradient);
                        ConstraintLayout mContainer = findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), 0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(swatch.getTitleTextColor());
                        artist_name.setTextColor(swatch.getBodyTextColor());
                    }
                    else {
                        ImageView gradient = findViewById(R.id.imageViewGradient);
                        ConstraintLayout mContainer = findViewById(R.id.mContainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xff000000, 0x00000000});
                        gradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xff000000, 0xff000000});
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.GRAY);
                    }
                }
            });
        }
        else {
            bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.pic);
            ImageAnimation(this, cover_art, bitmap);

            ImageView gradient = findViewById(R.id.imageViewGradient);
            ConstraintLayout mContainer = findViewById(R.id.mContainer);
            gradient.setBackgroundResource(R.drawable.gradient_bg);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            song_name.setTextColor(Color.WHITE);
            artist_name.setTextColor(Color.GRAY);
        }
    }
    public void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap) {
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
        musicService.setCallBack(this);
        //Toast.makeText(musicService, "connected", Toast.LENGTH_SHORT).show();
        seekBar.setMax(musicService.getDuration() / 1000);
        metaData(uri, new MediaMetadataRetriever());
        song_name.setText(listSongs.get(position).getSongTitle());
        artist_name.setText(listSongs.get(position).getArtist());
        musicService.onCompleted();
        try {musicService.showNotification(R.drawable.ic_pause);} catch (IOException e) {}
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }
}