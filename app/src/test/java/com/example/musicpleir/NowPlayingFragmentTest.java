package com.example.musicpleir;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

public class NowPlayingFragmentTest {
    @Test
    public void testBtnClicked() {
        NowPlayingFragment nowPlayingFragment = mock(NowPlayingFragment.class);
        nowPlayingFragment.btnClicked(1);
        verify(nowPlayingFragment, times(1)).btnClicked(1);
    }
}
