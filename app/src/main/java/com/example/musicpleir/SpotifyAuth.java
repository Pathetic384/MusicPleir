package com.example.musicpleir;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import org.apache.hc.core5.http.ParseException;

import java.io.IOException;

public class SpotifyAuth {
    private static final String clientId = "1c485840565142e6a7713e9d00780e1d";
    private static final String clientSecret = "c72373e2a4df4938a53433d7cd4206f1";

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
