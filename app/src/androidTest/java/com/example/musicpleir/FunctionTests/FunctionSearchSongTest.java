package com.example.musicpleir.FunctionTests;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.musicpleir.Util.waitFor;
import static com.example.musicpleir.Util.waitId;
import static org.hamcrest.Matchers.allOf;

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
public class FunctionSearchSongTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);
    @Test
    public void searchSong1Test() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(replaceText("badd"));
        onView(isRoot()).perform(waitFor(3000));
        ViewInteraction textView = onView(
                allOf(withId(R.id.music_file_name), withText("Baddie"),
                        withParent(allOf(withId(R.id.audio_item),
                                withParent(withId(R.id.recyclerView)))),
                        isDisplayed()));
        textView.check(matches(withText("Baddie")));
    }

    @Test
    public void searchSong2Test() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(replaceText("heya"));
        onView(isRoot()).perform(waitFor(3000));
        ViewInteraction textView = onView(
                allOf(withId(R.id.music_file_name), withText("Heya"),
                        withParent(allOf(withId(R.id.audio_item),
                                withParent(withId(R.id.recyclerView)))),
                        isDisplayed()));
        textView.check(matches(withText("Heya")));
    }

    @Test
    public void searchSong3Test() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(replaceText("Lonely"));
        onView(isRoot()).perform(waitFor(3000));
        ViewInteraction textView = onView(
                allOf(withId(R.id.music_file_name), withText("Lonely"),
                        withParent(allOf(withId(R.id.audio_item),
                                withParent(withId(R.id.recyclerView)))),
                        isDisplayed()));
        textView.check(matches(withText("Lonely")));
    }
}
