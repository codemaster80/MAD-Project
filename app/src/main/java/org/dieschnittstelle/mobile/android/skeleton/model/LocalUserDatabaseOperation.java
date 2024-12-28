package org.dieschnittstelle.mobile.android.skeleton.model;

import android.content.Context;
import android.util.Log;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.Update;

import java.util.List;

public class LocalUserDatabaseOperation implements IUserDatabaseOperation {

    @Dao
    @TypeConverters(UserTypeConverter.class)
    public interface SQLITEUserCRUDOperation {

        @Query("SELECT * FROM user WHERE email=(:username) AND pwd=(:password)")
        boolean authenticateUser(String username, String password);

        @Insert
        long createUser(User user);

        @Update
        void updateUser(User user);

        @Delete
        void deleteUser(User user);

        @Query("DELETE FROM user")
        void deleteAllUsers();
    }

    @Database(entities = {User.class}, version = 1)
    public abstract static class UserDatabase extends RoomDatabase {

        public abstract LocalUserDatabaseOperation.SQLITEUserCRUDOperation getDao();
    }

    private final UserDatabase userDatabase;

    public LocalUserDatabaseOperation(Context context) {
        userDatabase = Room.databaseBuilder(
                        context.getApplicationContext(),
                        LocalUserDatabaseOperation.UserDatabase.class,
                        "user-db"
                )
                // clear DB when schema changed
                .fallbackToDestructiveMigration()
                .build();
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        return userDatabase.getDao().authenticateUser(username, password);
    }

    @Override
    public long createUser(User user) {
        Log.i("CreateUserLocal", "Yes");
        long newUserId = userDatabase.getDao().createUser(user);
        return newUserId;
    }

    @Override
    public boolean updateUser(User user) {
        userDatabase.getDao().updateUser(user);
        return true;
    }

    @Override
    public boolean deleteUser(long id) {
        return false;
    }

    @Override
    public boolean deleteAllUsers() {
        userDatabase.getDao().deleteAllUsers();
        return true;
    }
}
