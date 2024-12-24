package org.dieschnittstelle.mobile.android.skeleton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.dieschnittstelle.mobile.android.skeleton.model.AppContainer;
import org.dieschnittstelle.mobile.android.skeleton.viewmodel.LoginViewModel;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeText;
    private FloatingActionButton showTaskListAction;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Gets userRepository from the instance of AppContainer in Application
        AppContainer appContainer = ((TaskApplication) getApplication()).appContainer;
        loginViewModel = new LoginViewModel(appContainer.userRepository);

        welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText(R.string.welcome_message);
        welcomeText.setOnClickListener(view -> this.showTaskListView());

        showTaskListAction = findViewById(R.id.showTaskListAction);
        showTaskListAction.setOnClickListener(view -> this.showTaskListView());
    }

    protected void showTaskListView() {
        Intent callTaskOverviewIntent = new Intent(this, TaskListViewActivity.class);
        startActivity(callTaskOverviewIntent);
        this.finish();
    }
}
