package org.dieschnittstelle.mobile.android.skeleton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeText;
    private ViewGroup taskListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        taskListView = findViewById(R.id.taskListView);

        Arrays.asList("Aufgabe 1", "Aufgabe 2", "Aufgabe 3", "Aufgabe 4").forEach(taskName -> {
            TextView taskView = (TextView) getLayoutInflater().inflate(R.layout.task_view, null);
            taskView.setText(taskName);
            taskView.setOnClickListener(view -> showTaskDetailView(taskName));

            taskListView.addView(taskView);
        });

//        TextView welcomeText = findViewById(R.id.welcomeText);
//        welcomeText.setText(R.string.welcome_message_alternative);
//
//        welcomeText.setOnClickListener((view) ->
//                Snackbar.make(findViewById(R.id.rootView), R.string.welcome_message_reply, Snackbar.LENGTH_SHORT).show();
    }

    protected void showTaskDetailView(String taskName) {
        Intent callTaskDetailViewIntent = new Intent(this, TaskDetailViewActivity.class);
        callTaskDetailViewIntent.putExtra("taskName", taskName);
        startActivity(callTaskDetailViewIntent);
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(R.id.rootView), message, Snackbar.LENGTH_SHORT).show();
    }
}
