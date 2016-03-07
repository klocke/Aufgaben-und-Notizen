package com.example.aufgabenundnotizen.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aufgabenundnotizen.R;
import com.example.aufgabenundnotizen.helpers.UniversalHelper;

/**
 * Created by Tobias on 07.03.16.
 */
public class TimeView extends PercentRelativeLayout implements View.OnClickListener, View.OnFocusChangeListener {

    private ImageView mImvAlarm;
    private ImageView mImvReset;
    private TextView mTevDate;
    private TextView mTevTime;
    private View mDivider;
    private View mDivider2;

    public TimeView(Context context) {
        super(context, null, R.attr.timeViewDefStyle);
        init(null);
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.timeViewDefStyle);
        init(attrs);
    }

    public TimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, R.attr.timeViewDefStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.view_timeview, this);

        // benutzerdefinierte Attribute einlesen
        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs, R.styleable.TimeView, 0, 0);
            try {
                // TODO:
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mImvAlarm = (ImageView) this.findViewById(R.id.imv_alarm);
        mImvReset = (ImageView) this.findViewById(R.id.imv_reset);
        mTevDate = (TextView) this.findViewById(R.id.tev_date);
        mTevTime = (TextView) this.findViewById(R.id.tev_time);
        mDivider = this.findViewById(R.id.divider);
        mDivider2 = this.findViewById(R.id.divider2);

        this.setOnClickListener(this);
        this.setOnFocusChangeListener(this);

        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);

            child.setOnClickListener(this);
            child.setOnFocusChangeListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.imv_reset) {
            Log.i("demo", "ImvReset clicked!");
        } else {
            this.requestFocus();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();

        if (id == this.getId() && hasFocus) {
            focusAnimation(true);
        } else {
            focusAnimation(false);
        }
    }

    private void focusAnimation(boolean hasFocus) {
        int dividerHeight;

        if (hasFocus) {
            final int accentColor = UniversalHelper.getColor(getContext(), R.color.colorAccent);

            changeImageViewDrawableColor(mImvAlarm, accentColor);
            mDivider.setBackgroundColor(accentColor);
            mDivider2.setBackgroundColor(accentColor);

            dividerHeight = 2;
        } else {
            final int colorPrimaryDark = UniversalHelper.getColor(getContext(), R.color.colorPrimaryDark);
            final int dividerColor = UniversalHelper.getColor(getContext(), R.color.colorEditTextDivider);

            changeImageViewDrawableColor(mImvAlarm, colorPrimaryDark);
            mDivider.setBackgroundColor(dividerColor);
            mDivider2.setBackgroundColor(dividerColor);

            dividerHeight = 1;
        }

        setViewHeight(mDivider, dividerHeight);
        setViewHeight(mDivider2, dividerHeight);
    }

    private void changeImageViewDrawableColor(ImageView imageView, int newColor) {
        Drawable imvDrawable = imageView.getDrawable();

        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
        imvDrawable.setColorFilter(porterDuffColorFilter);

        imageView.invalidateDrawable(imvDrawable);
    }

    private void setViewHeight(View v, int heightInDp) {
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        layoutParams.height = UniversalHelper.convertDpToPixel(heightInDp);

        v.setLayoutParams(layoutParams);
    }

}
