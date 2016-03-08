package com.example.customviews;

import android.app.Activity;
import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * Created by Tobias on 07.03.16.
 */
public class DateTimeView extends PercentRelativeLayout implements View.OnClickListener, View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private LocalDate mDate;
    private LocalTime mTime;

    private ImageView mImvAlarm;
    private ImageView mImvReset;
    private TextView mTevLabel;
    private TextView mTevDate;
    private TextView mTevTime;
    private View mDivider;
    private View mDivider2;

    public DateTimeView(Context context) {
        super(context, null, R.attr.timeViewDefStyle);
        init();
    }

    public DateTimeView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.timeViewDefStyle);
        init();
    }

    public DateTimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, R.attr.timeViewDefStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_datetime, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mImvAlarm = (ImageView) this.findViewById(R.id.imv_alarm);
        mImvReset = (ImageView) this.findViewById(R.id.imv_reset);
        mTevLabel = (TextView) this.findViewById(R.id.tev_label);
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
            setDate(null);
            setTime(null);
        } else {
            this.requestFocus();

            if (id == R.id.tev_date | id == R.id.divider) {
                createDatePickerDialog();
            } else {
                createTimePickerDialog();
            }
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


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        setTime(new LocalTime(hourOfDay, minute, second));
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        setDate(new LocalDate(year, monthOfYear, dayOfMonth));
    }

    private void focusAnimation(boolean hasFocus) {
        int dividerHeight;

        if (hasFocus) {
            final int accentColor = Helper.getColorValueByAttr(getContext(), R.attr.colorAccent);

            Helper.changeImageViewDrawableColor(mImvAlarm, accentColor);
            mDivider.setBackgroundColor(accentColor);
            mDivider2.setBackgroundColor(accentColor);

            dividerHeight = 2;
        } else {
            final int colorPrimaryDark = Helper.getColorValueByAttr(getContext(), R.attr.colorPrimaryDark);
            final int dividerColor = Helper.getColorValueByRes(getContext(), R.color.colorEditTextDivider);

            Helper.changeImageViewDrawableColor(mImvAlarm, colorPrimaryDark);
            mDivider.setBackgroundColor(dividerColor);
            mDivider2.setBackgroundColor(dividerColor);

            dividerHeight = 1;
        }

        Helper.setViewHeight(mDivider, dividerHeight);
        Helper.setViewHeight(mDivider2, dividerHeight);
    }

    private void createTimePickerDialog() {
        DateTime dt = Helper.getGermanTime();

        TimePickerDialog tpd = TimePickerDialog.newInstance(
                DateTimeView.this,
                dt.getHourOfDay(),
                dt.getMinuteOfHour(),
                true);

        Activity activity = (Activity) getContext();

        if (activity != null) {
            tpd.show(activity.getFragmentManager(), "TimePickerDialog");
        }
    }

    private void createDatePickerDialog() {
        DateTime dt = Helper.getGermanTime();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                DateTimeView.this,
                dt.getYear(),
                dt.getMonthOfYear(),
                dt.getDayOfMonth()
        );

        Activity activity = (Activity) getContext();

        if (activity != null) {
            dpd.show(activity.getFragmentManager(), "DatePickerDialog");
        }
    }

    public void setDate(LocalDate date) {
        this.mDate = date;

        if (date != null) {
            mTevDate.setText(Helper.getFormattedDateString(date));

            // Wenn noch keine Zeit gesetzt wurde, dann auf Mitternacht setzen
            if (mTime == null) {
                DateTime dateTime = Helper.getGermanTime();
                setTime(LocalTime.MIDNIGHT);
            }
        } else {
            mTevDate.setText("");
        }
    }

    public void setTime(LocalTime time) {
        this.mTime = time;

        if (time != null) {
            mTevTime.setText(Helper.getFormattedTimeString(time) + " Uhr");

            // Wenn noch kein Datum gesetzt wurde, dann schauen ob
            // ausgewählte Zeit vor oder nach aktueller Zeit liegt und
            // entspr. Datum auf Heute oder Morgen setzen
            if (mDate == null) {
                DateTime dateTime = Helper.getGermanTime();

                if (mTime.isAfter(dateTime.toLocalTime())) {
                    setDate(dateTime.toLocalDate());
                } else {
                    setDate(dateTime.toLocalDate().plusDays(1));
                }
            }

        } else {
            mTevTime.setText("");
        }
    }

    public DateTime getDateTime() {
        if (mDate != null) {
            if (mTime != null) {
                return mDate.toDateTime(mTime);
            } else {
                return mDate.toDateTime(LocalTime.MIDNIGHT);
            }
        } else {
            if (mTime != null) {
                return mTime.toDateTimeToday();
            } else {
                return null;
            }
        }
    }

}
