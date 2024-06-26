package com.example.musicpleir;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.AudioFeatures;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.data.browse.GetRecommendationsRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import com.wrapper.spotify.requests.data.tracks.GetAudioFeaturesForTrackRequest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Recommender {

    public static ArrayList recommend(String accessToken, String playlistID) {

        ArrayList<String> res = new ArrayList<String>();
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(accessToken)
                .build();


        GetPlaylistRequest getPlaylistRequest = spotifyApi.getPlaylist(playlistID).build();


        try {
            Playlist playlist = getPlaylistRequest.execute();
            Paging<PlaylistTrack> playlistTrackPaging = playlist.getTracks();
            PlaylistTrack[] playlistTracks = playlistTrackPaging.getItems();
            List<TrackSimplified> recommendedTracks = new ArrayList<>();

            for (PlaylistTrack track : playlistTracks) {
                GetAudioFeaturesForTrackRequest getAudioFeaturesForTrackRequest = spotifyApi.getAudioFeaturesForTrack(track.getTrack().getId()).build();
                AudioFeatures audioFeatures = getAudioFeaturesForTrackRequest.execute();
                if (audioFeatures != null) {
                    // Get recommendations based on the track
                    GetRecommendationsRequest getRecommendationsRequest = spotifyApi.getRecommendations().seed_tracks(track.getTrack().getId()).limit(10).build();
                    TrackSimplified[] recommendations = getRecommendationsRequest.execute().getTracks();
                    for (TrackSimplified recommendedTrack : recommendations) {
                        recommendedTracks.add(recommendedTrack);
                    }
                } else {
                    System.out.println("No audio features found for track " + track.getTrack().getId());
                }
            }

            // Print the recommended tracks
            for (TrackSimplified track : recommendedTracks) {
                res.add(track.getName());
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return res;
    }
}