package com.example.musicpleir;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


public class SongsFragment extends Fragment {

    RecyclerView recyclerView;
    static MusicAdapter musicAdapter;
//    EditText songFinding;
//    Button findEnter;

    public SongsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_songs, container, false);
            recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            if (!MainActivity.musicFiles.isEmpty()) {
                musicAdapter = new MusicAdapter(getContext(), MainActivity.musicFiles);
                recyclerView.setAdapter(musicAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            }
//        songFinding = view.findViewById(R.id.song_find);
//        findEnter = view.findViewById(R.id.enter_find);
//        findEnter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ArrayList<MusicFiles> myFiles = new ArrayList<>();
//                for(MusicFiles song : MainActivity.musicFiles) {
//                    if(song.getSongTitle().toLowerCase().contains(songFinding.getText().toString().toLowerCase())) {
//                        myFiles.add(song);
//                    }
//                }
//                musicAdapter.updateList(myFiles);
//            }
//        });
            return view;


    }
}