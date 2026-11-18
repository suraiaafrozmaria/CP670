package com.example.androidassignments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
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
public class ChatWindowEspressoTest {

    @Rule
    public ActivityScenarioRule<ChatWindow> rule = new ActivityScenarioRule<>(ChatWindow.class);

    @Test
    public void typedMessageAppearsAndInputIsCleared() {
        onView(withId(R.id.chatEditText)).perform(typeText("Hello"), closeSoftKeyboard());
        onView(withId(R.id.sendButton)).perform(click());
        onView(withText("Hello")).check(matches(isDisplayed()));
        onView(withId(R.id.chatEditText)).check(matches(withText("")));
    }
}