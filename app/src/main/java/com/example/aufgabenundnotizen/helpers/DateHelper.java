package com.example.aufgabenundnotizen.helpers;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tobias on 05.03.16.
 */
public class DateHelper {


    public static boolean wasYesterday(Date date) {
        return isEqual(date, -1);
    }

    public static boolean isToday(Date date) {
        return isEqual(date, 0);
    }

    public static boolean isTomorrow(Date date) {
        return isEqual(date, 1);
    }

    public static boolean isCurrentYear(Date date) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return currentYear == c.get(Calendar.YEAR);
    }

    private static boolean isEqual(Date date, int todayPlusXDays) {
        boolean res = false;

        Calendar c1 = Calendar.getInstance();   // Heute
        c1.add(Calendar.DAY_OF_YEAR, todayPlusXDays);   // Morgen

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date);

        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {
            res = true;
        }

        return res;
    }

    public static Date getDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth);
        return cal.getTime();
    }
}
