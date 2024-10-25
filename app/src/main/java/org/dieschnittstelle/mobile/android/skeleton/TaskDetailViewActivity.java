package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.dieschnittstelle.mobile.android.skeleton.model.Task;

public class TaskDetailViewActivity extends AppCompatActivity {
    protected static final String TASK_NAME_ID = "taskName";
    private EditText taskNameEditText;
    private FloatingActionButton saveTaskAction;
    private Task task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_detail_view);

        String taskName = getIntent().getStringExtra(TASK_NAME_ID);
        // task = (Task) getIntent().getSerializableExtra(TASK_NAME_ID)
        taskNameEditText = findViewById(R.id.taskName);
        taskNameEditText.setText(taskName);
        // taskNameEditText.setText(task.getName());

        saveTaskAction = findViewById(R.id.saveTaskAction);
        saveTaskAction.setOnClickListener(view -> {
            this.saveTask();
        });
    }

    protected void saveTask() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(TASK_NAME_ID, taskNameEditText.getText().toString());
        this.setResult(TaskDetailViewActivity.RESULT_OK, returnIntent);
        this.finish();
    }
}
