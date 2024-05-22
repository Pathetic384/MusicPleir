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
import com.example.musicpleir.Register;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class UIRegisterTest {

    @Rule
    public ActivityScenarioRule<Register> mActivityScenarioRule =
            new ActivityScenarioRule<>(Register.class);

    @Test
    public void uICreateRegisterTest() {
        ViewInteraction textView = onView(
                allOf(withId(R.id.note), withText("Create your account"),
                        withParent(allOf(withId(R.id.main),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView.check(matches(withText("Create your account")));
    }
        @Test
        public void uIButtoonRegisterTest() {
            ViewInteraction button = onView(
                    allOf(withId(R.id.register_btn), withText("REGISTER"),
                            withParent(allOf(withId(R.id.main),
                                    withParent(withId(android.R.id.content)))),
                            isDisplayed()));
            button.check(matches(isDisplayed()));
        }

    @Test
    public void uILoginNowRegisterTest() {
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.loginNow), withText("CLICK TO LOGIN"),
                        withParent(allOf(withId(R.id.main),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView2.check(matches(withText("CLICK TO LOGIN")));
    }
}
