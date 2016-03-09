package com.example.customviews;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

/**
 * Created by Tobias on 08.03.16.
 */
abstract class DateViewBase extends PercentRelativeLayout implements View.OnClickListener, View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener {

    protected LocalDate mDate;

    private static final String ARG_SUPERSTATE = "super_state";
    private static final String ARG_STATEDATA = "state_data";

    private static final String TAG_DATEPICKERDIALOG = "datepickerdialog";

    public DateViewBase(Context context) {
        super(context, null, R.attr.dateViewDefStyle);
    }

    public DateViewBase(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.dateViewDefStyle);
    }

    public DateViewBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, R.attr.dateViewDefStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        this.setOnClickListener(this);
        this.setOnFocusChangeListener(this);

        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);

            child.setOnClickListener(this);
            child.setOnFocusChangeListener(this);
        }
    }

    @Override
    public final void onClick(View v) {
        int id = v.getId();

        // TODO: schauen ob das bei beiden funktioniert
        if (id == R.id.imv_reset) {
            onViewReset();
        } else {
            this.requestFocus();
            onViewClick(v);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();

        if (id == this.getId() && hasFocus) {
            onViewFocusGained();
        } else {
            onViewFocusLost();
        }
    }

    /**
     * Wird aufgerufen sobald der Bildschirm gedreht wird.
     * Hier kann der State der View gespeichert werden.
     * Achtung: View braucht eine ID und muss saveEnabled sein.
     * @return ein Parcelable Objekt (Hier: Bundle) welches gespeichert werden soll.
     */
    @Override
    protected final Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_SUPERSTATE, super.onSaveInstanceState());

        Bundle stateData = putStateData();
        if (stateData != null) {
            bundle.putBundle(ARG_STATEDATA, stateData);
        }

        return bundle;
    }

    /**
     * Wird nach onSaveInstanceState aufgerufen.
     * Hier kann der State der View wiederhergestellt werden.
     * @param state der wiederhergestelt werden kann.
     */
    @Override
    protected final void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            Bundle stateData = bundle.getBundle(ARG_STATEDATA);
            if (stateData != null) {
                getStateData(stateData);
            }

            state = bundle.getParcelable(ARG_SUPERSTATE);
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        setDate(new LocalDate(year, monthOfYear + 1, dayOfMonth));
    }

    protected abstract void onViewClick(View child);

    protected abstract void onViewReset();

    /**
     * Wird in onSaveInstanceState aufgerufen und ermöglicht das
     * Speichern von eigenen Daten bei Drehung des Bildschirms.
     * @return StateData die gespeichert werden sollen.
     */
    protected abstract Bundle putStateData();

    /**
     * Wird in onRestoreInstanceState aufgerufen und ermöglicht das
     * Wiederherstellen von eigenen Daten nach Drehung des Bildschirms.
     * Achtung: putStateData müssen einen Wert != null zurückgeben damit
     * diese Methode aufgerufen wird.
     * @param stateData die wiederhergestellt werden sollen.
     */
    protected abstract void getStateData(Bundle stateData);

    /**
     * Wird aufgerufen sobald die View den Focus erlangt.
     */
    protected abstract void onViewFocusGained();

    /**
     * Wird aufgerufen sobald die View den Focus verliert.
     */
    protected abstract void onViewFocusLost();

    protected void createDatePickerDialog() {
        DateTime dt;

        if (mDate != null) {
            dt = mDate.toDateTime(LocalTime.MIDNIGHT);
        } else {
            dt = Helper.getGermanTime();
        }

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                DateViewBase.this,
                dt.getYear(),
                dt.getMonthOfYear() - 1,
                dt.getDayOfMonth()
        );

        Activity activity = (Activity) getContext();

        if (activity != null) {
            dpd.show(activity.getFragmentManager(), TAG_DATEPICKERDIALOG);
        }
    }

    protected String getFormattedDateString(LocalDate date) {
        if (date == null) {
            return "";
        }

        String formattedString;
        String pattern;

        LocalDate dateNow = LocalDate.now(Helper.getGermanTimeZone());

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

        return formattedString;
    }

    protected void changeImageViewDrawableColor(ImageView imv, int newColor) {
        Drawable drawable = imv.getDrawable();

        PorterDuffColorFilter filter = new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
        drawable.setColorFilter(filter);

        imv.invalidateDrawable(drawable);
    }

    protected void setViewHeight(View v, int height) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
        layoutParams.height = height;

        v.setLayoutParams(layoutParams);
    }

    public abstract void setDate(LocalDate date);
}
