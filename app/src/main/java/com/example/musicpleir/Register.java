package com.example.musicpleir;

import static com.example.musicpleir.R.color.warningColor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextConf;
    Button regButton;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    TextView note;
    FirebaseAuth firebaseAuth;

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextConf = findViewById(R.id.conf_password);
        regButton = findViewById(R.id.register_btn);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        note = findViewById(R.id.note);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
                finish();
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, conf_password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                conf_password = String.valueOf(editTextConf.getText());

                if(TextUtils.isEmpty(email)) {
                    progressBar.setVisibility(View.GONE);
                    note.setText("Please enter your Email");
                    note.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warningColor));
                    return;
                }
                if(TextUtils.isEmpty(password)) {
                    progressBar.setVisibility(View.GONE);
                    note.setText("Please enter your Password");
                    note.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warningColor));
                    return;
                }
                if(TextUtils.isEmpty(conf_password)) {
                    progressBar.setVisibility(View.GONE);
                    note.setText("Please confirm your Password");
                    note.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warningColor));
                    return;
                }
                if(!password.equals(conf_password)) {
                    progressBar.setVisibility(View.GONE);
                    note.setText("Passwords do not match");
                    note.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warningColor));
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Runnable myRunnable = new Runnable(){
                                        public void run(){

                                            FirebaseUser user = task.getResult().getUser();
                                            String id1 = user.getUid();
                                            Addsong addsong = new Addsong(AuthenticateSpotify.oauth2.accessToken, AuthenticateSpotify.oauth2.PLAYLIST_ID);
                                            String playlist_id = addsong.createPlaylist();

                                            DatabaseReference mDatabase;
                                            mDatabase = FirebaseDatabase.getInstance().getReference();
                                            mDatabase.child("rcm-id").child(id1).setValue(playlist_id).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(Register.this, "Account created",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            mDatabase.child("users").child(MainActivity.userID).child("base_album")
                                                    .setValue("base_album");
                                        }
                                    };
                                    Thread thread = new Thread(myRunnable);
                                    thread.start();



                                    progressBar.setVisibility(View.GONE);

                                    Intent i = new Intent(getApplicationContext(), Login.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    note.setText("Your mail must follow format: @gmail.com and the length of password must be >=6 letters" +
                                            ", or you mail has been used already");
                                    note.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.warningColor));
                                }
                            }
                        });

            }
        });
    }

}