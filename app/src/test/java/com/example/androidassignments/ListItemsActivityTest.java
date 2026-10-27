package com.example.androidassignments;

import org.robolectric.shadows.ShadowLooper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.shadows.ShadowToast;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {34})
public class ListItemsActivityTest {

    @Test
    public void switchShowsShortToastWhenOnAndLongToastWhenOff() {
        try (ActivityScenario<ListItemsActivity> s = ActivityScenario.launch(ListItemsActivity.class)) {
            s.onActivity(a -> {
                Switch sw = a.findViewById(R.id.switchView);
                sw.setChecked(true);
                assertEquals("Switch is On", ShadowToast.getTextOfLatestToast());
                assertEquals(Toast.LENGTH_SHORT, ShadowToast.getLatestToast().getDuration());
                sw.setChecked(false);
                assertEquals("Switch is Off", ShadowToast.getTextOfLatestToast());
                assertEquals(Toast.LENGTH_LONG, ShadowToast.getLatestToast().getDuration());
            });
        }
    }

    @Test
    public void checkBoxDialogYesFinishesWithResponse() {
        try (ActivityScenario<ListItemsActivity> s = ActivityScenario.launch(ListItemsActivity.class)) {
            s.onActivity(a -> {
                ((CheckBox) a.findViewById(R.id.checkBox)).performClick();
                AlertDialog dialog = (AlertDialog) ShadowDialog.getLatestDialog();
                assertNotNull(dialog);
                assertTrue(dialog.isShowing());
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                ShadowLooper.idleMainLooper();
                assertTrue(a.isFinishing());
                assertEquals(Activity.RESULT_OK, shadowOf(a).getResultCode());
                assertEquals("My information to share",
                        shadowOf(a).getResultIntent().getStringExtra("Response"));
            });
        }
    }

    @Test
    public void checkBoxDialogNoJustDismisses() {
        try (ActivityScenario<ListItemsActivity> s = ActivityScenario.launch(ListItemsActivity.class)) {
            s.onActivity(a -> {
                ((CheckBox) a.findViewById(R.id.checkBox)).performClick();
                AlertDialog dialog = (AlertDialog) ShadowDialog.getLatestDialog();
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).performClick();
                ShadowLooper.idleMainLooper();
                assertFalse(dialog.isShowing());
                assertFalse(a.isFinishing());
            });
        }
    }

    @Test
    public void imageButtonLaunchesCameraWhenPermissionGranted() {
        Application app = ApplicationProvider.getApplicationContext();
        shadowOf(app).grantPermissions(Manifest.permission.CAMERA);
        try (ActivityScenario<ListItemsActivity> s = ActivityScenario.launch(ListItemsActivity.class)) {
            s.onActivity(a -> {
                ((ImageButton) a.findViewById(R.id.imageButton)).performClick();
                ShadowActivity.IntentForResult r = shadowOf(a).getNextStartedActivityForResult();
                assertNotNull(r);
                assertEquals(MediaStore.ACTION_IMAGE_CAPTURE, r.intent.getAction());
                assertEquals(11, r.requestCode);
            });
        }
    }

    @Test
    public void pictureResultReplacesTheButtonImage() {
        Application app = ApplicationProvider.getApplicationContext();
        shadowOf(app).grantPermissions(Manifest.permission.CAMERA);
        try (ActivityScenario<ListItemsActivity> s = ActivityScenario.launch(ListItemsActivity.class)) {
            s.onActivity(a -> {
                ImageButton ib = a.findViewById(R.id.imageButton);
                Drawable before = ib.getDrawable();
                ib.performClick();
                ShadowActivity.IntentForResult r = shadowOf(a).getNextStartedActivityForResult();
                Intent data = new Intent();
                data.putExtra("data", Bitmap.createBitmap(8, 8, Bitmap.Config.ARGB_8888));
                shadowOf(a).receiveResult(r.intent, Activity.RESULT_OK, data);
                assertNotSame(before, ib.getDrawable());
            });
        }
    }

    @Test
    public void deniedCameraPermissionShowsMessage() {
        try (ActivityScenario<ListItemsActivity> s = ActivityScenario.launch(ListItemsActivity.class)) {
            s.onActivity(a -> {
                a.onRequestPermissionsResult(12,
                        new String[]{Manifest.permission.CAMERA},
                        new int[]{PackageManager.PERMISSION_DENIED});
                assertEquals("Camera permission is required to take a picture",
                        ShadowToast.getTextOfLatestToast());
            });
        }
    }

    @Test
    public void grantedCameraPermissionResultLaunchesCamera() {
        try (ActivityScenario<ListItemsActivity> s = ActivityScenario.launch(ListItemsActivity.class)) {
            s.onActivity(a -> {
                a.onRequestPermissionsResult(12,
                        new String[]{Manifest.permission.CAMERA},
                        new int[]{PackageManager.PERMISSION_GRANTED});
                ShadowActivity.IntentForResult r = shadowOf(a).getNextStartedActivityForResult();
                assertNotNull(r);
                assertEquals(MediaStore.ACTION_IMAGE_CAPTURE, r.intent.getAction());
            });
        }
    }

    @Test
    public void canceledPhotoKeepsTheOriginalImage() {
        Application app = ApplicationProvider.getApplicationContext();
        shadowOf(app).grantPermissions(Manifest.permission.CAMERA);
        try (ActivityScenario<ListItemsActivity> s = ActivityScenario.launch(ListItemsActivity.class)) {
            s.onActivity(a -> {
                ImageButton ib = a.findViewById(R.id.imageButton);
                Drawable before = ib.getDrawable();
                ib.performClick();
                ShadowActivity.IntentForResult r = shadowOf(a).getNextStartedActivityForResult();
                shadowOf(a).receiveResult(r.intent, Activity.RESULT_CANCELED, null);
                assertSame(before, ib.getDrawable());
            });
        }
    }

    @Test
    public void emptyPermissionResultCountsAsDenied() {
        try (ActivityScenario<ListItemsActivity> s = ActivityScenario.launch(ListItemsActivity.class)) {
            s.onActivity(a -> {
                a.onRequestPermissionsResult(12, new String[0], new int[0]);
                assertEquals("Camera permission is required to take a picture",
                        ShadowToast.getTextOfLatestToast());
            });
        }
    }

    @Test
    public void unrelatedPermissionResultIsIgnored() {
        try (ActivityScenario<ListItemsActivity> s = ActivityScenario.launch(ListItemsActivity.class)) {
            s.onActivity(a -> {
                a.onRequestPermissionsResult(99, new String[0], new int[0]);
                assertNull(ShadowToast.getLatestToast());
            });
        }
    }
}
