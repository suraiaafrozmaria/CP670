package com.example.androidassignments;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {34})
public class ChatWindowTest {

    private void send(ChatWindow a, String text) {
        ((EditText) a.findViewById(R.id.chatEditText)).setText(text);
        ((Button) a.findViewById(R.id.sendButton)).performClick();
    }

    private ListAdapter adapter(ChatWindow a) {
        return ((ListView) a.findViewById(R.id.chatListView)).getAdapter();
    }

    @Test
    public void sendingAddsMessageAndClearsInput() {
        try (ActivityScenario<ChatWindow> s = ActivityScenario.launch(ChatWindow.class)) {
            s.onActivity(a -> {
                send(a, "Hello");
                assertEquals(1, adapter(a).getCount());
                assertEquals("Hello", adapter(a).getItem(0));
                EditText input = a.findViewById(R.id.chatEditText);
                assertEquals("", input.getText().toString());
            });
        }
    }

    @Test
    public void blankMessageIsIgnored() {
        try (ActivityScenario<ChatWindow> s = ActivityScenario.launch(ChatWindow.class)) {
            s.onActivity(a -> {
                send(a, "   ");
                assertEquals(0, adapter(a).getCount());
            });
        }
    }

    @Test
    public void severalMessagesAreAllListed() {
        try (ActivityScenario<ChatWindow> s = ActivityScenario.launch(ChatWindow.class)) {
            s.onActivity(a -> {
                send(a, "one");
                send(a, "two");
                send(a, "three");
                assertEquals(3, adapter(a).getCount());
                assertEquals("three", adapter(a).getItem(2));
            });
        }
    }

    @Test
    public void rowsAlternateBetweenIncomingAndOutgoingLayouts() {
        try (ActivityScenario<ChatWindow> s = ActivityScenario.launch(ChatWindow.class)) {
            s.onActivity(a -> {
                send(a, "one");
                send(a, "two");
                ListView list = a.findViewById(R.id.chatListView);
                View row0 = adapter(a).getView(0, null, list);
                View row1 = adapter(a).getView(1, null, list);
                assertEquals("one", ((TextView) row0.findViewById(R.id.message_text)).getText().toString());
                assertEquals("two", ((TextView) row1.findViewById(R.id.message_text)).getText().toString());
                assertEquals("Incoming message",
                        ((ImageView) row0.findViewById(R.id.chat_icon)).getContentDescription().toString());
                assertEquals("Outgoing message",
                        ((ImageView) row1.findViewById(R.id.chat_icon)).getContentDescription().toString());
            });
        }
    }

    @Test
    public void toolbarShowsUpArrow() {
        try (ActivityScenario<ChatWindow> s = ActivityScenario.launch(ChatWindow.class)) {
            s.onActivity(a -> {
                assertNotNull(a.getSupportActionBar());
                assertEquals("Chat", a.getSupportActionBar().getTitle().toString());
            });
        }
    }

    @Test
    public void sentMessageIsSavedAndReloadedNextTime() {
        try (ActivityScenario<ChatWindow> s = ActivityScenario.launch(ChatWindow.class)) {
            s.onActivity(a -> {
                ((EditText) a.findViewById(R.id.chatEditText)).setText("Saved hello");
                ((Button) a.findViewById(R.id.sendButton)).performClick();
            });
        }
        try (ActivityScenario<ChatWindow> s = ActivityScenario.launch(ChatWindow.class)) {
            s.onActivity(a -> {
                ListView list = a.findViewById(R.id.chatListView);
                assertEquals(1, list.getAdapter().getCount());
                assertEquals("Saved hello", list.getAdapter().getItem(0));
            });
        }
    }

    @Test
    public void existingDatabaseMessagesAreLoadedOnStart() {
        ChatDatabaseHelper h = new ChatDatabaseHelper(ApplicationProvider.getApplicationContext());
        SQLiteDatabase d = h.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(ChatDatabaseHelper.KEY_MESSAGE, "Old one");
        d.insert(ChatDatabaseHelper.TABLE_NAME, null, v);
        d.close();
        h.close();

        try (ActivityScenario<ChatWindow> s = ActivityScenario.launch(ChatWindow.class)) {
            s.onActivity(a -> {
                ListView list = a.findViewById(R.id.chatListView);
                assertEquals(1, list.getAdapter().getCount());
                assertEquals("Old one", list.getAdapter().getItem(0));
            });
        }
    }
}
