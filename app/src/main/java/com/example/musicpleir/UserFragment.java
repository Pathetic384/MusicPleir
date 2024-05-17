package com.example.musicpleir;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.Nullable;


public class UserFragment extends Fragment {


    public UserFragment() {
        // Required empty public constructor
    }
    public UserFragment(Context context) {
        this.context = context;
    }
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 2;
    Button save;
    EditText name;
    ImageView imageView;
    Context context;
    Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        TextView mail = view.findViewById(R.id.user_mail);
        Button logout = view.findViewById(R.id.logout_btn);
        mail.setText(MainActivity.userMail);
        save = view.findViewById(R.id.save_button);
        name = view.findViewById(R.id.user_name);
        updatePicValue();
        updateNameValue();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getContext(), Login.class);
                startActivity(i);
                //finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
        imageView = view.findViewById(R.id.imageView);
        imageView.setOnClickListener(v -> {
        //    if (checkStoragePermission()) {
                openImagePicker();
//            } else {
//                requestStoragePermission();
//            }
        });
        return view;
    }

    private void saveData() {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user-profile").child(MainActivity.userID).child("name")
                .setValue(name.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        updateNameValue();

                    }
                });

    }

    void updateNameValue() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userProfileRef = database.getReference("user-profile").child(MainActivity.userID).child("name");

        // Read from the database
        userProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String get = snapshot.getValue(String.class);
                name.setText(get);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void updatePicValue() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userProfileRef = database.getReference("user-profile").child(MainActivity.userID).child("pic");

        // Read from the database
        userProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String get = snapshot.getValue(String.class);
                Glide.with(getContext()).load(get).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            // Get a reference to the Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

            // Upload the image
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL
                        imageRef.getDownloadUrl().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                // Save the download URL to Firebase Realtime Database
                                saveImageUrlToDatabase(downloadUri.toString());
                            } else {
                                Toast.makeText(getContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Image Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveImageUrlToDatabase(String imageUrl) {
        // Get a reference to the Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference("user-profile").child(MainActivity.userID);

        // Save the image URL under the 'pic' node
        databaseRef.child("pic").setValue(imageUrl)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Image URL Saved to Database", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to Save Image URL", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
           imageUri = data.getData();
            // Use Glide or any image loading library to set the selected image to ImageView
            Glide.with(this).load(imageUri).into(imageView);
            uploadImageToFirebase();
        }
    }
}