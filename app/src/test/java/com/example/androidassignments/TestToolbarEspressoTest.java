package com.example.androidassignments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {34})
public class TestToolbarEspressoTest {

    @Rule
    public ActivityScenarioRule<TestToolbar> rule = new ActivityScenarioRule<>(TestToolbar.class);

    @Test
    public void floatingButtonShowsSnackbar() {
        onView(withId(R.id.fab)).perform(click());
        onView(withText(R.string.fab_message)).check(matches(isDisplayed()));
    }

    @Test
    public void toolbarItemOneShowsSnackbar() {
        onView(withId(R.id.action_one)).perform(click());
        onView(withText(R.string.you_selected_item_1)).check(matches(isDisplayed()));
    }
}
