package com.example.androidassignments;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {34})
public class AppLogTest {

    @Test
    public void messagesWithoutABoundViewDoNotCrash() {
        Context ctx = ApplicationProvider.getApplicationContext();
        TextView box = new TextView(ctx);
        AppLog.bind(box);
        AppLog.unbind(box);
        AppLog.i("TestTag", "nobody is listening");
    }

    @Test
    public void unbindingADifferentViewKeepsTheCurrentOne() {
        Context ctx = ApplicationProvider.getApplicationContext();
        TextView first = new TextView(ctx);
        TextView second = new TextView(ctx);
        AppLog.bind(first);
        AppLog.unbind(second);
        AppLog.i("TestTag", "still shown");
        assertTrue(first.getText().toString().contains("still shown"));
        AppLog.unbind(first);
    }

    @Test
    public void onlyTheLastFourteenLinesAreShown() {
        Context ctx = ApplicationProvider.getApplicationContext();
        TextView box = new TextView(ctx);
        AppLog.bind(box);
        for (int i = 1; i <= 20; i++) {
            AppLog.i("TestTag", "line-" + i);
        }
        String text = box.getText().toString();
        assertTrue(text.contains("line-20"));
        assertTrue(text.contains("line-7"));
        assertFalse(text.contains("line-6"));
        AppLog.unbind(box);
    }
}
