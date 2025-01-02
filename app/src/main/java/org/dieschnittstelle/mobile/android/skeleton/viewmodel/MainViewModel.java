package org.dieschnittstelle.mobile.android.skeleton.viewmodel;

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
    public User user;

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
        Log.i("Login", "onLoginInputChanged");
        this.mailInputError.setValue(null);
        return false;
    }

    public boolean onPasswordInputChanged() {
        Log.i("Login", "onLoginInputChanged");
        this.passwordInputError.setValue(null);
        return false;
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

    public void setTaskDBOperation(ITaskDatabaseOperation taskDBOperation) {
        this.taskDBOperation = taskDBOperation;
    }

    public void checkRemoteTaskDatabaseOperation() {
        new Thread(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("10.0.2.2", 8080), 2000);
                socket.close();
                Log.i("Login", "Remote database reachable...");
                databaseState.postValue(DatabaseState.CONNECT_REMOTE_SUCCESS);
            } catch (IOException e) {
                databaseState.postValue(DatabaseState.CONNECT_REMOTE_FAIL);
                Log.i("Login", "Remote database not reachable...");
                Log.e("Login", "checkRemoteTaskDatabaseOperation: " + e.toString());
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
