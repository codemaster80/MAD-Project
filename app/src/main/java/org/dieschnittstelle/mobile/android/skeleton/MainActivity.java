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

        // testuser
        User mockUser1 = new User("a@b.de", "test");

        new Thread(() -> {
            //Log.i("Login", String.valueOf(userDBOperation.authenticateUser("a@b.de", "test")));
            //userDBOperation.deleteAllUsers();
            //userDBOperation.createUser(mockUser1);
            //userDBOperation.deleteUser(2);
        }).start();

        welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText(R.string.welcome_message);

        emailAdress = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);

        // Authentication
        this.viewModel.isUsernameNotAnEMail().observe(this, onUsernameNotAnEMail -> {
            if (onUsernameNotAnEMail) {
                showMessage(getString(R.string.login_username_error));
            }
        });

        this.viewModel.isUserAuthenticated().observe(this, onAuthenticated -> {
            if (onAuthenticated) {
                // Log.i("Login", "onAuthenticated");
                Intent callTaskOverviewIntent = new Intent(this, TaskListViewActivity.class);
                startActivity(callTaskOverviewIntent);
                this.finish();
            }
        });
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(R.id.rootView), message, Snackbar.LENGTH_SHORT).show();
    }
}
