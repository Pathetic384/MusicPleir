package com.example.musicpleir;


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
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PlayerTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void playerTest() {
        onView(isRoot()).perform(waitFor(5000));
        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recyclerView),
                        childAtPosition(
                                withClassName(is("android.widget.RelativeLayout")),
                                0),isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(0, click()));
        onView(isRoot()).perform(waitFor(3000));
        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.id_next),
                        childAtPosition(
                                allOf(withId(R.id.relative_layout_for_bottom),
                                        childAtPosition(
                                                withId(R.id.mContainer),
                                                4)),
                                3),
                        isDisplayed()));
        appCompatImageView.perform(click());
        onView(isRoot()).perform(waitFor(3000));
        ViewInteraction textView = onView(
                allOf(withId(R.id.song_name), withText("Ed Sheeran - Shape of You (Official Music Video)"),
                        withParent(allOf(withId(R.id.mContainer),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView.check(matches(withText("Ed Sheeran - Shape of You (Official Music Video)")));

        ViewInteraction appCompatImageView2 = onView(
                allOf(withId(R.id.id_prev),
                        childAtPosition(
                                allOf(withId(R.id.relative_layout_for_bottom),
                                        childAtPosition(
                                                withId(R.id.mContainer),
                                                4)),
                                1),
                        isDisplayed()));
        appCompatImageView2.perform(click());
        onView(isRoot()).perform(waitFor(3000));
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.song_name), withText("Never Gonna Give You Up"),
                        withParent(allOf(withId(R.id.mContainer),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView2.check(matches(withText("Never Gonna Give You Up")));

        ViewInteraction appCompatImageView3 = onView(
                allOf(withId(R.id.id_repeat),
                        childAtPosition(
                                allOf(withId(R.id.relative_layout_for_bottom),
                                        childAtPosition(
                                                withId(R.id.mContainer),
                                                4)),
                                4),
                        isDisplayed()));
        appCompatImageView3.perform(click());
        onView(isRoot()).perform(waitFor(3000));
        ViewInteraction appCompatImageView4 = onView(
                allOf(withId(R.id.id_next),
                        childAtPosition(
                                allOf(withId(R.id.relative_layout_for_bottom),
                                        childAtPosition(
                                                withId(R.id.mContainer),
                                                4)),
                                3),
                        isDisplayed()));
        appCompatImageView4.perform(click());
        onView(isRoot()).perform(waitFor(3000));
        ViewInteraction textView3 = onView(
                allOf(withId(R.id.song_name), withText("Never Gonna Give You Up"),
                        withParent(allOf(withId(R.id.mContainer),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView3.check(matches(withText("Never Gonna Give You Up")));
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

    public static ViewAction waitFor(long delay) {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override public String getDescription() {
                return "wait for " + delay + "milliseconds";
            }

            @Override public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(delay);
            }
        };
    }
}
