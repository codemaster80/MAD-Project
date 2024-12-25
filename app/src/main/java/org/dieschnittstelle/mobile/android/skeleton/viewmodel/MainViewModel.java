package org.dieschnittstelle.mobile.android.skeleton.viewmodel;
import android.util.Log;
import androidx.lifecycle.ViewModel;
import org.dieschnittstelle.mobile.android.skeleton.model.User;

    public class MainViewModel extends ViewModel {
        private User user;
        public String username;
        public String password;

        public void login() {
            Log.i("Login", "Login successfull");
        }
    }
