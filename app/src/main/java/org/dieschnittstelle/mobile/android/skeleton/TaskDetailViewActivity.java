package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityTaskDetailViewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.Task;

public class TaskDetailViewActivity extends AppCompatActivity {
    protected static final String TASK_DETAIL_VIEW_KEY = "taskDetailViewObject";
    private Task task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        task = (Task) getIntent().getSerializableExtra(TASK_DETAIL_VIEW_KEY);
        if (task == null) {
            task = new Task();
        }

        ActivityTaskDetailViewBinding bind = DataBindingUtil.setContentView(this, R.layout.activity_task_detail_view);
        bind.setController(this);
    }

    public Task getTask() {
        return task;
    }

    public void saveTask() {
        // TODO: case when saving without name
//        if (taskNameEditText.getText().toString().isBlank()) {
//            Snackbar.make(findViewById(R.id.taskDetailViewActivity), "Cannot save: Mission name is missing", Snackbar.LENGTH_SHORT).show();
//            return;
//        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra(TASK_DETAIL_VIEW_KEY, task);

        this.setResult(TaskDetailViewActivity.RESULT_OK, returnIntent);
        this.finish();
    }
}
