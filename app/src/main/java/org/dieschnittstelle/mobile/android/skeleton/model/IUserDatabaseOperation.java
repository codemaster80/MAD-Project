package org.dieschnittstelle.mobile.android.skeleton.model;

import androidx.room.Query;

public interface IUserDatabaseOperation {

    boolean authenticateUser(String username, String password);

    long createUser(User user);

    boolean updateUser(User user);

    boolean deleteUser(long id);

    boolean deleteAllUsers();
}
