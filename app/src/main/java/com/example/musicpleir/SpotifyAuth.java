package com.example.musicpleir;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;

public class SpotifyAuth {
    private static final String clientId = "f923bc7764514e16bb92e743ac90333d";
    private static final String clientSecret = "e4e1b273ce264d76bf7fc2f5a5a1b648";

    public static String getAccessToken() {
        String accessToken = "";
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();

        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();

        try {
            ClientCredentials clientCredentials = null;
            try {
                clientCredentials = clientCredentialsRequest.execute();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // The access token
            accessToken = clientCredentials.getAccessToken();

        } catch (IOException | SpotifyWebApiException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println(accessToken);
        return accessToken;
    }
}
