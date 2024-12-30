package org.dieschnittstelle.mobile.android.skeleton.viewmodel;

import android.util.Log;
import android.view.inputmethod.EditorInfo;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import org.dieschnittstelle.mobile.android.skeleton.model.IUserDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainViewModel extends ViewModel {
        private IUserDatabaseOperation userDBOperation;
        private final MutableLiveData<LoginState> loginState = new MutableLiveData<>();
        private MutableLiveData<String> mailInputError = new MutableLiveData<>();
        private MutableLiveData<String> passwordInputError = new MutableLiveData<>();
        public User user;

    public void authenticateUser() {
        new Thread(() -> {
            if (userDBOperation.authenticateUser(user.getEmail(), user.getPwd())) {
                loginState.postValue(LoginState.AUTHENTICATION_SUCCESS);
            } else {
                loginState.postValue(LoginState.AUTHENTICATION_FAIL);
            }
        }).start();
    }

    public boolean checkMailInputOnEnterKey(int keyId) {
        Log.i("Login", "checkMailInputOnEnterKey");
        if (keyId == EditorInfo.IME_ACTION_NEXT || keyId == EditorInfo.IME_ACTION_DONE) {
            if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(user.getEmail()).matches())) {
                // this.loginState.setValue(LoginState.INVALID_EMAIL); // if later needed
                this.mailInputError.setValue("Input not valid, must be an e-mail address");
                return true;
            }
        }
        return false;
    }

    public boolean checkPasswordInputOnEnterKey(int keyId) {
        if (keyId == EditorInfo.IME_ACTION_NEXT || keyId == EditorInfo.IME_ACTION_DONE) {
            boolean pwdCheck = Pattern.matches("[0-9]{6}", user.getPwd());
            if (!(pwdCheck)) {
                // this.loginState.setValue(LoginState.WRONG_PASSWORD); // if later needed
                this.passwordInputError.setValue("Input not valid, must consist of 6 numbers");
                return true;
            }
        }
        return false;
    }

    public boolean onLoginInputChanged() {
        Log.i("Login", "onLoginInputChanged");
        this.loginState.setValue(null);
        return false;
    }

    public MutableLiveData<LoginState> getLoginState() {
        return loginState;
    }

    public MutableLiveData<String> getMailInputError() {
        return mailInputError;
    }

    public MutableLiveData<String> getPasswordInputError() {
        return passwordInputError;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserDBOperation(IUserDatabaseOperation userDBOperation) {
            this.userDBOperation = userDBOperation;
        }

    public enum LoginState {
        INVALID_EMAIL, WRONG_PASSWORD, AUTHENTICATION_SUCCESS, AUTHENTICATION_FAIL
    }
}
