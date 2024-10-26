package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.dieschnittstelle.mobile.android.skeleton.model.Task;

public class TaskDetailViewActivity extends AppCompatActivity {
    protected static final String TASK_NAME_VIEW_ID = "taskName";
    protected static final String TASK_DESCRIPTION_VIEW_ID  = "taskDescription";
    private EditText taskNameEditText;
    private EditText taskDescriptionEditText;
    private FloatingActionButton updateTaskAction;
    private Task task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail_view);

        String taskName = getIntent().getStringExtra(TASK_NAME_VIEW_ID);
        // task = (Task) getIntent().getSerializableExtra(TASK_NAME_ID)
        taskNameEditText = findViewById(R.id.taskName);
        taskNameEditText.setText(taskName);
        // taskNameEditText.setText(task.getName());

        String taskDescription = getIntent().getStringExtra(TASK_DESCRIPTION_VIEW_ID);
        taskDescriptionEditText = findViewById(R.id.taskDescription);
        taskDescriptionEditText.setText(taskDescription);

        updateTaskAction = findViewById(R.id.updateTaskAction);
        updateTaskAction.setOnClickListener(view -> this.saveTask());
    }

    private void saveTask() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(TASK_NAME_VIEW_ID, taskNameEditText.getText().toString());
        returnIntent.putExtra(TASK_DESCRIPTION_VIEW_ID, taskDescriptionEditText.getText().toString());
        // TODO: Save task to DB
        // TODO: Update list view with new task
        this.setResult(TaskDetailViewActivity.RESULT_OK, returnIntent);
        this.finish();
    }
}
