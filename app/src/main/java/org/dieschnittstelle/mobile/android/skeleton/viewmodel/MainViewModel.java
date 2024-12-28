package org.dieschnittstelle.mobile.android.skeleton.viewmodel;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.MemoryLruGcSettings;

import org.dieschnittstelle.mobile.android.skeleton.TaskListViewActivity;
import org.dieschnittstelle.mobile.android.skeleton.model.ITaskDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.IUserDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.LocalTaskDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.LocalUserDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends ViewModel {
        private IUserDatabaseOperation userDBOperation;
        private MutableLiveData<Boolean> isUserAuthenticated = new MutableLiveData<>(false);
        private User user;
        public String email;
        public String pwd;

        public void authenticateUser() {
            Log.i("Login", "Authenticate User Method Model");
            new Thread(() -> {
                // Log.i("Login", String.valueOf(userDBOperation.authenticateUser("a@b.de", "test")));
                if (userDBOperation.authenticateUser(email, pwd)) {
                    Log.i("Login", "Authenticate User Method Model2");
                    isUserAuthenticated.postValue(true);
                }
            }).start();
        }

        public MutableLiveData<Boolean> isUserAuthenticated() {
            return isUserAuthenticated;
        }

        public void setUserDBOperation(IUserDatabaseOperation userDBOperation) {
            this.userDBOperation = userDBOperation;
        }

    }
