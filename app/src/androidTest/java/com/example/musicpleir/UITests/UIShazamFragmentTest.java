package com.example.musicpleir.UITests;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.musicpleir.Util.waitId;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.musicpleir.MainActivity;
import com.example.musicpleir.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class UIShazamFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void shazamTitleTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction tabView = onView(
                allOf(withContentDescription("Shazam"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tab_layout),
                                        0),
                                3),
                        isDisplayed()));
        tabView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.title), withText("Music Recognition"),
                        withParent(withParent(withId(R.id.viewpager))),
                        isDisplayed()));
        textView.check(matches(withText("Music Recognition")));
    }

    @Test
    public void shazamResultTextTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction tabView = onView(
                allOf(withContentDescription("Shazam"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tab_layout),
                                        0),
                                3),
                        isDisplayed()));
        tabView.perform(click());
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.info_text), withText("Input a sound sequence."),
                        withParent(withParent(withId(R.id.viewpager))),
                        isDisplayed()));
        textView2.check(matches(withText("Input a sound sequence.")));
    }

    @Test
    public void shazamArtButtonTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction tabView = onView(
                allOf(withContentDescription("Shazam"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tab_layout),
                                        0),
                                3),
                        isDisplayed()));
        tabView.perform(click());
        ViewInteraction imageView = onView(
                allOf(withId(R.id.cover_art),
                        withParent(withParent(withId(R.id.viewpager))),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));
    }

    @Test
    public void shazamRecordButtonTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction tabView = onView(
                allOf(withContentDescription("Shazam"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tab_layout),
                                        0),
                                3),
                        isDisplayed()));
        tabView.perform(click());
        ViewInteraction button = onView(
                allOf(withId(R.id.start_recording), withText("Start Recording"),
                        withParent(withParent(withId(R.id.viewpager))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));
    }

    @Test
    public void shazamSearchButtonTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction tabView = onView(
                allOf(withContentDescription("Shazam"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tab_layout),
                                        0),
                                3),
                        isDisplayed()));
        tabView.perform(click());
        ViewInteraction button2 = onView(
                allOf(withId(R.id.play_btn), withText("Search"),
                        withParent(withParent(withId(R.id.viewpager))),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
