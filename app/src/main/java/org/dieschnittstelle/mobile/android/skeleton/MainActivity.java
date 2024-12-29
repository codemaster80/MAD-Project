package org.dieschnittstelle.mobile.android.skeleton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityMainBinding;
import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityTaskDetailViewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.IUserDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.User;
import org.dieschnittstelle.mobile.android.skeleton.viewmodel.MainViewModel;
import org.dieschnittstelle.mobile.android.skeleton.viewmodel.TaskListViewModel;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeText;
    private EditText emailAdress;
    private EditText password;
    private Button showTaskListAction;
    private MainViewModel viewModel;
    private IUserDatabaseOperation userDBOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        ActivityMainBinding MainViewBinding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_main
        );
        MainViewBinding.setMainViewModel(this.viewModel);
        MainViewBinding.setLifecycleOwner(this);

        userDBOperation = ((TaskApplication) getApplication()).getUserDatabaseOperation();
        viewModel.setUserDBOperation(userDBOperation);

        userDbInitialise(); // insert testdata

        welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText(R.string.welcome_message);

        emailAdress = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);

        // Login
        this.viewModel.getLoginLiveData().observe(this, onLogin -> {
            if (onLogin == "usernameIsNotAnEMail") {
                showPersistentMessage(getString(R.string.login_username_error));
            } else if (onLogin == "userAuthenticationIsFailed"){
                showPersistentMessage(getString(R.string.login_authentication_failed));
            } else if (onLogin == "passwordIsNotAllowed"){
                showPersistentMessage(getString(R.string.login_password_not_allowed));
            } else if (onLogin == "userIsAuthenticated") {
                Intent callTaskOverviewIntent = new Intent(this, TaskListViewActivity.class);
                startActivity(callTaskOverviewIntent);
                this.finish();
            }
        });
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(R.id.rootView), message, Snackbar.LENGTH_SHORT).show();
                Snackbar.make(findViewById(R.id.rootView), message, Snackbar.LENGTH_INDEFINITE).show();
    }

    private void showPersistentMessage(String message) {
        Snackbar.make(findViewById(R.id.rootView), message, Snackbar.LENGTH_INDEFINITE).show();
    }


    private void userDbInitialise() {
        new Thread(() -> {
            if (!(userDBOperation.authenticateUser("s@bht.de", "000000"))) {
                userDBOperation.createUser(new User("s@bht.de","000000"));
            }
        }).start();
    }
}
