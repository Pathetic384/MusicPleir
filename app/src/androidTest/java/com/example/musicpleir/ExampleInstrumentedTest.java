package com.example.musicpleir;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @BeforeClass
    public static void dismissANRSystemDialog() throws UiObjectNotFoundException {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        // If the device is running in English Locale
        UiObject waitButton = device.findObject(new UiSelector().textContains("wait"));
        if (waitButton.exists()) {
            waitButton.click();
        }
        // If the device is running in Japanese Locale
        waitButton = device.findObject(new UiSelector().textContains("待機"));
        if (waitButton.exists()) {
            waitButton.click();
        }
    }
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.musicpleir", appContext.getPackageName());
    }
}