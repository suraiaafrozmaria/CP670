package com.example.androidassignments;

import static org.junit.Assert.assertEquals;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {34})
public class ChatDatabaseHelperTest {

    private ChatDatabaseHelper helper;
    private SQLiteDatabase db;

    @Before
    public void setUp() {
        helper = new ChatDatabaseHelper(ApplicationProvider.getApplicationContext());
        db = helper.getWritableDatabase();
    }

    @After
    public void tearDown() {
        db.close();
        helper.close();
    }

    private void insert(String message) {
        ContentValues values = new ContentValues();
        values.put(ChatDatabaseHelper.KEY_MESSAGE, message);
        db.insert(ChatDatabaseHelper.TABLE_NAME, null, values);
    }

    @Test
    public void constantsHaveExpectedValues() {
        assertEquals("Messages.db", ChatDatabaseHelper.DATABASE_NAME);
        assertEquals("ChatMessages", ChatDatabaseHelper.TABLE_NAME);
        assertEquals("_id", ChatDatabaseHelper.KEY_ID);
        assertEquals("MESSAGE", ChatDatabaseHelper.KEY_MESSAGE);
        assertEquals("ChatDatabaseHelper", ChatDatabaseHelper.ACTIVITY_NAME);
    }

    @Test
    public void tableStartsEmpty() {
        assertEquals(0, DatabaseUtils.queryNumEntries(db, ChatDatabaseHelper.TABLE_NAME));
    }

    @Test
    public void insertedMessageCanBeReadBack() {
        insert("hello");
        try (Cursor c = db.query(ChatDatabaseHelper.TABLE_NAME, null, null, null, null, null, null)) {
            assertEquals(2, c.getColumnCount());
            c.moveToFirst();
            assertEquals("hello", c.getString(c.getColumnIndexOrThrow(ChatDatabaseHelper.KEY_MESSAGE)));
        }
    }

    @Test
    public void idsAutoIncrement() {
        insert("one");
        insert("two");
        try (Cursor c = db.query(ChatDatabaseHelper.TABLE_NAME, new String[]{ChatDatabaseHelper.KEY_ID},
                null, null, null, null, ChatDatabaseHelper.KEY_ID + " ASC")) {
            c.moveToFirst();
            long first = c.getLong(0);
            c.moveToNext();
            assertEquals(first + 1, c.getLong(0));
        }
    }

    @Test
    public void upgradeDropsOldMessagesAndRecreatesTable() {
        insert("old message");
        helper.onUpgrade(db, 1, 2);
        assertEquals(0, DatabaseUtils.queryNumEntries(db, ChatDatabaseHelper.TABLE_NAME));
        insert("new message");
        assertEquals(1, DatabaseUtils.queryNumEntries(db, ChatDatabaseHelper.TABLE_NAME));
    }
}
