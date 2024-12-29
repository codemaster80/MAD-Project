package org.dieschnittstelle.mobile.android.skeleton.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import org.dieschnittstelle.mobile.android.skeleton.model.IUserDatabaseOperation;

public class MainViewModel extends ViewModel {
        private IUserDatabaseOperation userDBOperation;
        private MutableLiveData<String> loginLiveData = new MutableLiveData<>("initial");
        public String email;
        public String pwd;

    public MutableLiveData<String> getLoginLiveData() {
        return loginLiveData;
    }

    public void authenticateUser() {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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
