package org.dieschnittstelle.mobile.android.skeleton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.model.Task;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected static final int REQUEST_CODE_DETAIL_VIEW_EDIT_CALL = 1;

    private TextView welcomeText;
    private ViewGroup taskListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        taskListView = findViewById(R.id.taskListView);

//        Arrays.asList("Aufgabe 1", "Aufgabe 2", "Aufgabe 3", "Aufgabe 4").forEach(taskName -> {
//            TextView taskView = (TextView) getLayoutInflater().inflate(R.layout.task_view, null);
//            taskView.setText(taskName);
//            taskView.setOnClickListener(view -> showTaskDetailView(taskName));
//
//            taskListView.addView(taskView);
//        });

        List<Task> taskList = List.of(
                new Task("Aufgabe 1", "Beschreibung 1", false),
                new Task("Aufgabe 2", "Beschreibung 2", false),
                new Task("Aufgabe 3", "Beschreibung 3", false),
                new Task("Aufgabe 4", "Beschreibung 4", false)
        );
        taskList.forEach(task -> {
            TextView taskView = (TextView) getLayoutInflater().inflate(R.layout.task_view, null);
            taskView.setText(task.getName());
            taskView.setOnClickListener(view -> showTaskDetailView(task));

            taskListView.addView(taskView);
        });

//        TextView welcomeText = findViewById(R.id.welcomeText);
//        welcomeText.setText(R.string.welcome_message_alternative);
//
//        welcomeText.setOnClickListener((view) ->
//                Snackbar.make(findViewById(R.id.rootView), R.string.welcome_message_reply, Snackbar.LENGTH_SHORT).show();
    }

    protected void showTaskDetailView(Task task) {
        Intent callTaskDetailViewIntent = new Intent(this, TaskDetailViewActivity.class);
        callTaskDetailViewIntent.putExtra(TaskDetailViewActivity.TASK_NAME_ID, task.getName());
//        startActivity(callTaskDetailViewIntent);
        startActivityForResult(callTaskDetailViewIntent, REQUEST_CODE_DETAIL_VIEW_EDIT_CALL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_DETAIL_VIEW_EDIT_CALL && resultCode == MainActivity.RESULT_OK) {
            String newTaskName = data.getStringExtra(TaskDetailViewActivity.TASK_NAME_ID);
            showMessage(String.format(getResources().getString(R.string.task_edited_feedback_message), newTaskName));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(R.id.rootView), message, Snackbar.LENGTH_SHORT).show();
    }
}
