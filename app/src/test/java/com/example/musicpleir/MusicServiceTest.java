package com.example.musicpleir;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import java.io.IOException;

public class MusicServiceTest {
    @Test
    public void testCreateMediaPlayer() {
        MusicService musicService = mock(MusicService.class);
        musicService.createMediaPlayer(5);
        verify(musicService, times(1)).createMediaPlayer(5);
    }

    @Test
    public void testShowNotification() {
        MusicService musicService = mock(MusicService.class);
        try {
            musicService.showNotification(R.drawable.ic_pause);
            verify(musicService, times(1)).showNotification(R.drawable.ic_pause);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
