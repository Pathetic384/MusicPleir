//package com.example.musicpleir;
//
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//
//public class SongsFragment extends Fragment {
//
//    RecyclerView recyclerView;
//    static MusicAdapter musicAdapter;
//
//
//    public SongsFragment() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//            // Inflate the layout for this fragment
//            View view = inflater.inflate(R.layout.fragment_songs, container, false);
//            recyclerView = view.findViewById(R.id.recyclerView);
//            recyclerView.setHasFixedSize(true);
//            if (!MainActivity.musicFiles.isEmpty()) {
//                musicAdapter = new MusicAdapter(getContext(), MainActivity.musicFiles);
//                recyclerView.setAdapter(musicAdapter);
//                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
//            }
//            return view;
//    }
//}
package com.example.musicpleir;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicpleir.databinding.FragmentSongsBinding;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.util.ArrayList;

public class SongsFragment extends Fragment implements MusicDataListener {

    private FragmentSongsBinding binding;
    static MusicAdapter musicAdapter;

//    EditText songFinding;
//    Button findEnter;

    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment using ViewBinding
        binding = FragmentSongsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Setup RecyclerView
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.recyclerView.setItemViewCacheSize(20); // Set cache size
        binding.recyclerView.setDrawingCacheEnabled(true);
        binding.recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // Check if music files are already loaded
        if (!MainActivity.musicFiles.isEmpty()) {
            musicAdapter = new MusicAdapter(getContext(), MainActivity.musicFiles);
            binding.recyclerView.setAdapter(musicAdapter);
        }

        return view;
    }

    // Method to update music list using DiffUtil
    public void updateMusicList(ArrayList<MusicFiles> newMusicFiles) {
        if (musicAdapter != null) {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MusicDiffCallback(musicAdapter.getMusicFiles(), newMusicFiles));
            musicAdapter.updateMusicFiles(newMusicFiles);
            diffResult.dispatchUpdatesTo(musicAdapter);
        }
    }

    @Override
    public void onMusicDataLoaded(ArrayList<MusicFiles> musicFiles) {
        // Update music list
        updateMusicList(musicFiles);
    }
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

// DiffUtil.Callback implementation for MusicFiles
class MusicDiffCallback extends DiffUtil.Callback {

    private final ArrayList<MusicFiles> oldList;
    private final ArrayList<MusicFiles> newList;

    public MusicDiffCallback(ArrayList<MusicFiles> oldList, ArrayList<MusicFiles> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override

    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getSongLink().equals(newList.get(newItemPosition).getSongLink());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}

