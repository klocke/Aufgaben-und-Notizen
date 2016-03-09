package com.example.customviews;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.LocalDate;

/**
 * Created by Tobias on 08.03.16.
 */
public class DateView extends DateViewBase {

    private ImageView mImvCalendar;
    private ImageView mImvReset;
    private TextView mTevLabel;
    private TextView mTevDate;
    private View mDivider;

    private static final String ARG_DATE = "date";

    public DateView(Context context) {
        super(context);
        init();
    }

    public DateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_date, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mImvCalendar = (ImageView) this.findViewById(R.id.imv_calendar);
        mImvReset = (ImageView) this.findViewById(R.id.imv_reset);
        mTevLabel = (TextView) this.findViewById(R.id.tev_label);
        mTevDate = (TextView) this.findViewById(R.id.tev_date);
        mDivider = this.findViewById(R.id.divider);
    }

    @Override
    protected void onViewClick(View child) {
        createDatePickerDialog();
    }

    @Override
    protected void onViewReset() {
        setDate(null);
    }

    @Override
    protected Bundle putStateData() {
        Bundle stateData = new Bundle();
        stateData.putSerializable(ARG_DATE, mDate);
        return stateData;
    }

    @Override
    protected void getStateData(Bundle stateData) {
        LocalDate date = (LocalDate) stateData.getSerializable(ARG_DATE);
        setDate(date);
    }

    @Override
    protected void onViewFocusGained() {
        final int accentColor = Helper.getColorValueByAttr(getContext(), R.attr.colorAccent);
        final int dividerHeight = Helper.convertDpToPixel(2);

        changeImageViewDrawableColor(mImvCalendar, accentColor);
        mDivider.setBackgroundColor(accentColor);
        setViewHeight(mDivider, dividerHeight);
    }

    // Achtung: Die Methode onFocusLost() scheint es intern (evtl. im globalen Context) schon zu geben,
    // daher wird sie systemintern aufgerufen, was zu unerwartetem Verhalten führt.
    @Override
    protected void onViewFocusLost() {
        final int colorPrimaryDark = Helper.getColorValueByAttr(getContext(), R.attr.colorPrimaryDark);
        final int dividerColor = Helper.getColorValueByRes(getContext(), R.color.colorEditTextDivider);
        final int dividerHeight = Helper.convertDpToPixel(1);

        changeImageViewDrawableColor(mImvCalendar, colorPrimaryDark);
        mDivider.setBackgroundColor(dividerColor);
        setViewHeight(mDivider, dividerHeight);
    }

    @Override
    public void setDate(LocalDate date) {
        this.mDate = date;

        if (date != null) {
            mTevDate.setText(getFormattedDateString(date));
        } else {
            mTevDate.setText("");
        }
    }

    public LocalDate getDate() {
        return mDate;
    }


}
