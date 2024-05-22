package com.example.musicpleir.UITests;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.musicpleir.Util.waitId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.musicpleir.MainActivity;
import com.example.musicpleir.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class UISongFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void musicSong1NameTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction textView = onView(
                allOf(withId(R.id.music_file_name), withText("After LIKE"),
                        withParent(allOf(withId(R.id.audio_item),
                                withParent(withId(R.id.recyclerView)))),
                        isDisplayed()));
        textView.check(matches(withText("After LIKE")));
    }

    @Test
    public void musicSong1ArtistTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.music_artist_name), withText("LE SSERAFIM"),
                        withParent(allOf(withId(R.id.audio_item),
                                withParent(withId(R.id.recyclerView)))),
                        isDisplayed()));
        textView2.check(matches(withText("LE SSERAFIM")));
    }

    @Test
    public void musicSong2NameTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction textView3 = onView(
                allOf(withId(R.id.music_file_name), withText("Baddie"),
                        withParent(allOf(withId(R.id.audio_item),
                                withParent(withId(R.id.recyclerView)))),
                        isDisplayed()));
        textView3.check(matches(withText("Baddie")));
    }

    @Test
    public void musicSong2ArtistTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction textView4 = onView(
                allOf(withId(R.id.music_artist_name), withText("Army Of Lovers"),
                        withParent(allOf(withId(R.id.audio_item),
                                withParent(withId(R.id.recyclerView)))),
                        isDisplayed()));
        textView4.check(matches(withText("Army Of Lovers")));
    }
}
