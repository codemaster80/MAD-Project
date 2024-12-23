package org.dieschnittstelle.mobile.android.skeleton.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY);

    public static Long fromDateString(String dateTime) {
        if (dateTime == null || dateTime.isBlank()) {
            return null;
        }
        try {
            Date date = DATE_FORMATTER.parse(dateTime);
            return date == null ? null : date.getTime();
        } catch (ParseException e) {
            return null;
        }
    }

    public static String toDateString(Long dateTime) {
        if (dateTime == null || dateTime == 0) {
            return "";
        }
        return DATE_FORMATTER.format(dateTime);
    }

    public static Long fromDate(Date dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.getTime();
    }
}
