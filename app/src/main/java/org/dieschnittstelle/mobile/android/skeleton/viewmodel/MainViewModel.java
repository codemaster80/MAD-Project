package org.dieschnittstelle.mobile.android.skeleton.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.dieschnittstelle.mobile.android.skeleton.model.ITaskDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.User;

import java.util.regex.Pattern;

public class MainViewModel extends ViewModel {
    private ITaskDatabaseOperation taskDBOperation;
    private final MutableLiveData<LoginState> loginState = new MutableLiveData<>();
    private MutableLiveData<String> mailInputError = new MutableLiveData<>();
    private MutableLiveData<String> passwordInputError = new MutableLiveData<>();
    public User user;

    public void authenticateUser() {
        if ((checkMailInput() == true) && (checkPasswordInput() == true)) {
            Log.i("Login", "auth");
            new Thread(() -> {
                if (taskDBOperation.authenticateUser(user)) {
                    loginState.postValue(LoginState.AUTHENTICATION_SUCCESS);
                } else {
                    loginState.postValue(LoginState.AUTHENTICATION_FAIL);
                }
            }).start();
        }
    }

    public boolean checkMailInput() {
        Log.i("Login", "checkMailInputOnEnterKey");
        if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(user.getEmail()).matches())) {
            this.mailInputError.setValue("Input not valid, must be an e-mail address");
            return false;
        } else {
            return true;
        }
    }

    public boolean checkPasswordInput() {
        boolean pwdCheck = Pattern.matches("[0-9]{6}", user.getPwd());
        if (!(pwdCheck)) {
            this.passwordInputError.setValue("Input not valid, must consist of 6 numbers");
            return false;
        } else {
            return true;
        }
    }

    public boolean onMailInputChanged() {
        Log.i("Login", "onLoginInputChanged");
        this.mailInputError.setValue(null);
        return false;
    }

    public boolean onPasswordInputChanged() {
        Log.i("Login", "onLoginInputChanged");
        this.passwordInputError.setValue(null);
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

    public void setTaskDBOperation(ITaskDatabaseOperation taskDBOperation) {
        this.taskDBOperation = taskDBOperation;
    }

    public enum LoginState {
        INVALID_EMAIL, WRONG_PASSWORD, AUTHENTICATION_SUCCESS, AUTHENTICATION_FAIL
    }
}
