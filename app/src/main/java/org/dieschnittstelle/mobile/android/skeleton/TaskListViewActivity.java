package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.model.Task;

import java.util.List;

public class TaskListViewActivity extends AppCompatActivity {

    protected static final int REQUEST_CODE_DETAIL_VIEW_EDIT_CALL = 1;
    private ViewGroup taskListView;
    private FloatingActionButton addTaskAction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_view);
        taskListView = findViewById(R.id.taskListView);

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

        addTaskAction = findViewById(R.id.addTaskAction);
        addTaskAction.setOnClickListener(view -> this.showNewTaskDetailView());
    }

    protected void showTaskDetailView(Task task) {
        Intent callTaskDetailViewIntent = new Intent(this, TaskDetailViewActivity.class);
        callTaskDetailViewIntent.putExtra(TaskDetailViewActivity.TASK_NAME_VIEW_ID, task.getName());
        if (!task.getDescription().isBlank()) {
            callTaskDetailViewIntent.putExtra(TaskDetailViewActivity.TASK_DESCRIPTION_VIEW_ID, task.getDescription());
        }
        startActivityForResult(callTaskDetailViewIntent, REQUEST_CODE_DETAIL_VIEW_EDIT_CALL);
    }

    protected void showNewTaskDetailView() {
        Intent callTaskDetailViewIntent = new Intent(this, TaskDetailViewActivity.class);
        startActivityForResult(callTaskDetailViewIntent, REQUEST_CODE_DETAIL_VIEW_EDIT_CALL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_DETAIL_VIEW_EDIT_CALL && resultCode == MainActivity.RESULT_OK) {
            String newTaskName = data.getStringExtra(TaskDetailViewActivity.TASK_NAME_VIEW_ID);
            showMessage(getString(R.string.task_updated_feedback_message) + " " + newTaskName);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(R.id.taskListActivityView), message, Snackbar.LENGTH_SHORT).show();
    }
}
