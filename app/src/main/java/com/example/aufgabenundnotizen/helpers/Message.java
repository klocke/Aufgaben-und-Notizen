package com.example.aufgabenundnotizen.helpers;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Tobias on 11.03.16.
 */
public final class Message {

    private Message() {
        throw new IllegalStateException("No Instances.");
    }

    public static void show(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
