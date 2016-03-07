package com.example.aufgabenundnotizen.customviews;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.aufgabenundnotizen.R;
import com.example.aufgabenundnotizen.helpers.DateHelper;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Tobias on 03.03.16.
 */
public class DateView extends RelativeLayout implements View.OnClickListener, View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener {

    private ImageView mImvCalendar, mImvReset;
    private TextView mTevDate;
    private View mDivider;

    private Drawable mDrawableLeft, mDrawableRight;
    private String mHint;

    private final int DEF_ATTR_BACKGROUND = R.attr.selectableItemBackground;
    private final boolean DEF_ATTR_CLICKABLE = true;
    private final boolean DEF_ATTR_FOCUSABLE = true;
    private final boolean DEF_ATTR_FOCUSABLEINTOUCHMODE = true;
    private final int DEF_ATTR_PADDINGTOP = convertDpToPixel(4);
    private final int DEF_ATTR_PADDINGBOTTOM = convertDpToPixel(4);

    private Date mDate;

    public DateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public DateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public DateView(Context context) {
        super(context);
        init(context, null, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        inflate(getContext(), R.layout.view_dateview, this);

        setDefaultAttributes();

        mImvCalendar = (ImageView) this.findViewById(R.id.imv_calendar);
        mImvReset = (ImageView) this.findViewById(R.id.imv_reset);
        mTevDate = (TextView) this.findViewById(R.id.tev_date);
        mDivider = this.findViewById(R.id.divider);

        this.setOnClickListener(this);
        mImvCalendar.setOnClickListener(this);
        mImvReset.setOnClickListener(this);
        mTevDate.setOnClickListener(this);

        this.setOnFocusChangeListener(this);
        mImvCalendar.setOnFocusChangeListener(this);
        mImvReset.setOnFocusChangeListener(this);
        mTevDate.setOnFocusChangeListener(this);

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.DateView,
                    defStyleAttr,
                    0);

            try {
                mDrawableLeft = a.getDrawable(R.styleable.DateView_drawableLeft);
                mDrawableRight = a.getDrawable(R.styleable.DateView_drawableRight);
                mHint = a.getString(R.styleable.DateView_hint);
            } finally {
                a.recycle();
            }
        }

        setDrawableLeft(mDrawableLeft);
        setDrawableRight(mDrawableRight);
        setHint(mHint);
    }

    public Drawable getDrawableLeft() {
        return mDrawableLeft;
    }

    public void setDrawableLeft(Drawable drawable) {
        if (mImvCalendar != null) {
            mImvCalendar.setImageDrawable(drawable);
        }
    }

    public Drawable getDrawableRight() {
        return mDrawableRight;
    }

    public void setDrawableRight(Drawable drawable) {
        if (mImvReset != null) {
            mImvReset.setImageDrawable(drawable);
        }
    }

    public void setHint(String hint) {
        if (mTevDate != null) {
            mTevDate.setHint(hint);
        }
    }

    /**
     * Achtung: Kann null sein.
     */
    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
        mTevDate.setText(getFormattedDateString(mDate));
    }

    private String getFormattedDateString(Date date) {
        if (date == null) {
            return "";
        }

        String formattedDateString;
        final String dueString = "fällig: ";
        String pattern;

        if(DateHelper.isToday(date)) {
            formattedDateString = dueString + "Heute";
        } else if (DateHelper.wasYesterday(date)) {
            formattedDateString = dueString + "Gestern";
        } else if (DateHelper.isTomorrow(date)) {
            formattedDateString = dueString + "Morgen";
        } else {
            if (DateHelper.isCurrentYear(date)) {
                pattern = "EEE d. MMM";
            } else {
                pattern = "EEE d. MMMM yyyy";
            }

            final DateFormat df = new SimpleDateFormat(pattern, Locale.GERMANY);
            formattedDateString = dueString + df.format(date);
        }

        return formattedDateString;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == this.getId() | id == R.id.imv_calendar | id == R.id.tev_date) {
            Log.i("demo", getResources().getResourceEntryName(id) + " clicked");
            this.requestFocus();
            createDatePickerDialog();
        } else if (id == R.id.imv_reset) {
            Log.i("demo", "Reset clicked");
            setDate(null);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();

        Log.i("demo", getResources().getResourceEntryName(id) + " onFocusChange called");

        if (id == this.getId() && v.hasFocus()) {
            animate(R.color.colorAccent, true);
        } else {
            animate(R.color.colorEditTextDivider, false);
        }
    }

    private void animate(int colorResId, boolean hasFocus) {
        setDrawableLeftColor(colorResId);
        mDivider.setBackgroundColor(ContextCompat.getColor(getContext(), colorResId));

        if (hasFocus) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mDivider.getLayoutParams();
            layoutParams.height = convertDpToPixel(2);

            mDivider.setLayoutParams(layoutParams);
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mDivider.getLayoutParams();
            layoutParams.height = convertDpToPixel(1);

            mDivider.setLayoutParams(layoutParams);
        }
    }

    private void setDrawableLeftColor(int resId) {
        int newColor = ContextCompat.getColor(getContext(), resId);

        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
        mDrawableLeft.setColorFilter(porterDuffColorFilter);

        setDrawableLeft(mDrawableLeft);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        setDate(DateHelper.getDate(year, monthOfYear, dayOfMonth));
    }

    private int getDrawableId(int attribute) {
        int id = 0;

        int[] attrs = new int[]{attribute};
        TypedArray a = getContext().obtainStyledAttributes(attrs);
        try {
            id = a.getResourceId(a.getIndex(0), 0);
        } finally {
            a.recycle();
        }

        return id;
    }

    private void createDatePickerDialog() {
        Calendar cal = Calendar.getInstance();

        if (mDate != null) {
            // Wenn Datum gesetzt, dann damit öffnen
            cal.setTime(mDate);
        }

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );

        try {
            Activity activity = (Activity) getContext();
            dpd.show(activity.getFragmentManager(), "Datepickerdialog");
        } catch (Exception e) {
            // TODO: Logging anpassen
            Log.i("Demo", e.getMessage());
        }
    }

    private void setDefaultAttributes() {
        int backgroundResId = getDrawableId(DEF_ATTR_BACKGROUND);
        if (backgroundResId != 0) {
            setBackgroundResource(backgroundResId);
        }
        setClickable(DEF_ATTR_CLICKABLE);
        setFocusable(DEF_ATTR_FOCUSABLE);
        setFocusableInTouchMode(DEF_ATTR_FOCUSABLEINTOUCHMODE);
        setPadding(0, DEF_ATTR_PADDINGTOP, 0, DEF_ATTR_PADDINGBOTTOM);
    }

    public static int convertDpToPixel(int dp){
        Resources resources = Resources.getSystem();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
}
