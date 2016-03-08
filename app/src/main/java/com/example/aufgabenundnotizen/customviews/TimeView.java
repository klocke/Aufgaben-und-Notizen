package com.example.aufgabenundnotizen.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aufgabenundnotizen.R;
import com.example.aufgabenundnotizen.helpers.UniversalHelper;

import java.util.Calendar;

/**
 * Created by Tobias on 07.03.16.
 */
public class TimeView extends PercentRelativeLayout implements View.OnClickListener, View.OnFocusChangeListener {

    private Calendar mCalendar;

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
        inflate(getContext(), R.layout.view_datetime, this);

        // benutzerdefinierte Attribute einlesen
        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs, R.styleable.DateTimeView, 0, 0);
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

            UniversalHelper.changeImageViewDrawableColor(mImvAlarm, accentColor);
            mDivider.setBackgroundColor(accentColor);
            mDivider2.setBackgroundColor(accentColor);

            dividerHeight = 2;
        } else {
            final int colorPrimaryDark = UniversalHelper.getColor(getContext(), R.color.colorPrimaryDark);
            final int dividerColor = UniversalHelper.getColor(getContext(), R.color.colorEditTextDivider);

            UniversalHelper.changeImageViewDrawableColor(mImvAlarm, colorPrimaryDark);
            mDivider.setBackgroundColor(dividerColor);
            mDivider2.setBackgroundColor(dividerColor);

            dividerHeight = 1;
        }

        UniversalHelper.setViewHeight(mDivider, dividerHeight);
        UniversalHelper.setViewHeight(mDivider2, dividerHeight);
    }

    private void createTimePickerDialog() {

    }

    private void createDatePickerDialog() {

    }

    public Calendar getCalendar() {
        return mCalendar;
    }

    public void setCalendar(Calendar calendar) {
        this.mCalendar = calendar;
        // TODO: Set TextViews
    }
}
