package com.example.musicpleir;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.junit.Test;

import java.util.zip.Inflater;

public class UserFragmentTest {

    @Test public void testOnCreateView() {
        UserFragment userFragment = mock(UserFragment.class);
        ViewGroup viewGroup = mock(ViewGroup.class);
        LayoutInflater layoutInflater = mock(LayoutInflater.class);
        Bundle bundle = mock(Bundle.class);;;
        assertTrue(userFragment.onCreateView(layoutInflater, viewGroup, bundle) instanceof View);

    }
    @Test
    public void testUpdateNameValue() {
        UserFragment userFragment = mock(UserFragment.class);
        userFragment.updateNameValue();
        verify(userFragment, times(1)).updateNameValue();
    }

    @Test
    public void testUpdatePicValue() {
        UserFragment userFragment = mock(UserFragment.class);
        userFragment.updatePicValue();
        verify(userFragment, times(1)).updatePicValue();
    }

}
