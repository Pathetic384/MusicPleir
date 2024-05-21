package com.example.musicpleir;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AuthenticateSpotify extends AppCompatActivity {
    static OAuth2 oauth2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        oauth2 = new OAuth2(this);
        oauth2.authenticate();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {
            String authorizationCode = uri.getQueryParameter("code");
            if (authorizationCode != null) {
                // Sử dụng authorization code để trao đổi lấy access token
                oauth2.exchangeAuthorizationCode(authorizationCode);

                oauth2.authorizationComplete.thenRun(() -> {
                    System.out.println("Access token: " + oauth2.accessToken);
                    Addsong addsong = new Addsong(oauth2.accessToken, oauth2.PLAYLIST_ID);
                    addsong.addSongToPlaylist("Ditto");
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                });

                System.out.println("Access token: " + oauth2.accessToken);

            } else {
                // Xử lý khi không có authorization code được trả về
                System.out.println("Authorization Code not found in the URI");
            }
        }
    }
}
