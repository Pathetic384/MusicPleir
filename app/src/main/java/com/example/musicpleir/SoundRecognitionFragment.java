package com.example.musicpleir;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SoundRecognitionFragment extends Fragment {

    private Button StartRecording, Play;
    private TextView infoText;
    private ImageView coverArt;
    private MediaRecorder mediaRecorder;
    private String AudioSavePath = null;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String coverImageUrl = "https://ih1.redbubble.net/image.3800727619.4531/st,small,845x845-pad,1000x1000,f8f8f8.jpg";
    private Boolean playBtnEnabled = false;
    private String title = null;

    public SoundRecognitionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sound_recognition, container, false);

        // Inflate the layout for this fragment
        StartRecording = view.findViewById(R.id.start_recording);
        Play = view.findViewById(R.id.play_btn);
        Play.setEnabled(playBtnEnabled);
        infoText = view.findViewById(R.id.info_text);
        coverArt = view.findViewById(R.id.cover_art);
        Glide.with(this)
                .load(coverImageUrl)
                .into(coverArt);

        StartRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.equals(MainActivity.userMail, "tester@gmail.com")) {
                    infoText.setText("Recording...");
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            infoText.setText("Error: JSON parsing failed");
                        }
                    }, 5000);
                    return;
                }
                if (ContextCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted. Ask for it.
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            REQUEST_RECORD_AUDIO_PERMISSION);  // Use a unique request code
                } else {
                    prepareAudioRecorder();
                    mediaRecorder.start();
                    StartRecording.setEnabled(false);
                    Play.setEnabled(false);
                    infoText.setText("Recording...");
                    //  Start recording logic
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mediaRecorder.stop();
                            mediaRecorder.release(); // Release resources
                            mediaRecorder = null; // Set to null for future recordings
                            StartRecording.setEnabled(true);

                            //  Start AsyncTask for network call
                            new SendAudioTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, AudioSavePath);

                        }
                    }, 5000);
                }
            }
        });

        Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Notify MainActivity
                notifySongRecognized(title);
                Toast.makeText(getContext(), "Check yo music tabs ASAP!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void executeAsyncTask(String audioSavePath) {
        new SendAudioTask().execute(audioSavePath);
    }

    private void prepareAudioRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // AAC
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        // Set output file path (consider using Context.getExternalFilesDir() for app-specific storage)
        String storageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_RECORDINGS).getAbsolutePath();
        }
        AudioSavePath = storageDir + "/recording.aac";
        mediaRecorder.setOutputFile(AudioSavePath);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            // Handle IO exceptions
            infoText.setText("Error: Recording failed");
        }
    }
    public interface OnSongRecognizedListener {
        void onSongRecognized(String songTitle);
    }

    private OnSongRecognizedListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSongRecognizedListener) {
            mListener = (OnSongRecognizedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnSongRecognizedListener");
        }
    }

    private void notifySongRecognized(String songTitle) {
        if (mListener != null) {
            mListener.onSongRecognized(songTitle);
        }
    }
    private class SendAudioTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            infoText.setText("Sending...");
        }

        @Nullable
        @Override
        protected String doInBackground(String... params) {

            // Your existing network call logic using OkHttpClient
            try {
                final MediaType MEDIA_TYPE_MP3 = MediaType.get("audio/mpeg; charset=utf-8"); // Assuming AAC, adjust if needed
                File file = new File(AudioSavePath); // Replace with your AAC file path
                OkHttpClient client = new OkHttpClient();
                RequestBody data = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("api_token", "cba6849723d635ce459ac9cd44ad8447")
                        .addFormDataPart("file", file.getName(), RequestBody.create(file, MEDIA_TYPE_MP3))
                        .addFormDataPart("return", "spotify").build();
                Request request = new Request.Builder().url("https://api.audd.io/")
                        .post(data).build();

                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code: " + response);
                }
                String result = response.body().string(); // Read the body only once
                String finalResult = extractInfoFromResponse(result);

                return finalResult;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private String extractInfoFromResponse(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);

                // Check if status is "success" (assuming success indicates data)
                if (jsonObject.getString("status").equals("success")) {

                    JSONObject resultObject = jsonObject.getJSONObject("result");

                    // Extract desired information
                    String artist = resultObject.getString("artist");
                    title = resultObject.getString("title");
                    String album = resultObject.getString("album");
                    coverImageUrl = resultObject.getJSONObject("spotify")
                            .getJSONObject("album")
                            .getJSONArray("images")
                            .getJSONObject(0)
                            .getString("url");
                    // ... (extract other desired information)

                    // Build the text to display
                    String infoTextContent = "Title: " + title + "\n" +
                            "Artist: " + artist + "\n" +
                            "Album: " + album + "\n";
                    // ... (add other extracted information)

                    playBtnEnabled = true;

                    return infoTextContent;
                } else {
                    playBtnEnabled = false;
                    return "Error: Unexpected response";  // Handle non-success responses
                }
            } catch (JSONException e) {
                e.printStackTrace();
                playBtnEnabled = false;
                return "Error: JSON parsing failed";  // Handle JSON parsing exceptions
            }
        }
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                infoText.setText("Response: \n" + result);
                // Load the image using Glide
                Glide.with(SoundRecognitionFragment.this)
                        .load(coverImageUrl)
                        .into(coverArt);
            } else {
                infoText.setText("Error: Something went wrong");
            }
            Play.setEnabled(playBtnEnabled);
        }
    }
}