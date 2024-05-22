package com.example.musicpleir;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest3 {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void FunctionChangeProfileTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction tabView = onView(
                allOf(withContentDescription("Profile"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tab_layout),
                                        0),
                                4),
                        isDisplayed()));
        tabView.perform(click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.user_name), withText("kira yoshikage"),
                        withParent(withParent(withId(R.id.viewpager))),
                        isDisplayed()));
        editText.check(matches(withText("kira yoshikage")));

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.user_name), withText("kira yoshikage"),
                        childAtPosition(
                                withParent(withId(R.id.viewpager)),
                                5),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("kira yoshikage"));

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.user_name), withText("kira yoshikage"),
                        childAtPosition(
                                withParent(withId(R.id.viewpager)),
                                5),
                        isDisplayed()));
        appCompatEditText2.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.save_button),
                        childAtPosition(
                                withParent(withId(R.id.viewpager)),
                                2),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.user_name), withText("kira yoshikage"),
                        withParent(withParent(withId(R.id.viewpager))),
                        isDisplayed()));
        editText2.check(matches(withText("kira yoshikage")));
    }

    @Test
    public void FunctionCheckProfileTest() {
        onView(isRoot()).perform(waitId(R.id.music_img, 30000));
        ViewInteraction tabView = onView(
                allOf(withContentDescription("Profile"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.tab_layout),
                                        0),
                                4),
                        isDisplayed()));
        tabView.perform(click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.user_name), withText("kira yoshikage"),
                        withParent(withParent(withId(R.id.viewpager))),
                        isDisplayed()));
        editText.check(matches(withText("kira yoshikage")));
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
