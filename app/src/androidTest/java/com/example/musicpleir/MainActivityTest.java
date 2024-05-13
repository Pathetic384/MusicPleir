package com.example.musicpleir;


import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertTrue;

import android.app.Instrumentation;
import android.os.Build;
import android.view.View;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

//    @Before
//    public void set() {
//        MainActivity.testing = true;
//    }
//
//
    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);
//
//    @Rule
//    public GrantPermissionRule mGrantPermissionRule =
//            GrantPermissionRule.grant(
//                    "android.permission.READ_MEDIA_AUDIO",
//                    "android.permission.READ_EXTERNAL_STORAGE");
//
//    @Before
//    public void grant() {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getInstrumentation().getUiAutomation().executeShellCommand("pm grant "
//                    + getTargetContext().getPackageName() + "android.permission.READ_MEDIA_AUDIO");
//            getInstrumentation().getUiAutomation().executeShellCommand("pm grant "
//                    + getTargetContext().getPackageName() + "android.permission.READ_EXTERNAL_STORAGE");
//        }
//    }

    @Test
    public void mainActivityTest() throws Exception{
        ViewInteraction textView = onView(
                allOf(withText("SONGS"),
                        withParent(allOf(withContentDescription("Songs"),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        textView.check(matches(withText("SONGS")));

        ViewInteraction textView2 = onView(
                allOf(withText("ALBUMS"),
                        withParent(allOf(withContentDescription("Albums"),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        textView2.check(matches(withText("ALBUMS")));
       // assertTrue(true);
    }
}
