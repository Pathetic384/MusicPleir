package com.example.musicpleir;

import android.media.MediaMetadataRetriever;

import java.io.IOException;
import java.util.Objects;
import org.mockito.Mockito.*;

public class Util {
    public static byte[] getAlbumArt(String uri, MediaMetadataRetriever retriever) throws IOException {
        if (Objects.equals(uri, "")) return null;
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    public static MusicFiles stringToSong(String find) {
        for(MusicFiles song : MainActivity.musicFiles) {
            if(song.getSongTitle().toLowerCase().contains(find.toLowerCase())) {
                return song;
            }
        }
        return null;
    }

    public static String formattedTime(int mCurrentPosition) {
        String totalout = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition/60);
        totalout = minutes + ":" + seconds;
        totalNew = minutes + ":0" + seconds;
        if(seconds.length() == 1)
        {
            return totalNew;
        }
        else return totalout;
    }
}
