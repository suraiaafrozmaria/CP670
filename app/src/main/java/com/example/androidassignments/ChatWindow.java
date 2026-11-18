package com.example.androidassignments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {

    protected static final String ACTIVITY_NAME = "ChatWindow";

    private ListView listView;
    private EditText textInput;
    private Button sendButton;
    private final ArrayList<String> chatMessages = new ArrayList<>();
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_window);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, ime.bottom));
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.chat_title);
        }

        listView = findViewById(R.id.chatListView);
        textInput = findViewById(R.id.chatEditText);
        sendButton = findViewById(R.id.sendButton);

        // The adapter is created and attached BEFORE the click handler can use it,
        // so there is no null pointer exception.
        ChatAdapter messageAdapter = new ChatAdapter(this);
        listView.setAdapter(messageAdapter);
//
        ChatDatabaseHelper dbHelper = new ChatDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(ChatDatabaseHelper.TABLE_NAME,
                new String[]{ChatDatabaseHelper.KEY_ID, ChatDatabaseHelper.KEY_MESSAGE},
                null, null, null, null, ChatDatabaseHelper.KEY_ID + " ASC");
        int messageIndex = cursor.getColumnIndexOrThrow(ChatDatabaseHelper.KEY_MESSAGE);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String saved = cursor.getString(messageIndex);
            Log.i(ACTIVITY_NAME, "SQL MESSAGE:" + saved);
            chatMessages.add(saved);
            cursor.moveToNext();
        }
        Log.i(ACTIVITY_NAME, "Cursor's  column count =" + cursor.getColumnCount());
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            Log.i(ACTIVITY_NAME, "Column " + i + " name = " + cursor.getColumnName(i));
        }
        cursor.close();
        messageAdapter.notifyDataSetChanged();
//
        sendButton.setOnClickListener(v -> {
            String message = textInput.getText().toString().trim();
            if (message.isEmpty()) {
                return;
            }
            chatMessages.add(message);

            ContentValues values = new ContentValues();
            values.put(ChatDatabaseHelper.KEY_MESSAGE, message);
            db.insert(ChatDatabaseHelper.TABLE_NAME, null, values);

            messageAdapter.notifyDataSetChanged();
            listView.setSelection(chatMessages.size() - 1);
            textInput.setText("");
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    private class ChatAdapter extends ArrayAdapter<String> {

        public ChatAdapter(Context ctx) {
            super(ctx, 0);
        }

        @Override
        public int getCount() {
            return chatMessages.size();
        }

        @Nullable
        @Override
        public String getItem(int position) {
            return chatMessages.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result;
            if (position % 2 == 0) {
                result = inflater.inflate(R.layout.chat_row_incoming, parent, false);
            } else {
                result = inflater.inflate(R.layout.chat_row_outgoing, parent, false);
            }
            TextView message = result.findViewById(R.id.message_text);
            message.setText(getItem(position));
            return result;
        }
    }
}