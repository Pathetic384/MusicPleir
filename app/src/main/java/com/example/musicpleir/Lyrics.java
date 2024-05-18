package com.example.musicpleir;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Lyrics {
    private static final String API_KEY = "990d610252mshcfb719d49d5c783p1a23e1jsnd33c1f591dac";
    private static final String HOST = "genius-song-lyrics1.p.rapidapi.com";

    public String lyrics(String songTitle) throws Exception {
        String songId = Integer.toString(searchSong(songTitle));
        if (songId != null) {
            String htmlLyrics = getLyrics(songId);
            String lyrics = htmlLyrics.replaceAll("<[^>]+>", "");
            return lyrics;
        } else {
            return ("Song not found");
        }
    }

    private static int searchSong(String title) throws Exception {
        OkHttpClient client = new OkHttpClient();

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
            System.out.println("Search Response Data: " + responseData); // Print the response data for debugging
            JSONObject jsonObject = new JSONObject(responseData);


            if (jsonObject.has("hits")) {
                JSONArray hits = jsonObject.getJSONArray("hits");

                if (hits.length() > 0) {
                    JSONObject firstHit = hits.getJSONObject(0);
                    return firstHit.getJSONObject("result").getInt("id");
                }
            }
            else {
                System.out.println("No 'response' key found in JSON");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static String getLyrics(String songId) throws Exception {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://genius-song-lyrics1.p.rapidapi.com/song/lyrics/").newBuilder();
        urlBuilder.addQueryParameter("id", songId);

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .addHeader("X-RapidAPI-Key", API_KEY)
                .addHeader("X-RapidAPI-Host", HOST)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseData = response.body().string();
            System.out.println("Lyrics Response Data: " + responseData); // Print the response data for debugging
            JSONObject jsonObject = new JSONObject(responseData);
            return jsonObject.getJSONObject("lyrics").getJSONObject("lyrics").getJSONObject("body").getString("html");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
