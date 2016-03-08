package com.example.aufgabenundnotizen.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    public static void changeImageViewDrawableColor(ImageView imageView, int newColor) {
        Drawable imvDrawable = imageView.getDrawable();

        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
        imvDrawable.setColorFilter(porterDuffColorFilter);

        imageView.invalidateDrawable(imvDrawable);
    }

    public static void setViewHeight(View v, int heightInDp) {
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        layoutParams.height = UniversalHelper.convertDpToPixel(heightInDp);

        v.setLayoutParams(layoutParams);
    }

}
