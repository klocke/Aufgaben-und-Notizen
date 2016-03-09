package com.example.customviews;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

/**
 * Created by Tobias on 07.03.16.
 */
public class DateTimeView extends DateViewBase implements TimePickerDialog.OnTimeSetListener {

    private LocalTime mTime;

    private ImageView mImvAlarm;
    private ImageView mImvReset;
    private TextView mTevLabel;
    private TextView mTevDate;
    private TextView mTevTime;
    private View mDivider;
    private View mDivider2;

    private static final String ARG_DATETIME = "datetime";

    public DateTimeView(Context context) {
        super(context);
        init();
    }

    public DateTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DateTimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
    }

    @Override
    protected void onViewClick(View child) {
        int id = child.getId();

        if (id == R.id.tev_date | id == R.id.divider) {
            createDatePickerDialog();
        } else {
            createTimePickerDialog();
        }
    }

    @Override
    protected void onViewReset() {
        setDate(null);
        setTime(null);
    }

    @Override
    protected Bundle putStateData() {
        Bundle stateData = null;
        DateTime dateTime = getDateTime();

        if (dateTime != null) {
            stateData = new Bundle();
            stateData.putSerializable(ARG_DATETIME, dateTime);
        }

        return stateData;
    }

    @Override
    protected void getStateData(Bundle stateData) {
        DateTime dateTime = (DateTime) stateData.getSerializable(ARG_DATETIME);

        if (dateTime != null) {
            setDate(dateTime.toLocalDate());
            setTime(dateTime.toLocalTime());
        }
    }

    @Override
    protected void onViewFocusGained() {
        final int accentColor = Helper.getColorValueByAttr(getContext(), R.attr.colorAccent);
        final int dividerHeight = Helper.convertDpToPixel(2);

        changeImageViewDrawableColor(mImvAlarm, accentColor);
        mDivider.setBackgroundColor(accentColor);
        mDivider2.setBackgroundColor(accentColor);
        setViewHeight(mDivider, dividerHeight);
        setViewHeight(mDivider2, dividerHeight);
    }

    @Override
    protected void onViewFocusLost() {
        final int colorPrimaryDark = Helper.getColorValueByAttr(getContext(), R.attr.colorPrimaryDark);
        final int dividerColor = Helper.getColorValueByRes(getContext(), R.color.colorEditTextDivider);
        final int dividerHeight = Helper.convertDpToPixel(1);

        changeImageViewDrawableColor(mImvAlarm, colorPrimaryDark);
        mDivider.setBackgroundColor(dividerColor);
        mDivider2.setBackgroundColor(dividerColor);
        setViewHeight(mDivider, dividerHeight);
        setViewHeight(mDivider2, dividerHeight);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        setTime(new LocalTime(hourOfDay, minute, 0));
    }

    private void createTimePickerDialog() {
        DateTime dt;

        if (mTime != null) {
            dt = mTime.toDateTimeToday();
        } else {
            dt = Helper.getGermanTime();
        }

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

    public String getFormattedTimeString(LocalTime time) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm");
        return dtf.withLocale(Locale.GERMANY).print(time);
    }

    @Override
    public void setDate(LocalDate date) {
        this.mDate = date;

        if (date != null) {
            mTevDate.setText(getFormattedDateString(date));

            // Wenn noch keine Zeit gesetzt wurde, dann auf 8 Uhr setzen
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
            mTevTime.setText(getFormattedTimeString(time) + " Uhr");

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
