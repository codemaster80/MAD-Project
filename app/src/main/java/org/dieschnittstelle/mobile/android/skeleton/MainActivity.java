package org.dieschnittstelle.mobile.android.skeleton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityMainBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.ITaskDatabaseOperation;
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
    private ITaskDatabaseOperation taskDBOperation;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        this.viewModel.getDatabaseState().observe(this, this::handleDatabaseState);
        this.viewModel.getLoginState().observe(this, this::handleUserLoginState);
        this.viewModel.checkRemoteTaskDatabaseOperation();
    }

    private void handleDatabaseState(MainViewModel.DatabaseState databaseState) {
        if (databaseState != null) {
            switch (databaseState) {
                case CONNECT_REMOTE_FAIL:
                    callTaskListViewIntent();
                    break;
                case CONNECT_REMOTE_SUCCESS:
                    User user = new User("", "");
                    ActivityMainBinding MainViewBinding = DataBindingUtil.setContentView(
                            this,
                            R.layout.activity_main
                    );
                    MainViewBinding.setMainViewModel(this.viewModel);
                    MainViewBinding.setLifecycleOwner(this);

                    taskDBOperation = ((TaskApplication) getApplication()).getTaskDatabaseOperation();
                    viewModel.setTaskDBOperation(taskDBOperation);

                    viewModel.setUser(user);

                    welcomeText = findViewById(R.id.welcomeText);
                    welcomeText.setText(R.string.welcome_message);

                    emailAdressLayout = findViewById(R.id.editTextEmailAddress);
                    emailAdress = emailAdressLayout.getEditText();
                    passwordLayout = findViewById(R.id.editTextPassword);
                    password = passwordLayout.getEditText();

                    progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.GONE);

                    emailAdressValidation();
                    passwordValidation();

                    break;
            }
        }
    }

    private void emailAdressValidation() {
        emailAdress.addTextChangedListener(new TextWatcher() {
            private Handler emailAdressValidationHandler = new Handler(Looper.getMainLooper());

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.getMailInputError().setValue(null);
                emailAdressValidationHandler.removeCallbacksAndMessages(null); // Remove pending validations
                emailAdressValidationHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (viewModel.checkMailInput()) {
                            viewModel.getMailInputError().setValue(null); // Clear any previous error
                        } else {
                            viewModel.getMailInputError().setValue("Input not valid, must be an e-mail address");
                        }
                    }
                }, 2000);
            }
        });
    }

    private void passwordValidation() {
        password.addTextChangedListener(new TextWatcher() {
            private Handler passwordAdressValidationHandler = new Handler(Looper.getMainLooper());

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.getPasswordInputError().setValue(null);
                passwordAdressValidationHandler.removeCallbacksAndMessages(null); // Remove pending validations
                passwordAdressValidationHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (viewModel.checkPasswordInput()) {
                            viewModel.getPasswordInputError().setValue(null); // Clear any previous error
                        } else {
                            viewModel.getPasswordInputError().setValue("Input not valid, must consist of 6 numbers");
                        }
                    }
                }, 2000);
            }
        });
    }

    private void handleUserLoginState(MainViewModel.LoginState loginState) {
        if (loginState != null) {
            switch (loginState) {
                case RUNNING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case AUTHENTICATION_FAIL:
                    progressBar.setVisibility(View.GONE);
                    showPersistentMessage(getString(R.string.login_authentication_failed));
                    break;
                case AUTHENTICATION_SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    callTaskListViewIntent();
                    break;
                default:
                    break;
            }
        }
    }

    private void callTaskListViewIntent() {
        Intent callTaskListViewIntent = new Intent(this, TaskListViewActivity.class);
        startActivity(callTaskListViewIntent);
        this.finish();
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(R.id.rootView), message, Snackbar.LENGTH_SHORT).show();
    }

    private void showPersistentMessage(String message) {
        Snackbar.make(findViewById(R.id.rootView), message, Snackbar.LENGTH_INDEFINITE).show();
    }
}
