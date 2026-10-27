package com.example.androidassignments;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.robolectric.Shadows.shadowOf;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;

import androidx.test.core.app.ActivityScenario;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowToast;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {34})
public class MainActivityTest {

    @Test
    public void startChatButtonLaunchesChatWindow() {
        try (ActivityScenario<MainActivity> s = ActivityScenario.launch(MainActivity.class)) {
            s.onActivity(a -> {
                ((Button) a.findViewById(R.id.startChatButton)).performClick();
                Intent next = shadowOf(a).getNextStartedActivity();
                assertNotNull(next);
                assertEquals(ChatWindow.class.getName(), next.getComponent().getClassName());
            });
        }
    }

    @Test
    public void buttonStartsListItemsActivityWithRequestCode10() {
        try (ActivityScenario<MainActivity> s = ActivityScenario.launch(MainActivity.class)) {
            s.onActivity(a -> {
                ((Button) a.findViewById(R.id.button)).performClick();
                ShadowActivity.IntentForResult r = shadowOf(a).getNextStartedActivityForResult();
                assertNotNull(r);
                assertEquals(10, r.requestCode);
                assertEquals(ListItemsActivity.class.getName(), r.intent.getComponent().getClassName());
            });
        }
    }

    @Test
    public void okResultShowsPassedMessageToast() {
        try (ActivityScenario<MainActivity> s = ActivityScenario.launch(MainActivity.class)) {
            s.onActivity(a -> {
                ((Button) a.findViewById(R.id.button)).performClick();
                ShadowActivity.IntentForResult r = shadowOf(a).getNextStartedActivityForResult();
                Intent data = new Intent().putExtra("Response", "My information to share");
                shadowOf(a).receiveResult(r.intent, Activity.RESULT_OK, data);
                assertEquals("ListItemsActivity passed: My information to share",
                        ShadowToast.getTextOfLatestToast());
            });
        }
    }

    @Test
    public void canceledResultShowsNoToast() {
        try (ActivityScenario<MainActivity> s = ActivityScenario.launch(MainActivity.class)) {
            s.onActivity(a -> {
                ((Button) a.findViewById(R.id.button)).performClick();
                ShadowActivity.IntentForResult r = shadowOf(a).getNextStartedActivityForResult();
                shadowOf(a).receiveResult(r.intent, Activity.RESULT_CANCELED, null);
                assertNull(ShadowToast.getLatestToast());
            });
        }
    }

    @Test
    public void okResultWithoutResponseExtraShowsNoToast() {
        try (ActivityScenario<MainActivity> s = ActivityScenario.launch(MainActivity.class)) {
            s.onActivity(a -> {
                ((Button) a.findViewById(R.id.button)).performClick();
                ShadowActivity.IntentForResult r = shadowOf(a).getNextStartedActivityForResult();
                shadowOf(a).receiveResult(r.intent, Activity.RESULT_OK, new Intent());
                assertNull(ShadowToast.getLatestToast());
            });
        }
    }
}