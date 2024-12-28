package org.dieschnittstelle.mobile.android.skeleton.util;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringListConverter {
    @TypeConverter
    public static String fromList(List<String> list) {
        if (list.isEmpty()) {
            return "";
        }
        return String.join(",", list);
    }

    @TypeConverter
    public static List<String> toList(String str) {
        if (str.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.asList(str.split(","));
    }
}
