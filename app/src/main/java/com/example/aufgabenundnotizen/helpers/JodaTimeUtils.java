package com.example.aufgabenundnotizen.helpers;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

/**
 * Created by Tobias on 07.03.16.
 */
public final class JodaTimeUtils {

    private JodaTimeUtils() {
        throw new IllegalStateException("No instances.");
    }

    private static final String CENTRAL_EUROPEAN_TIME_ID = "CET";
    private static final DateTime JAN_1_1970 = new DateTime(1970, 1, 1, 0, 0, DateTimeZone.UTC);

    public static DateTimeZone getGermanTimeZone() {
        return DateTimeZone.forID(CENTRAL_EUROPEAN_TIME_ID);
    }

    public static DateTime getGermanTime() {
        return new DateTime(getGermanTimeZone());
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

        return new Duration(JAN_1_1970, dateTime).getMillis();
    }

    public static long toMillisSinceEpoch(LocalDate localDate) {
        if (localDate == null) {
            return 0;
        }

        return new Duration(JAN_1_1970, localDate.toDateTimeAtStartOfDay(getGermanTimeZone())).getMillis();
    }

    public static DateTime toDateTime(long millisSinceEpoch) {
        if (millisSinceEpoch == 0) {
            return null;
        }

        return new DateTime(millisSinceEpoch, getGermanTimeZone());
    }

    public static LocalDate toLocalDate(long millisSinceEpoch) {
        if (millisSinceEpoch == 0) {
            return null;
        }

        return new LocalDate(millisSinceEpoch, getGermanTimeZone());
    }

}
