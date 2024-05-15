package com.example.musicpleir;

public class MusicFiles {
    String songsCategory, songTitle, artist, songDuration, songLink, mKey;
    //String path, title, artist, album, duration, mKey;


    public MusicFiles(String songsCategory, String songTitle, String artist,  String songDuration, String songLink) {
        this.songsCategory = songsCategory;
        this.songTitle = songTitle;
        this.artist = artist;
        this.songDuration = songDuration;
        this.songLink = songLink;
    }


    public String getSongsCategory() {
        return songsCategory;
    }

    public void setSongsCategory(String songsCategory) {
        this.songsCategory = songsCategory;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }


    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    public MusicFiles() {
    }
}
