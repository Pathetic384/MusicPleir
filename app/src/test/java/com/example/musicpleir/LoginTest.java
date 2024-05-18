package com.example.musicpleir;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.os.Bundle;

import org.junit.Test;
import org.mockito.Mock;

public class LoginTest {
    @Test
    public void onStart() {
        Login login = mock(Login.class);
        doNothing().when(login).onStart();
        login.onStart();
        verify(login, times(1)).onStart();
    }


    @Test
    public void testOnCreate() {
        Login login = mock(Login.class);
        Bundle bundle = mock(Bundle.class);
        login.onCreate(bundle);
        verify(login, times(1)).onCreate(bundle);
    }
}