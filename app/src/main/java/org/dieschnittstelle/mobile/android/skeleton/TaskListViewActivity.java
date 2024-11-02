package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskListViewActivity extends AppCompatActivity {

    protected static final int REQUEST_CODE_DETAIL_VIEW_EDIT_CALL = 1;
    protected static final int REQUEST_CODE_DETAIL_VIEW_ADD_CALL = 2;
//    private ViewGroup taskListView;
    private ListView taskListView;
    private List<Task> taskList = new ArrayList<>();
    private ArrayAdapter<Task> taskListViewAdapter;
    private FloatingActionButton addTaskAction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_view);
        taskListView = findViewById(R.id.taskListView);

        taskList.add(new Task("Aufgabe 1", "Beschreibung 1", false));
        taskList.add(new Task("Aufgabe 2", "Beschreibung 2", false));
        taskList.add(new Task("Aufgabe 3", "Beschreibung 3", false));
        taskList.add(new Task("Aufgabe 4", "Beschreibung 4", false));

//        taskListViewAdapter = new ArrayAdapter<>(this, R.layout.task_view, taskList);
//        taskListViewAdapter = new ArrayAdapter<>(this, R.layout.structured_task_view, R.id.taskName, taskList);
        taskListViewAdapter = new ArrayAdapter<>(this, R.layout.structured_task_view, taskList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                ViewGroup taskListView = (ViewGroup) getLayoutInflater().inflate(R.layout.structured_task_view, null);
                Task taskFromList = getItem(position);
                ((TextView) taskListView.findViewById(R.id.taskName)).setText(taskFromList.getName());
                ((CheckBox) taskListView.findViewById(R.id.taskCompleted)).setChecked(taskFromList.isCompleted());
                ((CheckBox) taskListView.findViewById(R.id.taskCompleted))
                        .setOnCheckedChangeListener(
                                (buttonView, isChecked) -> taskFromList.setCompleted(isChecked)
                        );

                return taskListView;
            }
        };
        taskListView.setAdapter(taskListViewAdapter);
        taskListView.setOnItemClickListener((parent, view, position, id) -> {
            Task selectedTask = taskListViewAdapter.getItem(position);
            showTaskDetailView(selectedTask);
        });

//        taskList.forEach(task -> {
//            TextView taskView = (TextView) getLayoutInflater().inflate(R.layout.task_view, null);
//            taskView.setText(task.getName());
//            taskView.setOnClickListener(view -> showTaskDetailView(task));
//
//            taskListView.addView(taskView);
//        });

        addTaskAction = findViewById(R.id.addTaskAction);
        addTaskAction.setOnClickListener(view -> this.showNewTaskDetailView());
    }

    protected void showTaskDetailView(Task task) {
        Intent callTaskDetailViewIntent = new Intent(this, TaskDetailViewActivity.class);
        callTaskDetailViewIntent.putExtra(TaskDetailViewActivity.TASK_DETAIL_VIEW_KEY, task);
        startActivityForResult(callTaskDetailViewIntent, REQUEST_CODE_DETAIL_VIEW_EDIT_CALL);
    }

    protected void showNewTaskDetailView() {
        Intent callTaskDetailViewIntent = new Intent(this, TaskDetailViewActivity.class);
        startActivityForResult(callTaskDetailViewIntent, REQUEST_CODE_DETAIL_VIEW_ADD_CALL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_DETAIL_VIEW_EDIT_CALL && resultCode == TaskDetailViewActivity.RESULT_OK) {
            Task taskFromDetailView = (Task) data.getSerializableExtra(TaskDetailViewActivity.TASK_DETAIL_VIEW_KEY);
            Task selectedTask = taskList.stream()
                    .filter(task -> task.getId().equals(taskFromDetailView.getId()))
                    .findAny()
                    .orElse(new Task());
            selectedTask.setName(taskFromDetailView.getName());
            selectedTask.setDescription(taskFromDetailView.getDescription());
            selectedTask.setCompleted(taskFromDetailView.isCompleted());
            // TODO: update DB
            taskListViewAdapter.notifyDataSetChanged();
            showMessage(getString(R.string.task_updated_feedback_message) + " " + taskFromDetailView.getName() + " description: " + taskFromDetailView.getDescription());
        }
        if (requestCode == REQUEST_CODE_DETAIL_VIEW_ADD_CALL && resultCode == TaskDetailViewActivity.RESULT_OK) {
            Task taskFromDetailView = (Task) data.getSerializableExtra(TaskDetailViewActivity.TASK_DETAIL_VIEW_KEY);
            taskList.add(taskFromDetailView);
            // TODO: update DB
            taskListViewAdapter.notifyDataSetChanged();
            showMessage(getString(R.string.task_added_feedback_message) + " " + taskFromDetailView.getName() + " description: " + taskFromDetailView.getDescription());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(R.id.taskListViewActivity), message, Snackbar.LENGTH_SHORT).show();
    }
}
