package org.dieschnittstelle.mobile.android.skeleton.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import org.dieschnittstelle.mobile.android.skeleton.model.ITaskDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.User;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Pattern;

public class MainViewModel extends ViewModel {
    private ITaskDatabaseOperation taskDBOperation;
    private final MutableLiveData<DatabaseState> databaseState = new MutableLiveData<>();
    private final MutableLiveData<LoginState> loginState = new MutableLiveData<>();
    private MutableLiveData<String> mailInputError = new MutableLiveData<>();
    private MutableLiveData<String> passwordInputError = new MutableLiveData<>();
    private User user;

    public void authenticateUser() {
        if ((checkMailInput() == true) && (checkPasswordInput() == true)) {
           loginState.setValue(LoginState.RUNNING);
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
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
        Handler emailAdressValidationHandler = new Handler(Looper.getMainLooper());
        getMailInputError().setValue(null);
        emailAdressValidationHandler.removeCallbacksAndMessages(null); // Remove pending validations
        emailAdressValidationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkMailInput()) {
                    mailInputError.setValue(null); // Clear any previous error
                } else {
                    mailInputError.setValue("Input not valid, must be an e-mail address");
                }
            }
        }, 2000);
        return true;
    }

    public boolean onPasswordInputChanged() {
        Handler passwordAdressValidationHandler = new Handler(Looper.getMainLooper());
        getPasswordInputError().setValue(null);
        passwordAdressValidationHandler.removeCallbacksAndMessages(null); // Remove pending validations
        passwordAdressValidationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkPasswordInput()) {
                    passwordInputError.setValue(null); // Clear any previous error
                } else {
                    passwordInputError.setValue("Input not valid, must consist of 6 numbers");
                }
            }
        }, 2000);
        return true;
    }

    public MutableLiveData<DatabaseState> getDatabaseState() {
        return databaseState;
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

    public User getUser() {
        return user;
    }

    public void setTaskDBOperation(ITaskDatabaseOperation taskDBOperation) {
        this.taskDBOperation = taskDBOperation;
    }

    public void checkRemoteTaskDatabaseOperation() {
        new Thread(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("10.0.2.2", 8080), 500);
                socket.close();
                databaseState.postValue(DatabaseState.CONNECT_REMOTE_SUCCESS);
            } catch (IOException e) {
                databaseState.postValue(DatabaseState.CONNECT_REMOTE_FAIL);
            }
        }).start();
    }

    public enum DatabaseState {
        CONNECT_REMOTE_SUCCESS, CONNECT_REMOTE_FAIL
    }

    public enum LoginState {
        AUTHENTICATION_SUCCESS, AUTHENTICATION_FAIL, RUNNING
    }
}
