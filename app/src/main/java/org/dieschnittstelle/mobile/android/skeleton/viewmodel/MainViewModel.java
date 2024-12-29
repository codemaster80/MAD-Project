package org.dieschnittstelle.mobile.android.skeleton.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import org.dieschnittstelle.mobile.android.skeleton.model.IUserDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainViewModel extends ViewModel {
        private IUserDatabaseOperation userDBOperation;
        private MutableLiveData<String> loginLiveData = new MutableLiveData<>("initial");
        private Pattern passwordPattern = Pattern.compile("[0-9]{6}");
        public User user;

    public MutableLiveData<String> getLoginLiveData() {
        return loginLiveData;
    }

    public void authenticateUser() {
        Log.i("Login", user.getEmail());
        boolean emailCheck = android.util.Patterns.EMAIL_ADDRESS.matcher(user.getEmail()).matches();
        Matcher pwdMatcher = passwordPattern.matcher(user.getPwd());
        boolean pwdCheck = pwdMatcher.find();

        if (!(emailCheck)) {
            loginLiveData.setValue("usernameIsNotAnEMail");
        } else if (!(pwdCheck)) {
            loginLiveData.setValue("passwordIsNotAllowed");
        } else if (pwdCheck == true && emailCheck == true) {
                new Thread(() -> {
                    if (userDBOperation.authenticateUser(user.getEmail(), user.getPwd())) {
                        loginLiveData.postValue("userIsAuthenticated");
                    } else {
                        loginLiveData.postValue("userAuthenticationIsFailed");
                    }
                }).start();
            } else {
                loginLiveData.setValue("usernameIsNotAnEMail");
            }
        }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserDBOperation(IUserDatabaseOperation userDBOperation) {
            this.userDBOperation = userDBOperation;
        }
}
