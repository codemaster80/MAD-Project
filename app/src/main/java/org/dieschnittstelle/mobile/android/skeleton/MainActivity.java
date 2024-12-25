package org.dieschnittstelle.mobile.android.skeleton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Button showTaskListAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText(R.string.welcome_message);
        welcomeText.setOnClickListener(view -> this.showTaskListView());

        showTaskListAction = findViewById(R.id.loginButton);
        showTaskListAction.setOnClickListener(view -> this.showTaskListView());
    }

    protected void showTaskListView() {
        Intent callTaskOverviewIntent = new Intent(this, TaskListViewActivity.class);
        startActivity(callTaskOverviewIntent);
        this.finish();
    }
}
