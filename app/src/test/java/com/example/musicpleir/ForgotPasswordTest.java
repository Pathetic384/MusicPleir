package com.example.musicpleir;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.view.Gravity;

import org.junit.Test;

public class ForgotPasswordTest {
    @Test
    public void testAuthen() {
        ForgotPassword forgotPassword = mock(ForgotPassword.class);
        forgotPassword.authen();
        verify(forgotPassword, times(1)).authen();
    }
}
