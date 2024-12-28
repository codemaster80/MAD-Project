package org.dieschnittstelle.mobile.android.skeleton.viewmodel;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.dieschnittstelle.mobile.android.skeleton.TaskListViewActivity;
import org.dieschnittstelle.mobile.android.skeleton.model.ITaskDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.User;

    public class MainViewModel extends ViewModel {
        private ITaskDatabaseOperation taskDbOperation;
        private MutableLiveData<Boolean> isUserAuthenticated = new MutableLiveData<>(false);
        private User user;
        public String email;
        public String pwd;


        public void authenticateUser() {
            Log.i("Login", "Authenticate User Method Model");
            isUserAuthenticated.setValue(true);
        }

        public MutableLiveData<Boolean> isUserAuthenticated() {
            return isUserAuthenticated;
        }

    }
