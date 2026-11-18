package com.example.androidassignments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AppFlowInstrumentedTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> rule = new ActivityScenarioRule<>(LoginActivity.class);

    private void login(String email, String password) {
        onView(withId(R.id.loginName)).perform(replaceText(email), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(replaceText(password), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());
    }

    @Test
    public void badEmailShowsAnError() {
        login("abc", "secret");
        onView(withId(R.id.loginName)).check(matches(hasErrorText("Please enter a valid email address")));
    }

    @Test
    public void emptyPasswordShowsAnError() {
        login("maria@example.com", "");
        onView(withId(R.id.password)).check(matches(hasErrorText("Password cannot be empty")));
    }

    @Test
    public void validLoginOpensTheMainScreen() {
        login("maria@example.com", "secret");
        onView(withId(R.id.startChatButton)).check(matches(isDisplayed()));
        onView(withId(R.id.testToolbarButton)).check(matches(isDisplayed()));
    }

    @Test
    public void chatShowsTheSentMessageAndClearsTheInput() {
        login("maria@example.com", "secret");
        onView(withId(R.id.startChatButton)).perform(click());
        onView(withId(R.id.chatEditText)).perform(replaceText("Hello"), closeSoftKeyboard());
        onView(withId(R.id.sendButton)).perform(click());
        onView(withText("Hello")).check(matches(isDisplayed()));
        onView(withId(R.id.chatEditText)).check(matches(withText("")));
    }

    @Test
    public void testToolbarButtonOpensTheToolbarScreen() {
        login("maria@example.com", "secret");
        onView(withId(R.id.testToolbarButton)).perform(click());
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
    }
}
