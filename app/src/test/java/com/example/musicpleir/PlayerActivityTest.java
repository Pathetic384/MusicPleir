package com.example.musicpleir;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.view.Gravity;

import org.junit.Test;

import java.io.IOException;

public class PlayerActivityTest {
    @Test
    public void testOpenLyricsDialog() {
        PlayerActivity playerActivity = mock(PlayerActivity.class);
        try {
            playerActivity.openLyricsDialog(Gravity.CENTER);
            verify(playerActivity, times(1)).openLyricsDialog(Gravity.CENTER);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testOpenFeedbackDialog() {
        PlayerActivity playerActivity = mock(PlayerActivity.class);
        playerActivity.openFeedbackDialog(Gravity.CENTER);
        verify(playerActivity, times(1)).openFeedbackDialog(Gravity.CENTER);
    }

    @Test
    public void testPrevBtnClicked() {
        PlayerActivity playerActivity = mock(PlayerActivity.class);
        try {
            playerActivity.prevBtnClicked();
            verify(playerActivity, times(1)).prevBtnClicked();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testNextBtnClicked() {
        PlayerActivity playerActivity = mock(PlayerActivity.class);
        try {
            playerActivity.nextBtnClicked();
            verify(playerActivity, times(1)).nextBtnClicked();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPlayPauseBtnClicked() {
        PlayerActivity playerActivity = mock(PlayerActivity.class);
        try {
            playerActivity.playPauseBtnClicked();
            verify(playerActivity, times(1)).playPauseBtnClicked();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
