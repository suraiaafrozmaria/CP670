package com.example.androidassignments;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.test.core.app.ActivityScenario;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenu;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowToast;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {34})
public class TestToolbarTest {

    private static void select(TestToolbar a, int id) {
        a.onOptionsItemSelected(new RoboMenuItem(id));
    }

    private static String snackbarText(TestToolbar a) {
        ShadowLooper.idleMainLooper();
        TextView tv = a.findViewById(com.google.android.material.R.id.snackbar_text);
        return tv == null ? null : tv.getText().toString();
    }

    private static AlertDialog latestDialog() {
        ShadowLooper.idleMainLooper();
        return (AlertDialog) ShadowDialog.getLatestDialog();
    }

    @Test
    public void toolbarMenuHasThreeIconsAndAbout() {
        try (ActivityScenario<TestToolbar> s = ActivityScenario.launch(TestToolbar.class)) {
            s.onActivity(a -> {
                Menu menu = new RoboMenu(a);
                assertTrue(a.onCreateOptionsMenu(menu));
                assertEquals(4, menu.size());
                assertNotNull(menu.findItem(R.id.action_one));
                assertNotNull(menu.findItem(R.id.action_two));
                assertNotNull(menu.findItem(R.id.action_three));
                assertNotNull(menu.findItem(R.id.action_about));
            });
        }
    }

    @Test
    public void aboutShowsVersionToast() {
        try (ActivityScenario<TestToolbar> s = ActivityScenario.launch(TestToolbar.class)) {
            s.onActivity(a -> {
                select(a, R.id.action_about);
                assertEquals("Version 1.0, by Suraia Afroz Maria", ShadowToast.getTextOfLatestToast());
            });
        }
    }

    @Test
    public void unknownMenuItemIsNotHandled() {
        try (ActivityScenario<TestToolbar> s = ActivityScenario.launch(TestToolbar.class)) {
            s.onActivity(a -> assertFalse(a.onOptionsItemSelected(new RoboMenuItem(12345))));
        }
    }

    @Test
    public void itemOneShowsDefaultSnackbar() {
        try (ActivityScenario<TestToolbar> s = ActivityScenario.launch(TestToolbar.class)) {
            s.onActivity(a -> {
                select(a, R.id.action_one);
                assertEquals("You selected item 1", snackbarText(a));
            });
        }
    }

    @Test
    public void floatingButtonShowsOwnSnackbar() {
        try (ActivityScenario<TestToolbar> s = ActivityScenario.launch(TestToolbar.class)) {
            s.onActivity(a -> {
                View fab = a.findViewById(R.id.fab);
                fab.performClick();
                assertEquals("Hello from the floating button!", snackbarText(a));
            });
        }
    }

    @Test
    public void itemTwoNoKeepsTheActivity() {
        try (ActivityScenario<TestToolbar> s = ActivityScenario.launch(TestToolbar.class)) {
            s.onActivity(a -> {
                select(a, R.id.action_two);
                AlertDialog dialog = latestDialog();
                assertNotNull(dialog);
                assertTrue(dialog.isShowing());
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).performClick();
                ShadowLooper.idleMainLooper();
                assertFalse(dialog.isShowing());
                assertFalse(a.isFinishing());
            });
        }
    }

    @Test
    public void itemTwoYesFinishesTheActivity() {
        try (ActivityScenario<TestToolbar> s = ActivityScenario.launch(TestToolbar.class)) {
            s.onActivity(a -> {
                select(a, R.id.action_two);
                AlertDialog dialog = latestDialog();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                ShadowLooper.idleMainLooper();
                assertTrue(a.isFinishing());
            });
        }
    }

    @Test
    public void itemThreeOkSetsTheNewSnackbarMessage() {
        try (ActivityScenario<TestToolbar> s = ActivityScenario.launch(TestToolbar.class)) {
            s.onActivity(a -> {
                select(a, R.id.action_three);
                AlertDialog dialog = latestDialog();
                EditText input = dialog.findViewById(R.id.new_message);
                input.setText("My new message");
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                ShadowLooper.idleMainLooper();

                select(a, R.id.action_one);
                assertEquals("My new message", snackbarText(a));
            });
        }
    }

    @Test
    public void itemThreeCancelOrEmptyKeepsTheOldMessage() {
        try (ActivityScenario<TestToolbar> s = ActivityScenario.launch(TestToolbar.class)) {
            s.onActivity(a -> {
                select(a, R.id.action_three);
                AlertDialog first = latestDialog();
                ((EditText) first.findViewById(R.id.new_message)).setText("ignored");
                first.getButton(AlertDialog.BUTTON_NEGATIVE).performClick();
                ShadowLooper.idleMainLooper();

                select(a, R.id.action_three);
                AlertDialog second = latestDialog();
                ((EditText) second.findViewById(R.id.new_message)).setText("   ");
                second.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                ShadowLooper.idleMainLooper();

                select(a, R.id.action_one);
                assertEquals("You selected item 1", snackbarText(a));
            });
        }
    }
}
