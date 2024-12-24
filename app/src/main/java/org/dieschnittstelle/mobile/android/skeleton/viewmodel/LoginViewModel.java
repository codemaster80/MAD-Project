package org.dieschnittstelle.mobile.android.skeleton.viewmodel;

import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import org.dieschnittstelle.mobile.android.skeleton.TaskApplication;
import org.dieschnittstelle.mobile.android.skeleton.model.AppContainer;
import org.dieschnittstelle.mobile.android.skeleton.model.UserRepository;

public class LoginViewModel extends ViewModel {
    public String username;
    public String password;

    public LoginViewModel(UserRepository userRepository) {
    }

    public void login() {
        Log.i("Login", "Login successfull");
    }
}
