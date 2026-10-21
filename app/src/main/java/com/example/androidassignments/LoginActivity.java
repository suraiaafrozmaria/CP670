package com.example.androidassignments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    protected static final String ACTIVITY_NAME = "LoginActivity";
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String EMAIL_KEY = "DefaultEmail";

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private SharedPreferences prefs;
    private TextView logView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLog.i(ACTIVITY_NAME, "LoginActivity: inside onCreate");
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.loginName);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        emailEditText.setText(prefs.getString(EMAIL_KEY, "email@domain.com"));

        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getString(R.string.error_email_invalid));
            emailEditText.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordEditText.setError(getString(R.string.error_password_empty));
            passwordEditText.requestFocus();
            return;
        }

        prefs.edit().putString(EMAIL_KEY, email).apply();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppLog.i(ACTIVITY_NAME, "LoginActivity: inside onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLog.i(ACTIVITY_NAME, "LoginActivity: inside onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppLog.i(ACTIVITY_NAME, "LoginActivity: inside onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppLog.i(ACTIVITY_NAME, "LoginActivity: inside onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppLog.i(ACTIVITY_NAME, "LoginActivity: inside onDestroy");
    }
}