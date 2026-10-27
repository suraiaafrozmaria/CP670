package com.example.androidassignments;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {34})
public class LoginActivityTest {

    private SharedPreferences prefs() {
        Context context = ApplicationProvider.getApplicationContext();
        return context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
    }

    private void fillAndLogin(LoginActivity a, String email, String password) {
        ((EditText) a.findViewById(R.id.loginName)).setText(email);
        ((EditText) a.findViewById(R.id.password)).setText(password);
        ((Button) a.findViewById(R.id.loginButton)).performClick();
    }

    @Test
    public void firstRunShowsDefaultEmail() {
        try (ActivityScenario<LoginActivity> s = ActivityScenario.launch(LoginActivity.class)) {
            s.onActivity(a -> {
                EditText email = a.findViewById(R.id.loginName);
                assertEquals("email@domain.com", email.getText().toString());
            });
        }
    }

    @Test
    public void savedEmailIsShownOnNextStart() {
        prefs().edit().putString("DefaultEmail", "maria@example.com").commit();
        try (ActivityScenario<LoginActivity> s = ActivityScenario.launch(LoginActivity.class)) {
            s.onActivity(a -> {
                EditText email = a.findViewById(R.id.loginName);
                assertEquals("maria@example.com", email.getText().toString());
            });
        }
    }

    @Test
    public void badlyFormattedEmailShowsErrorAndStaysOnScreen() {
        try (ActivityScenario<LoginActivity> s = ActivityScenario.launch(LoginActivity.class)) {
            s.onActivity(a -> {
                fillAndLogin(a, "abc", "secret");
                EditText email = a.findViewById(R.id.loginName);
                assertNotNull(email.getError());
                assertNull(shadowOf(a).getNextStartedActivity());
            });
        }
    }

    @Test
    public void emptyPasswordShowsError() {
        try (ActivityScenario<LoginActivity> s = ActivityScenario.launch(LoginActivity.class)) {
            s.onActivity(a -> {
                fillAndLogin(a, "maria@example.com", "");
                EditText password = a.findViewById(R.id.password);
                assertNotNull(password.getError());
                assertNull(shadowOf(a).getNextStartedActivity());
            });
        }
    }

    @Test
    public void validLoginSavesEmailAndStartsMainActivity() {
        try (ActivityScenario<LoginActivity> s = ActivityScenario.launch(LoginActivity.class)) {
            s.onActivity(a -> {
                fillAndLogin(a, "maria@example.com", "secret");
                Intent next = shadowOf(a).getNextStartedActivity();
                assertNotNull(next);
                assertEquals(MainActivity.class.getName(), next.getComponent().getClassName());
                assertEquals("maria@example.com", prefs().getString("DefaultEmail", ""));
            });
        }
    }

    @Test
    public void logBoxShowsStartAndResumeMessages() {
        try (ActivityScenario<LoginActivity> s = ActivityScenario.launch(LoginActivity.class)) {
            s.onActivity(a -> {
                String log = ((TextView) a.findViewById(R.id.logView)).getText().toString();
                assertTrue("Log box was: [" + log + "]", log.contains("onStart"));
                assertTrue("Log box was: [" + log + "]", log.contains("onResume"));
            });
        }
    }

    @Test
    public void logBoxShowsPauseAndStopMessages() {
        try (ActivityScenario<LoginActivity> s = ActivityScenario.launch(LoginActivity.class)) {
            s.moveToState(Lifecycle.State.CREATED);
            s.onActivity(a -> {
                String log = ((TextView) a.findViewById(R.id.logView)).getText().toString();
                assertTrue("Log box was: [" + log + "]", log.contains("onPause"));
                assertTrue("Log box was: [" + log + "]", log.contains("onStop"));
            });
        }
    }

    @Test
    public void appLogShowsMessagesInTheBoundTextView() {
        TextView box = new TextView(ApplicationProvider.getApplicationContext());
        AppLog.bind(box);
        AppLog.i("TestTag", "hello from the test");
        assertTrue(box.getText().toString().contains("hello from the test"));
        AppLog.unbind(box);
    }

    @Test
    public void emptyEmailShowsError() {
        try (ActivityScenario<LoginActivity> s = ActivityScenario.launch(LoginActivity.class)) {
            s.onActivity(a -> {
                fillAndLogin(a, "", "secret");
                EditText email = a.findViewById(R.id.loginName);
                assertNotNull(email.getError());
            });
        }
    }
}