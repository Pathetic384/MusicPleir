package com.example.musicpleir;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.CodeVerifierUtil;
import net.openid.appauth.ResponseTypeValues;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OAuth2 {
    public String accessToken;
    private static final String CLIENT_ID = "f923bc7764514e16bb92e743ac90333d";
    private static final String CLIENT_SECRET = "e4e1b273ce264d76bf7fc2f5a5a1b648";
    private static final String REDIRECT_URI = "myapp://callbacks";
    private static final String AUTHORIZATION_ENDPOINT = "https://accounts.spotify.com/authorize";
    private static final String TOKEN_ENDPOINT = "https://accounts.spotify.com/api/token";
    private static final String SCOPES = "playlist-modify-public playlist-modify-private user-library-read playlist-read-private playlist-read-collaborative";

    public static String PLAYLIST_ID = "1saF6mV7QYRRO6vQUiglmW";
    public CompletableFuture<String> authorizationComplete = new CompletableFuture<>();
    private AuthorizationService authService;
    private String codeVerifier;

    private Activity activity;

    public OAuth2(Activity activity) {
        this.activity = activity;
    }


    public void exchangeAuthorizationCode(String authorizationCode) {
        OkHttpClient client = new OkHttpClient();

        // Tạo request body với các thông tin cần thiết
        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", authorizationCode)
                .add("redirect_uri", REDIRECT_URI)
                .add("client_id", CLIENT_ID)
                .add("client_secret", CLIENT_SECRET)
                .add("code_verifier", codeVerifier)
                .build();

        // Tạo request
        Request request = new Request.Builder()
                .url(TOKEN_ENDPOINT)
                .post(formBody)
                .build();

        // Thực hiện request
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

                String responseBody = response.body().string();
                try {
                    // Xử lý phản hồi JSON
                    JSONObject json = new JSONObject(responseBody);
                    accessToken = json.getString("access_token");

                    authorizationComplete.complete(accessToken);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Đánh dấu rằng việc trao đổi authorization đã thất bại
                    authorizationComplete.completeExceptionally(e);
                }

            }

        });
    }

    public void authenticate() {
        AuthorizationServiceConfiguration serviceConfig = new AuthorizationServiceConfiguration(
                Uri.parse(AUTHORIZATION_ENDPOINT), // authorization endpoint
                Uri.parse(TOKEN_ENDPOINT)); // token endpoint

        codeVerifier = CodeVerifierUtil.generateRandomCodeVerifier();
        String codeChallenge = CodeVerifierUtil.deriveCodeVerifierChallenge(codeVerifier);

        AuthorizationRequest.Builder authRequestBuilder = new AuthorizationRequest.Builder(
                serviceConfig,
                CLIENT_ID,
                ResponseTypeValues.CODE,
                Uri.parse(REDIRECT_URI))
                .setScopes(SCOPES)
                .setCodeVerifier(codeVerifier);

        AuthorizationRequest authRequest = authRequestBuilder.build();
        authService = new AuthorizationService(activity);

        Intent authIntent = authService.getAuthorizationRequestIntent(authRequest);
        activity.startActivityForResult(authIntent, 0);
    }
}
