package com.example.androidassignments;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class TestToolbar extends AppCompatActivity {

    private static final String TAG = "Toolbar";

    private String snackbarMessage;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test_toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // the message shown by menu item 1 (item 3 can change it)
        snackbarMessage = getString(R.string.you_selected_item_1);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> showSnackbar(getString(R.string.fab_message)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.toolbar_menu, m);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        int id = mi.getItemId();
        if (id == R.id.action_one) {
            Log.d(TAG, "Option 1 selected");
            showSnackbar(snackbarMessage);
        } else if (id == R.id.action_two) {
            Log.d(TAG, "Option 2 selected");
            showGoBackDialog();
        } else if (id == R.id.action_three) {
            Log.d(TAG, "Option 3 selected");
            showCustomDialog();
        } else if (id == R.id.action_about) {
            Log.d(TAG, "About selected");
            Toast.makeText(this, R.string.about_message, Toast.LENGTH_LONG).show();
        } else {
            return super.onOptionsItemSelected(mi);
        }
        return true;
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_LONG)
                .setAnchorView(fab)
                .show();
    }

    // Item 2: "Do you want to go back?"  Yes -> finish(), No -> nothing
    private void showGoBackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.go_back_title)
                .setPositiveButton(R.string.ok, (dialog, id) -> finish())
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog: do nothing
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Item 3: custom dialog with an image and one EditText for the new message
    private void showCustomDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_custom, null);
        EditText input = view.findViewById(R.id.new_message);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.custom_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    String text = input.getText().toString().trim();
                    if (!text.isEmpty()) {
                        snackbarMessage = text;
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog: do nothing
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}