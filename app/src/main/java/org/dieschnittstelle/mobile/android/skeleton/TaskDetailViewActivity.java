package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.model.Task;

public class TaskDetailViewActivity extends AppCompatActivity {
    protected static final String TASK_DETAIL_VIEW_KEY = "taskDetailViewObject";
    private EditText taskNameEditText;
    private EditText taskDescriptionEditText;
    private CheckBox taskCompletedCheckBox;
    private FloatingActionButton updateTaskAction;
    private Task task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail_view);

        task = (Task) getIntent().getSerializableExtra(TASK_DETAIL_VIEW_KEY);
        if (task == null) {
            task = new Task();
        }

        taskNameEditText = findViewById(R.id.taskName);
        taskNameEditText.setText(task.getName());

        taskDescriptionEditText = findViewById(R.id.taskDescription);
        taskDescriptionEditText.setText(task.getDescription());

        taskCompletedCheckBox = findViewById(R.id.taskCompleted);
        taskCompletedCheckBox.setChecked(task.isCompleted());

        updateTaskAction = findViewById(R.id.updateTaskAction);
        updateTaskAction.setOnClickListener(view -> this.saveTask());
    }

    private void saveTask() {
        if (taskNameEditText.getText().toString().isBlank()) {
            Snackbar.make(findViewById(R.id.taskDetailViewActivity), "Cannot save: Mission name is missing", Snackbar.LENGTH_SHORT).show();
            return;
        }
        Intent returnIntent = new Intent();
        task.setName(taskNameEditText.getText().toString());
        task.setDescription(taskDescriptionEditText.getText().toString());
        task.setCompleted(taskCompletedCheckBox.isChecked());
        returnIntent.putExtra(TASK_DETAIL_VIEW_KEY, task);


        // update taskListView
//        String newTaskDescription = data.getStringExtra(TaskDetailViewActivity.TASK_DESCRIPTION_VIEW_ID);
//        boolean isCompleted = data.getBooleanExtra(TaskDetailViewActivity.TASK_IS_COMPLETED_BUTTON_ID, false);
//        int taskPosition = taskList.indexOf(newTaskName);
//        Task existingTask = taskList.get(taskPosition);
//        Task newTask = new Task(newTaskName, newTaskDescription, isCompleted);
//        taskList.add(newTask);
//        taskListViewAdapter.notifyDataSetChanged();
        this.setResult(TaskDetailViewActivity.RESULT_OK, returnIntent);
        this.finish();
    }
}
