package com.example.musicpleir;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class LocalSongFragment extends Fragment {

    RecyclerView recyclerView;
    static LocalMusicAdapter musicAdapter2;
    Button reload;
    Context context;


    public LocalSongFragment() {
        // Required empty public constructor
    }

    public LocalSongFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //LocalMusicAdapter.mFiles = new ArrayList<>();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_local_song, container, false);
        recyclerView = view.findViewById(R.id.recyclerView2);

        reload = view.findViewById(R.id.reload);

        recyclerView.setHasFixedSize(true);
        if (!MainActivity.localMusicFiles.isEmpty()) {
            musicAdapter2 = new LocalMusicAdapter(getContext(), MainActivity.localMusicFiles);
            recyclerView.setAdapter(musicAdapter2);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.localMusicFiles = MainActivity.getAllLocalAudio(context);
                if(!MainActivity.localMusicFiles.isEmpty()) {
                    musicAdapter2.updateList(MainActivity.localMusicFiles);
                }
            }
        });
        return view;


    }
}