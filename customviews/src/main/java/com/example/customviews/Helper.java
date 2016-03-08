package com.example.customviews;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

/**
 * Created by Tobias on 07.03.16.
 */
public class Helper {

    public static int convertDpToPixel(int dp) {
        Resources resources = Resources.getSystem();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static int getColorValueByRes(Context context, int colorResId) {
        return ContextCompat.getColor(context, colorResId);
    }

    /**
     * Achtung: Gibt 0 zurück wenn Attribut nicht gefunden wird.
     */
    public static int getColorValueByAttr(Context context, int attrResId) {
        int color = 0;

        TypedArray a = null;
        try {
            int[] attrs = {attrResId};
            a = context.obtainStyledAttributes(attrs);
            color = a.getColor(0, 0);
        } finally {
            if (a != null) {
                a.recycle();
            }
        }

        return color;
    }

    public static void changeImageViewDrawableColor(ImageView imageView, int newColor) {
        Drawable imvDrawable = imageView.getDrawable();

        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
        imvDrawable.setColorFilter(porterDuffColorFilter);

        imageView.invalidateDrawable(imvDrawable);
    }

    public static void setViewHeight(View v, int heightInDp) {
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        layoutParams.height = Helper.convertDpToPixel(heightInDp);

        v.setLayoutParams(layoutParams);
    }

    private static final String CENTRAL_EUROPEAN_TIME_ID = "CET";

    public static DateTimeZone getGermanTimeZone() {
        return DateTimeZone.forID(CENTRAL_EUROPEAN_TIME_ID);
    }

    public static DateTime getGermanTime() {
        return new DateTime(getGermanTimeZone());
    }

    public static String getFormattedDateString(LocalDate date) {
        if (date == null) {
            return "";
        }

        String formattedString;
        String pattern;

        LocalDate dateNow = LocalDate.now(getGermanTimeZone());

        if (date.isEqual(dateNow)) {
            formattedString = "Heute";
        } else if (date.equals(dateNow.minusDays(1))) {
            formattedString = "Gestern";
        } else if (date.equals(dateNow.plusDays(1))) {
            formattedString = "Morgen";
        } else {
            if (date.getYear() == dateNow.getYear()) {
                pattern = "EEE d. MMM";
            } else {
                pattern = "EEE d. MMM yyyy";
            }

            DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
            formattedString = dtf.withLocale(Locale.GERMANY).print(date);
        }

        return formattedString.toString();
    }

    public static String getFormattedTimeString(LocalTime time) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
        return dtf.withLocale(Locale.GERMANY).print(time);
    }

    private static final DateTime JAN_1_1970 = new DateTime(1970, 1, 1, 0, 0, DateTimeZone.UTC);

    public static long toMillisSinceEpoch(DateTime dateTime) {
        return new Duration(JAN_1_1970, dateTime).getMillis();
    }

}
