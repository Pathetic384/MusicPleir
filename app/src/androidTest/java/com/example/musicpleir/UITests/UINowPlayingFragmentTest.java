package com.example.musicpleir.UITests;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.musicpleir.Util.waitId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

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
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class UINowPlayingFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);
    
    @Test
    public void NowPlayingTitleBTNTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recyclerView),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));
        onView(isRoot()).perform(waitId(R.id.back_btn, 30000));
        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.back_btn),
                        childAtPosition(
                                allOf(withId(R.id.layout_top_btn),
                                        childAtPosition(
                                                withId(R.id.mContainer),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageView.perform(click());
        ViewInteraction textView = onView(
                allOf(withId(R.id.song_name_mini), withText("After LIKE"),
                        withParent(allOf(withId(R.id.card_bottom_player),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)))),
                        isDisplayed()));
        textView.check(matches(withText("After LIKE")));
    }

    @Test
    public void NowPlayingArtistTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recyclerView),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));
        onView(isRoot()).perform(waitId(R.id.back_btn, 30000));
        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.back_btn),
                        childAtPosition(
                                allOf(withId(R.id.layout_top_btn),
                                        childAtPosition(
                                                withId(R.id.mContainer),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageView.perform(click());
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.song_artist_mini), withText("IVE"),
                        withParent(allOf(withId(R.id.card_bottom_player),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)))),
                        isDisplayed()));
        textView2.check(matches(withText("IVE")));
    }

    @Test
    public void NowPlayingPlayBTNTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recyclerView),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));
        onView(isRoot()).perform(waitId(R.id.back_btn, 30000));
        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.back_btn),
                        childAtPosition(
                                allOf(withId(R.id.layout_top_btn),
                                        childAtPosition(
                                                withId(R.id.mContainer),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageView.perform(click());
        ViewInteraction imageButton = onView(
                allOf(withId(R.id.play_pause_mini),
                        withParent(allOf(withId(R.id.card_bottom_player),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)))),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));
    }

    @Test
    public void NowPlayingPrevBTNTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recyclerView),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));
        onView(isRoot()).perform(waitId(R.id.back_btn, 30000));
        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.back_btn),
                        childAtPosition(
                                allOf(withId(R.id.layout_top_btn),
                                        childAtPosition(
                                                withId(R.id.mContainer),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageView.perform(click());
        ViewInteraction imageView = onView(
                allOf(withId(R.id.skip_prev_bottom),
                        withParent(allOf(withId(R.id.card_bottom_player),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)))),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));
    }

    @Test
    public void NowPlayingNextBTNTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recyclerView),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));
        onView(isRoot()).perform(waitId(R.id.back_btn, 30000));
        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.back_btn),
                        childAtPosition(
                                allOf(withId(R.id.layout_top_btn),
                                        childAtPosition(
                                                withId(R.id.mContainer),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageView.perform(click());
        ViewInteraction imageView2 = onView(
                allOf(withId(R.id.skip_next_bottom),
                        withParent(allOf(withId(R.id.card_bottom_player),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)))),
                        isDisplayed()));
        imageView2.check(matches(isDisplayed()));
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
