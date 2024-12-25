package org.dieschnittstelle.mobile.android.skeleton.viewmodel;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import org.dieschnittstelle.mobile.android.skeleton.model.ITaskDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.User;

    public class MainViewModel extends ViewModel {
        private ITaskDatabaseOperation taskDbOperation;
        public User user;
        public String username;
        public String password;

        public boolean authenticateUser(String username, String password) {
//            user.setEmail(username);
//            user.setPwd(password);
            Log.i("Login", "Authenticate...");
//            if (taskDbOperation.authenticateUser(user) == true) {
//                Log.i("Login", "Successfully via taskdboperation");
//            }
            return true;
        }
    }
