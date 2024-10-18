package org.dieschnittstelle.mobile.android.skeleton;

import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailViewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_detail_view);

        String taskName = getIntent().getStringExtra("taskName");
        EditText taskNameEditText = findViewById(R.id.taskName);
        taskNameEditText.setText(taskName);
    }
}
