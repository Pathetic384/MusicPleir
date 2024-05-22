package com.example.musicpleir.UITests;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

import android.view.View;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.musicpleir.MainActivity;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class UIMainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void checkSongsFragmentTest() {
        ViewInteraction textView = onView(
                allOf(withText("SONGS"),
                        withParent(allOf(withContentDescription("Songs"),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        textView.check(matches(withText("SONGS")));
    }

    @Test
    public void checkLocalFragmentTest() {
        ViewInteraction textView2 = onView(
                allOf(withText("LOCAL\nSONGS"),
                        withParent(allOf(withContentDescription("Local\nsongs"),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        textView2.check(matches(withText(containsString("SONGS"))));
    }

    @Test
    public void checkAlbumsFragmentTest() {
        ViewInteraction textView3 = onView(
                allOf(withText("ALBUMS"),
                        withParent(allOf(withContentDescription("Albums"),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        textView3.check(matches(withText("ALBUMS")));
    }
    @Test
    public void checkShazamFragmentTest() {
        ViewInteraction textView4 = onView(
                allOf(withText("SHAZAM"),
                        withParent(allOf(withContentDescription("Shazam"),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        textView4.check(matches(withText("SHAZAM")));
    }
    @Test
    public void checkProfileFragmentTest() {
        ViewInteraction textView5 = onView(
                allOf(withText("PROFILE"),
                        withParent(allOf(withContentDescription("Profile"),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        textView5.check(matches(withText("PROFILE")));
    }
}
