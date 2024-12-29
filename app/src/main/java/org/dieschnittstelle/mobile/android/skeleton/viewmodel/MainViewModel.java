package org.dieschnittstelle.mobile.android.skeleton.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import org.dieschnittstelle.mobile.android.skeleton.model.IUserDatabaseOperation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainViewModel extends ViewModel {
        private IUserDatabaseOperation userDBOperation;
        private MutableLiveData<String> loginLiveData = new MutableLiveData<>("initial");
        private Pattern passwordPattern = Pattern.compile("[0-9]{6}");
        public String email;
        public String pwd;

    public MutableLiveData<String> getLoginLiveData() {
        return loginLiveData;
    }

    public void authenticateUser() {
        boolean emailCheck = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        Matcher pwdMatcher = passwordPattern.matcher(pwd);
        boolean pwdCheck = pwdMatcher.find();

        if (!(emailCheck)) {
            loginLiveData.setValue("usernameIsNotAnEMail");
        } else if (!(pwdCheck)) {
            loginLiveData.setValue("passwordIsNotAllowed");
        } else if (pwdCheck == true && emailCheck == true) {
                new Thread(() -> {
                    if (userDBOperation.authenticateUser(email, pwd)) {
                        loginLiveData.postValue("userIsAuthenticated");
                    } else {
                        loginLiveData.postValue("userAuthenticationIsFailed");
                    }
                }).start();
            } else {
                loginLiveData.setValue("usernameIsNotAnEMail");
            }
        }

        public void setUserDBOperation(IUserDatabaseOperation userDBOperation) {
            this.userDBOperation = userDBOperation;
        }
}
