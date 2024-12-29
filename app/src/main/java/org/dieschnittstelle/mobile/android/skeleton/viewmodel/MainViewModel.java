package org.dieschnittstelle.mobile.android.skeleton.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import org.dieschnittstelle.mobile.android.skeleton.model.IUserDatabaseOperation;

public class MainViewModel extends ViewModel {
        private IUserDatabaseOperation userDBOperation;
        private MutableLiveData<Boolean> isUserAuthenticated = new MutableLiveData<>(false);
        private MutableLiveData<Boolean> isUsernameNotAnEMail = new MutableLiveData<>(false);
        private MutableLiveData<Boolean> isUserAuthenticationFailed = new MutableLiveData<>(false);
        public String email;
        public String pwd;

        public void authenticateUser() {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                new Thread(() -> {
                    if (userDBOperation.authenticateUser(email, pwd)) {
                        isUserAuthenticated.postValue(true);
                    } else {
                        isUserAuthenticationFailed.postValue(true);
                    }
                }).start();
            } else {
                isUsernameNotAnEMail.setValue(true);
            }
        }

        public MutableLiveData<Boolean> isUserAuthenticated() {
            return isUserAuthenticated;
        }

        public MutableLiveData<Boolean> isUsernameNotAnEMail() {
            return isUsernameNotAnEMail;
        }

        public MutableLiveData<Boolean> isUserAuthenticationFailed() {
            return isUserAuthenticationFailed;
        }

        public void setUserDBOperation(IUserDatabaseOperation userDBOperation) {
            this.userDBOperation = userDBOperation;
        }
}
