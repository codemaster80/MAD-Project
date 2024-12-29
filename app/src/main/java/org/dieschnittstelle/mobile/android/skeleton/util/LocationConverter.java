package org.dieschnittstelle.mobile.android.skeleton.util;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.dieschnittstelle.mobile.android.skeleton.model.Task;

import java.lang.reflect.Type;

// TODO : Missing proper serialization and deserialization of longitude and latitude of location.
public class LocationConverter {
    @TypeConverter
    public static Task.Location fromLocationString(String locationString) {
        Type listType = new TypeToken<Task.Location>() {}.getType();
        return new Gson().fromJson(locationString, listType);
    }

    @TypeConverter
    public static String toLocationString(Task.Location location) {
        Gson gson = new Gson();
        return gson.toJson(location);
    }
}
