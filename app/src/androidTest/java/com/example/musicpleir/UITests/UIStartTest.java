package com.example.musicpleir.UITests;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.musicpleir.R;
import com.example.musicpleir.StartActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class UIStartTest {

    @Rule
    public ActivityScenarioRule<StartActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(StartActivity.class);

    @Test
    public void TextTest() {
        ViewInteraction textView = onView(
                allOf(withId(R.id.textView2), withText("Want to get recommendations?"),
                        withParent(allOf(withId(R.id.main),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView.check(matches(withText("Want to get recommendations?")));
    }

        @Test
        public void NoBtnTest() {
            ViewInteraction button = onView(
                    allOf(withId(R.id.button2), withText("NO"),
                            withParent(allOf(withId(R.id.main),
                                    withParent(withId(android.R.id.content)))),
                            isDisplayed()));
            button.check(matches(isDisplayed()));
        }

        @Test
        public void YesBtnTest() {
        ViewInteraction button2 = onView(
                allOf(withId(R.id.button), withText("YES"),
                        withParent(allOf(withId(R.id.main),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));
    }
}
