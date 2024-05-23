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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class UIPlayerTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void PlayerNameTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recyclerView),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        onView(isRoot()).perform(waitId(R.id.song_name, 30000));

        ViewInteraction textView = onView(
                allOf(withId(R.id.song_name), withText("After LIKE"),
                        withParent(allOf(withId(R.id.mContainer),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView.check(matches(withText("After LIKE")));
    }

    @Test
    public void PlayerArtistTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recyclerView),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        onView(isRoot()).perform(waitId(R.id.song_name, 30000));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.song_artist), withText("IVE"),
                        withParent(allOf(withId(R.id.mContainer),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView2.check(matches(withText("IVE")));
    }

    @Test
    public void PlayerButtonTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recyclerView),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        onView(isRoot()).perform(waitId(R.id.song_name, 30000));
        ViewInteraction button = onView(
                allOf(withId(R.id.lyrics), withText("Lyrics"),
                        withParent(allOf(withId(R.id.mContainer),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));
    }
    @Test
    public void PlayerBottomTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recyclerView),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        onView(isRoot()).perform(waitId(R.id.song_name, 30000));
        ViewInteraction imageButton = onView(
                allOf(withId(R.id.play_pause),
                        withParent(allOf(withId(R.id.relative_layout_for_bottom),
                                withParent(withId(R.id.mContainer)))),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));
    }
    @Test
    public void PlayerSeekBarTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recyclerView),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        onView(isRoot()).perform(waitId(R.id.song_name, 30000));
        ViewInteraction viewGroup = onView(
                allOf(withId(R.id.relative_layout_for_bottom),
                        withParent(allOf(withId(R.id.mContainer),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        viewGroup.check(matches(isDisplayed()));

        ViewInteraction viewGroup2 = onView(
                allOf(withId(R.id.seek_bar_layout),
                        withParent(allOf(withId(R.id.mContainer),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        viewGroup2.check(matches(isDisplayed()));
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
