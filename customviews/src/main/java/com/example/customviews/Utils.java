package com.example.customviews;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Created by Tobias on 07.03.16.
 */
public final class Utils {

    private Utils() {
        throw new IllegalStateException("No instances.");
    }

    public static int convertDpToPixel(int dp) {
        Resources resources = Resources.getSystem();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * Achtung: Gibt 0 zurück wenn Ressource nicht gefunden.
     */
    public static int getColorValueByRes(Context context, int colorResId) {
        try {
            return ContextCompat.getColor(context, colorResId);
        } catch (Resources.NotFoundException e) {
            return 0;
        }
    }

    /**
     * Achtung: Gibt 0 zurück wenn Attribut nicht gefunden wird.
     */
    public static int getColorValueByAttr(Context context, int attrResId) {
        int color = 0;

        int[] attrs = {attrResId};
        TypedArray a = context.obtainStyledAttributes(attrs);
        try {
            color = a.getColor(a.getIndex(0), 0);
        } finally {
            a.recycle();
        }

        return color;
    }

    private static final String CENTRAL_EUROPEAN_TIME_ID = "CET";

    public static DateTimeZone getGermanDateTimeZone() {
        return DateTimeZone.forID(CENTRAL_EUROPEAN_TIME_ID);
    }

    public static DateTime getGermanDateTime() {
        return new DateTime(getGermanDateTimeZone());
    }

}
