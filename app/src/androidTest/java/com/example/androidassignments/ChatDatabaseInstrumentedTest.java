package com.example.androidassignments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ChatDatabaseInstrumentedTest {

    private final Context ctx = ApplicationProvider.getApplicationContext();

    @Before
    public void clean() {
        ctx.deleteDatabase(ChatDatabaseHelper.DATABASE_NAME);
    }

    @After
    public void cleanAfter() {
        ctx.deleteDatabase(ChatDatabaseHelper.DATABASE_NAME);
    }

    @Test
    public void upgradeDropsOldMessages() {
        ChatDatabaseHelper helper = new ChatDatabaseHelper(ctx);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(ChatDatabaseHelper.KEY_MESSAGE, "old");
        db.insert(ChatDatabaseHelper.TABLE_NAME, null, v);
        assertEquals(1, DatabaseUtils.queryNumEntries(db, ChatDatabaseHelper.TABLE_NAME));
        helper.onUpgrade(db, 1, 2);
        assertEquals(0, DatabaseUtils.queryNumEntries(db, ChatDatabaseHelper.TABLE_NAME));
        db.close();
        helper.close();
    }

    @Test
    public void sentMessageIsStillThereAfterRelaunch() {
        try (ActivityScenario<ChatWindow> s = ActivityScenario.launch(ChatWindow.class)) {
            onView(withId(R.id.chatEditText)).perform(replaceText("Persisted hello"), closeSoftKeyboard());
            onView(withId(R.id.sendButton)).perform(click());
            onView(withText("Persisted hello")).check(matches(isDisplayed()));
        }
        try (ActivityScenario<ChatWindow> s = ActivityScenario.launch(ChatWindow.class)) {
            onView(withText("Persisted hello")).check(matches(isDisplayed()));
        }
    }
}
