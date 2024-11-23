package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityTaskDetailViewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.Task;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TaskDetailViewActivity extends AppCompatActivity {
    protected static final String TASK_DETAIL_VIEW_KEY = "taskDetailViewObject";
    private Task task;
    private Spinner taskPrioDropDown;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        task = (Task) getIntent().getSerializableExtra(TASK_DETAIL_VIEW_KEY);
        if (task == null) {
            task = new Task();
        }
        ActivityTaskDetailViewBinding taskDetailViewBinding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_task_detail_view
        );
        taskDetailViewBinding.setController(this);

        taskPrioDropDown = findViewById(R.id.dropdownPriority);
        setPriorityDropDown();
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

    private void setPriorityDropDown() {
        String currentPrio = task.getPriority();
        List<String> priorities = Arrays.asList("NONE", "LOW", "NORMAL", "HIGH", "CRITICAL");
        if (!currentPrio.isBlank()) {
            priorities.set(0, currentPrio);
            priorities = priorities.stream().distinct().collect(Collectors.toList());
        }
        ArrayAdapter<String> dropdownPriorityAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item, priorities);
        dropdownPriorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskPrioDropDown.setAdapter(dropdownPriorityAdapter);

        taskPrioDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                String selectedPrio = taskPrioDropDown.getSelectedItem().toString();
                task.setPriority(selectedPrio);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }
}
