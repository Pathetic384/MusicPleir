package com.example.musicpleir;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    Button save, changeBtn, logout;
    TextView mail;
    EditText name;
    ImageView imageView;
    Context context;
    Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        mail = view.findViewById(R.id.user_email);
        logout = view.findViewById(R.id.logout_btn);
        mail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        save = view.findViewById(R.id.save_button);
        name = view.findViewById(R.id.user_name);
        changeBtn = view.findViewById(R.id.change_pass);
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
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangePassDialog(Gravity.CENTER);
            }
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
                if(get == null) get = "user";
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
                if(get!=null)
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

    void openChangePassDialog(int gravity) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_pass_dialog);

        Window window = dialog.getWindow();
        if(window == null) return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(true);

        Button change = dialog.findViewById(R.id.change);
        Button back = dialog.findViewById(R.id.back);
        TextView note = dialog.findViewById(R.id.note);
        TextInputEditText editTextPass1 = dialog.findViewById(R.id.current_pass);
        TextInputEditText editTextPass2 = dialog.findViewById(R.id.new_pass);
        TextInputEditText editTextPass3 = dialog.findViewById(R.id.confirm_pass);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String pass1 = String.valueOf(editTextPass1.getText());
                String pass2 = String.valueOf(editTextPass2.getText());
                String pass3 = String.valueOf(editTextPass3.getText());
                if(pass1.isEmpty() || pass2.isEmpty() || pass3.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    note.setText("Please enter all fields!");
                }
                else if(!pass2.equals(pass3)) {
                    progressBar.setVisibility(View.GONE);
                    note.setText("You didn't confirmed you new password correctly");
                }
                else {
                    FirebaseUser user;
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    final String email = user.getEmail();
                    AuthCredential credential = EmailAuthProvider.getCredential(email, pass1.toLowerCase().toString());

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                user.updatePassword(pass2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(!task.isSuccessful()){
                                            note.setText("Something went wrong, try again later");
                                            progressBar.setVisibility(View.GONE);
                                        }else {
                                            note.setText("Successfully changed your password");
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }else {
                                note.setText("authentication failed");
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}