package com.example.musicpleir;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.os.Bundle;

import org.junit.Test;

public class RegisterTest {
    @Test
    public void onStart() {
        Register register = mock(Register.class);
        doNothing().when(register).onStart();
        register.onStart();
        verify(register, times(1)).onStart();
    }


    @Test
    public void testOnCreate() {
        Register register = mock(Register.class);
        Bundle bundle = mock(Bundle.class);
        register.onCreate(bundle);
        verify(register, times(1)).onCreate(bundle);
    }
}
