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

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityMainBinding;
import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityTaskDetailViewBinding;
import org.dieschnittstelle.mobile.android.skeleton.viewmodel.MainViewModel;
import org.dieschnittstelle.mobile.android.skeleton.viewmodel.TaskListViewModel;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeText;
    private EditText emailAdress;
    private EditText password;
    private Button showTaskListAction;
    private MainViewModel viewModel;

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

        welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText(R.string.welcome_message);

        emailAdress = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);

        this.viewModel.isUserAuthenticated().observe(this, onAuthenticated -> {
            if (onAuthenticated) {
                // Log.i("Login", "onAuthenticated");
                Intent callTaskOverviewIntent = new Intent(this, TaskListViewActivity.class);
                startActivity(callTaskOverviewIntent);
                this.finish();
            }
        });
    }
}
