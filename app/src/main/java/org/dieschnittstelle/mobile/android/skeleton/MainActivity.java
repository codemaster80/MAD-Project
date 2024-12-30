package org.dieschnittstelle.mobile.android.skeleton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityMainBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.IUserDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.User;
import org.dieschnittstelle.mobile.android.skeleton.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeText;
    private TextInputLayout emailAdressLayout;
    private TextInputLayout passwordLayout;
    private EditText emailAdress;
    private EditText password;
    private Button showTaskListAction;
    private MainViewModel viewModel;
    private IUserDatabaseOperation userDBOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        User user = new User("", "");
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        ActivityMainBinding MainViewBinding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_main
        );
        MainViewBinding.setMainViewModel(this.viewModel);
        MainViewBinding.setLifecycleOwner(this);

        userDBOperation = ((TaskApplication) getApplication()).getUserDatabaseOperation();
        viewModel.setUserDBOperation(userDBOperation);

        viewModel.setUser(user);

        userDbInitialise(); // insert testdata

        welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText(R.string.welcome_message);

        emailAdressLayout = findViewById(R.id.editTextEmailAddress);
        emailAdress = emailAdressLayout.getEditText();
        passwordLayout = findViewById(R.id.editTextPassword);
        password = passwordLayout.getEditText();

        this.viewModel.getLoginState().observe(this, this::handleUserLoginState);
    }

    private void handleUserLoginState(MainViewModel.LoginState loginState) {
        if (loginState != null) {
            switch (loginState) {
                case INVALID_EMAIL:
                    showMessage(getString(R.string.login_username_error));
                    break;
                case WRONG_PASSWORD:
                    showMessage(getString(R.string.login_password_not_allowed));
                    break;
                case AUTHENTICATION_FAIL:
                    showPersistentMessage(getString(R.string.login_authentication_failed));
                    break;
                case AUTHENTICATION_SUCCESS:
                    Intent callTaskListViewIntent = new Intent(this, TaskListViewActivity.class);
                    startActivity(callTaskListViewIntent);
                    this.finish();
                    break;
                default:
                    break;
            }
        }
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(R.id.rootView), message, Snackbar.LENGTH_SHORT).show();
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
