package com.example.musicpleir;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Lyrics {
    private static final String API_KEY = "990d610252mshcfb719d49d5c783p1a23e1jsnd33c1f591dac";
    private static final String HOST = "genius-song-lyrics1.p.rapidapi.com";
    private static final OkHttpClient client = new OkHttpClient();

    public static void lyrics(String songTitle, LyricsCallback callback) {
        new SearchSongTask(callback).execute(songTitle);
    }

    private static class SearchSongTask extends AsyncTask<String, Void, Integer> {
        private LyricsCallback callback;

        SearchSongTask(LyricsCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                return searchSong(params[0]);
            } catch (Exception e) {
                Log.e("Lyrics", "Error during searchSong", e);
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer songId) {
            if (songId != -1) {
                new GetLyricsTask(callback).execute(songId);
            } else {
                callback.onLyricsRetrieved("Guess the lyrics yourself!!");
            }
        }
    }

    private static class GetLyricsTask extends AsyncTask<Integer, Void, String> {
        private LyricsCallback callback;

        GetLyricsTask(LyricsCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Integer... params) {
            try {
                return getLyrics(params[0]);
            } catch (Exception e) {
                Log.e("Lyrics", "Error during getLyrics", e);
                return "Error retrieving lyrics";
            }
        }

        @Override
        protected void onPostExecute(String lyrics) {
            callback.onLyricsRetrieved(lyrics);
        }
    }

    private static int searchSong(String title) throws Exception {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://genius-song-lyrics1.p.rapidapi.com/search/").newBuilder();
        urlBuilder.addQueryParameter("q", title);
        urlBuilder.addQueryParameter("per_page", "10");
        urlBuilder.addQueryParameter("page", "1");

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .addHeader("X-RapidAPI-Key", API_KEY)
                .addHeader("X-RapidAPI-Host", HOST)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseData = response.body().string();
            Log.d("Lyrics", "Search Response Data: " + responseData);

            JSONObject jsonObject = new JSONObject(responseData);
            if (jsonObject.has("hits")) {
                JSONArray hits = jsonObject.getJSONArray("hits");
                if (hits.length() > 0) {
                    JSONObject firstHit = hits.getJSONObject(0).getJSONObject("result");
                    return firstHit.getInt("id");
                }
            } else {
                Log.e("Lyrics", "No 'hits' key found in JSON");
            }
        } catch (Exception e) {
            Log.e("Lyrics", "Error during searchSong", e);
        }
        return -1;
    }

    private static String getLyrics(int songId) throws Exception {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://genius-song-lyrics1.p.rapidapi.com/song/lyrics/").newBuilder();
        urlBuilder.addQueryParameter("id", Integer.toString(songId));

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .addHeader("X-RapidAPI-Key", API_KEY)
                .addHeader("X-RapidAPI-Host", HOST)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseData = response.body().string();
            Log.d("Lyrics", "Lyrics Response Data: " + responseData);

            JSONObject jsonObject = new JSONObject(responseData);
            if (jsonObject.has("lyrics") && jsonObject.getJSONObject("lyrics").has("lyrics") &&
                    jsonObject.getJSONObject("lyrics").getJSONObject("lyrics").has("body") &&
                    jsonObject.getJSONObject("lyrics").getJSONObject("lyrics").getJSONObject("body").has("html")) {
                String lyrics = jsonObject.getJSONObject("lyrics").getJSONObject("lyrics").getJSONObject("body").getString("html");
                return lyrics.replaceAll("<br>", "\n").replaceAll("<[^>]+>", "");
            } else {
                Log.e("Lyrics", "Unexpected JSON structure in lyrics response");
            }
        } catch (Exception e) {
            Log.e("Lyrics", "Error during getLyrics", e);
        }
        return null;
    }

    public interface LyricsCallback {
        void onLyricsRetrieved(String lyrics);
    }
}
