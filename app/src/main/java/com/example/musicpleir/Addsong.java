package com.example.musicpleir;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Addsong {

    public String accessToken;

    public String PLAYLIST_ID;

    public Addsong(String accessToken, String PLAYLIST_ID) {
        this.accessToken = accessToken;
        this.PLAYLIST_ID = PLAYLIST_ID;
    }
    public void addSongToPlaylist(String songName) {
        OkHttpClient client = new OkHttpClient();

        // Tìm kiếm track ID của bài hát
        searchTrack(songName, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String responseBody = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseBody);
                    JSONArray tracks = json.getJSONObject("tracks").getJSONArray("items");
                    if (tracks.length() > 0) {
                        String trackId = tracks.getJSONObject(0).getString("id");
                        // Thêm bài hát vào playlist
                        addTrackToPlaylist(trackId);
                    } else {
                        System.out.println("Track not found");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void searchTrack(String songName, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.spotify.com/v1/search").newBuilder();
        urlBuilder.addQueryParameter("q", songName);
        urlBuilder.addQueryParameter("type", "track");
        urlBuilder.addQueryParameter("limit", "1");

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(callback);
    }

    private void addTrackToPlaylist(String trackId) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl url = HttpUrl.parse("https://api.spotify.com/v1/playlists/" + PLAYLIST_ID + "/tracks").newBuilder().build();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("uris", new JSONArray().put("spotify:track:" + trackId));
        } catch (Exception e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                System.out.println("Track added to playlist successfully");
            }
        });
    }

    public String createPlaylist() {
        String playlistId = "";
        try {
            String userId = getCurrentUserId();
            System.out.println("Spotify User ID: " + userId);

            playlistId = createPlaylist(userId, "My New Playlist", "This is a new playlist.");
            return playlistId;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return playlistId;
    }
    public String getCurrentUserId() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .get()
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            JSONObject responseBody = null;
            try {
                responseBody = new JSONObject(response.body().string());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return responseBody.getString("id");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String createPlaylist(String userId, String name, String description) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("description", description);
            json.put("public", false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/users/" + userId + "/playlists")
                .post(body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            JSONObject responseBody = null;
            try {
                responseBody = new JSONObject(response.body().string());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return responseBody.getString("id");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
