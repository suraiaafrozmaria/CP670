package com.example.androidassignments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    protected static final String ACTIVITY_NAME = "MainActivity";
    private static final int REQUEST_LIST_ITEMS = 10;

    private TextView logView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLog.i(ACTIVITY_NAME, "MainActivity: inside onCreate");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        logView = findViewById(R.id.logView);
        AppLog.bind(logView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListItemsActivity.class);
            startActivityForResult(intent, REQUEST_LIST_ITEMS);
        });

        Button startChatButton = findViewById(R.id.startChatButton);
        startChatButton.setOnClickListener(v -> {
            AppLog.i(ACTIVITY_NAME, "User clicked Start Chat");
            Intent chatIntent = new Intent(MainActivity.this, ChatWindow.class);
            startActivity(chatIntent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LIST_ITEMS) {
            AppLog.i(ACTIVITY_NAME, "Returned to MainActivity.onActivityResult");
            if (resultCode == Activity.RESULT_OK && data != null) {
                String messagePassed = data.getStringExtra("Response");
                if (messagePassed != null) {
                    Toast.makeText(this,
                            getString(R.string.passed_message, messagePassed),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppLog.i(ACTIVITY_NAME, "MainActivity: inside onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLog.bind(logView);
        AppLog.i(ACTIVITY_NAME, "MainActivity: inside onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppLog.i(ACTIVITY_NAME, "MainActivity: inside onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppLog.i(ACTIVITY_NAME, "MainActivity: inside onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppLog.i(ACTIVITY_NAME, "MainActivity: inside onDestroy");
        AppLog.unbind(logView);
    }
}