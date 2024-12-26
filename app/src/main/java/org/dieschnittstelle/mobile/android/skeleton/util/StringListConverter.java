package org.dieschnittstelle.mobile.android.skeleton.util;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

// TODO : Missing proper serialization and deserialization of contact information
public class StringListConverter {
    @TypeConverter
    public static String fromList(List<String> list) {
        return "";
    }

    @TypeConverter
    public static List<String> toList(String str) {
        return new ArrayList<>();
    }
}
