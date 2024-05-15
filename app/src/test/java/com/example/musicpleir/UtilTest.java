package com.example.musicpleir;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.media.MediaMetadataRetriever;

import org.junit.Test;

public class UtilTest {

    @Test
    public void testFormattedTimeFunction() throws Exception {
        String timeConvert = Util.formattedTime(12345);
        assertThat(timeConvert, is("205:45"));
    }

    @Test
    public void testGetAlbumArtFunction() throws Exception {
        MediaMetadataRetriever retrieverMock = mock(MediaMetadataRetriever.class);

        when(retrieverMock.getEmbeddedPicture()).thenReturn(new byte[]{});

        byte[] image = Util.getAlbumArt("/firebasestorage.googleapis.com/v0/b" +
                "/serverside-7a675.appspot.com/o" +
                "/songs%2F1714027049608.flac?alt=media&token=76769a1c-ac9c-428d-831a-591d7555be2d", retrieverMock);

        assertTrue(String.valueOf(image).contains("[B@"));
    }
}
