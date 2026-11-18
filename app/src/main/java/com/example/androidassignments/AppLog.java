package com.example.androidassignments;

import android.util.Log;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public final class AppLog {

    private static final int MAX_LINES_SHOWN = 14;
    private static final List<String> lines = new ArrayList<>();
    private static WeakReference<TextView> target = new WeakReference<>(null);

    private AppLog() {
    }

    public static void i(String tag, String message) {
        Log.i(tag, message);
        lines.add(message);
        render();
    }

    public static void bind(TextView view) {
        target = new WeakReference<>(view);
        render();
    }

    public static void unbind(TextView view) {
        if (target.get() == view) {
            target = new WeakReference<>(null);
        }
    }

    private static void render() {
        TextView view = target.get();
        if (view == null) {
            return;
        }
        int start = Math.max(0, lines.size() - MAX_LINES_SHOWN);
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < lines.size(); i++) {
            if (i > start) {
                sb.append('\n');
            }
            sb.append(lines.get(i));
        }
        view.setText(sb.toString());
    }
}