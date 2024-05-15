package com.example.musicpleir;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class AlbumFragment extends Fragment {
    RecyclerView recyclerView;
    AlbumAdapter adapter;
    Button button;
    EditText editText;
    StorageReference mStorageref;

    DatabaseReference referenceSongs;
    MediaMetadataRetriever metadataRetriever;

    public AlbumFragment() {
        metadataRetriever = new MediaMetadataRetriever();
        referenceSongs = FirebaseDatabase.getInstance().getReference().child("songs");
        mStorageref = FirebaseStorage.getInstance().getReference().child("songs");
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        if(!MainActivity.albums.isEmpty()) {
            adapter = new AlbumAdapter(getContext(), MainActivity.albums);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
        button = view.findViewById(R.id.addAlbum);
        editText = view.findViewById(R.id.albumName);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAlbum();
            }
        });
        return view;
    }

    private void uploadAlbum() {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(MainActivity.userID).child(editText.getText().toString())
                .setValue(editText.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                MainActivity.getAllAlbum();
                ArrayList<String> newAlbum = MainActivity.albums;
                adapter.updateList(newAlbum);

            }
        });
        //mDatabase.child("users").child(MainActivity.userID).child("a1").child(MainActivity.musicFiles.get(6).songTitle).setValue(MainActivity.musicFiles.get(6));
    }
}