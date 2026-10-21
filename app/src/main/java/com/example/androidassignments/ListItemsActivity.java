package com.example.androidassignments;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ListItemsActivity extends AppCompatActivity {

    protected static final String ACTIVITY_NAME = "ListItemsActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 11;
    private static final int REQUEST_CAMERA_PERMISSION = 12;

    private ImageButton imageButton;

    private TextView logView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLog.i(ACTIVITY_NAME, "ListItemsActivity: inside onCreate");
        setContentView(R.layout.activity_list_items);

        imageButton = findViewById(R.id.imageButton);
        Switch switchView = findViewById(R.id.switchView);
        CheckBox checkBox = findViewById(R.id.checkBox);

        imageButton.setOnClickListener(v -> openCamera());

        switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CharSequence text = isChecked
                    ? getString(R.string.switch_on)
                    : getString(R.string.switch_off);
            int duration = isChecked ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        });

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> showFinishDialog());
    }

    // Toasts the incoming message
    private void print(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }
        launchCamera();
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            print(getString(R.string.camera_unavailable));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                print(getString(R.string.camera_permission_denied));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK
                && data != null && data.getExtras() != null) {
            Object photo = data.getExtras().get("data");
            if (photo instanceof Bitmap) {
                imageButton.setImageBitmap((Bitmap) photo);
            }
        }
    }

    private void showFinishDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListItemsActivity.this);
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title)
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("Response", getString(R.string.response_message));
                    setResult(Activity.RESULT_OK, resultIntent);
                    AppLog.i(ACTIVITY_NAME, "ListItemsActivity: inside onFinish (finish() called)");
                    finish();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog: do nothing
                })
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppLog.i(ACTIVITY_NAME, "ListItemsActivity: inside onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLog.i(ACTIVITY_NAME, "ListItemsActivity: inside onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppLog.i(ACTIVITY_NAME, "ListItemsActivity: inside onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppLog.i(ACTIVITY_NAME, "ListItemsActivity: inside onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppLog.i(ACTIVITY_NAME, "ListItemsActivity: inside onDestroy");
    }
}