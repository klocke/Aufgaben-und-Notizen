package com.example.aufgabenundnotizen.helpers;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

public final class JodaTimeUtils {

    private JodaTimeUtils() {
        throw new IllegalStateException("No instances.");
    }

    private static final String CENTRAL_EUROPEAN_TIME_ID = "CET";

    public static DateTimeZone getGermanDateTimeZone() {
        return DateTimeZone.forID(CENTRAL_EUROPEAN_TIME_ID);
    }

    public static DateTime getGermanDateTime() {
        return new DateTime(getGermanDateTimeZone());
    }

    /**
     * In SQLite gibt es kein Date Format.
     * Best practice: Spalte als "SQLite-Integer" anlegen und Millisekunden als "java-long" schreiben.
     * (s. http://stackoverflow.com/a/13694823/4367848)
     */
    public static long toMillisSinceEpoch(DateTime dateTime) {
        if (dateTime == null) {
            return 0;
        }

        return dateTime.getMillis();
    }

    public static long toMillisSinceEpoch(LocalDate localDate) {
        if (localDate == null) {
            return 0;
        }

        return localDate.toDateTimeAtStartOfDay(getGermanDateTimeZone()).getMillis();
    }

    public static DateTime toDateTime(long millisSinceEpoch) {
        if (millisSinceEpoch == 0) {
            return null;
        }

        return new DateTime(millisSinceEpoch, getGermanDateTimeZone());
    }

    public static LocalDate toLocalDate(long millisSinceEpoch) {
        if (millisSinceEpoch == 0) {
            return null;
        }

        return new LocalDate(millisSinceEpoch, getGermanDateTimeZone());
    }

    public static String getFormattedDateString(LocalDate date) {
        if (date == null) {
            return "";
        }

        String formattedString;
        String pattern;

        LocalDate dateNow = LocalDate.now(getGermanDateTimeZone());

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

}
