package com.example.musicpleir;

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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextConf;
    Button regButton;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    TextView note;

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
                    return;
                }
                if(TextUtils.isEmpty(password)) {
                    progressBar.setVisibility(View.GONE);
                    note.setText("Please enter your Password");
                    return;
                }
                if(TextUtils.isEmpty(conf_password)) {
                    progressBar.setVisibility(View.GONE);
                    note.setText("Please confirm your Password");
                    return;
                }
                if(!password.equals(conf_password)) {
                    progressBar.setVisibility(View.GONE);
                    note.setText("You didnt confirm your password correctly");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Register.this, "Account created",
                                            Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getApplicationContext(), Login.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    note.setText("Your mail must follow format: @gmail.com and the length of password must be >=6 letters");
                                }
                            }
                        });

            }
        });
    }
}