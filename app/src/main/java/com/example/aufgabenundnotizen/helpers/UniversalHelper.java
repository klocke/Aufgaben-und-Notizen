package com.example.aufgabenundnotizen.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

/**
 * Created by Tobias on 07.03.16.
 */
public class UniversalHelper {

    public static int convertDpToPixel(int dp) {
        Resources resources = Resources.getSystem();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static int getColor(Context context, int colorResId) {
        return ContextCompat.getColor(context, colorResId);
    }

}
