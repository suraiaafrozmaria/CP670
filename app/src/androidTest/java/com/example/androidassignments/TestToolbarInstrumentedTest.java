package com.example.androidassignments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestToolbarInstrumentedTest {

    @Rule
    public ActivityScenarioRule<TestToolbar> rule = new ActivityScenarioRule<>(TestToolbar.class);

    private void assertDestroyedSoon() throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            if (rule.getScenario().getState() == Lifecycle.State.DESTROYED) {
                return;
            }
            Thread.sleep(100);
        }
        assertEquals(Lifecycle.State.DESTROYED, rule.getScenario().getState());
    }

    @Test
    public void toolbarShowsThreeItemsAndAboutInTheOverflowMenu() {
        onView(withId(R.id.action_one)).check(matches(isDisplayed()));
        onView(withId(R.id.action_two)).check(matches(isDisplayed()));
        onView(withId(R.id.action_three)).check(matches(isDisplayed()));
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText(R.string.about)).check(matches(isDisplayed()));
    }

    @Test
    public void itemOneShowsSnackbar() {
        onView(withId(R.id.action_one)).perform(click());
        onView(withText(R.string.you_selected_item_1)).check(matches(isDisplayed()));
    }

    @Test
    public void floatingButtonShowsSnackbar() {
        onView(withId(R.id.fab)).perform(click());
        onView(withText(R.string.fab_message)).check(matches(isDisplayed()));
    }

    @Test
    public void itemTwoNoKeepsTheActivityOpen() {
        onView(withId(R.id.action_two)).perform(click());
        onView(withText(R.string.go_back_title)).check(matches(isDisplayed()));
        onView(withText(R.string.cancel)).perform(click());
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
        assertEquals(Lifecycle.State.RESUMED, rule.getScenario().getState());
    }

    @Test
    public void itemTwoYesEndsTheActivity() throws InterruptedException {
        onView(withId(R.id.action_two)).perform(click());
        onView(withText(R.string.ok)).perform(click());
        assertDestroyedSoon();
    }

    @Test
    public void itemThreeSetsANewSnackbarMessage() {
        onView(withId(R.id.action_three)).perform(click());
        onView(withId(R.id.new_message)).perform(replaceText("My new message"), closeSoftKeyboard());
        onView(withText(R.string.ok)).perform(click());
        onView(withId(R.id.action_one)).perform(click());
        onView(withText("My new message")).check(matches(isDisplayed()));
    }
}
