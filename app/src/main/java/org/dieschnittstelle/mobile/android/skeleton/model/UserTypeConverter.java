package org.dieschnittstelle.mobile.android.skeleton.model;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

public class UserTypeConverter {
        @TypeConverter
        public static String fromUser(User user) {
            // Convert User to a database-storable format
            return new Gson().toJson(user);
        }

        @TypeConverter
        public static User toUser(String userJson) {
            // Convert database value back to User
            return new Gson().fromJson(userJson, User.class);
        }
}
