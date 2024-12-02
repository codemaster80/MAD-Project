package org.dieschnittstelle.mobile.android.skeleton;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityTaskDetailViewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.Task;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class TaskDetailViewActivity extends AppCompatActivity {
    protected static final String TASK_DETAIL_VIEW_KEY = "taskDetailViewObject";
    private Task task;
    private Spinner taskPrioritySpinner;
    TextView taskDateTextView;
    private Button pickDateBtn;

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

        pickDateBtn = findViewById(R.id.btnPickDueDate);
        taskDateTextView = findViewById(R.id.taskDate);
        setDueDate();

        taskPrioritySpinner = findViewById(R.id.dropdownPriority);
        setPriorityDropDown();
    }

    public Task getTask() {
        return task;
    }

    public void saveTask() {
        if (task.getName() == null || task.getName().isBlank()) {
            Snackbar.make(
                    findViewById(R.id.taskDetailViewActivity),
                    "Cannot save: Mission name is missing",
                    Snackbar.LENGTH_SHORT
            ).show();
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra(TASK_DETAIL_VIEW_KEY, task);

        this.setResult(TaskDetailViewActivity.RESULT_OK, returnIntent);
        this.finish();
    }

    private void setDueDate() {
        pickDateBtn.setOnClickListener(view -> {
            Calendar calendar;
            int currentYear, currentMonth, currentDay;
            DatePickerDialog datePickerDialog;

            if (task.getDate() == null || task.getDate().isBlank()) {
                calendar = Calendar.getInstance();
                currentYear = calendar.get(Calendar.YEAR);
                currentMonth = calendar.get(Calendar.MONTH);
                currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = setDatePickerDialog(currentYear, currentMonth, currentDay);
            } else {
                String currentDueDateStr = task.getDate();
                currentDay = Integer.parseInt(currentDueDateStr.split("\\.")[0]);
                // Month of Calendar starts from 0
                currentMonth = Integer.parseInt(currentDueDateStr.split("\\.")[1]) - 1;
                currentYear = Integer.parseInt(currentDueDateStr.split("\\.")[2]);
                datePickerDialog = setDatePickerDialog(currentYear, currentMonth, currentDay);
            }
            datePickerDialog.show();
        });
    }

    @NonNull
    private DatePickerDialog setDatePickerDialog(int currentYear, int currentMonth, int currentDay) {
        return new DatePickerDialog(
                this,
                (datePickerView, selectedYear, selectedMonth, selectedDay) -> {
                    // Set the selected date to the current task
                    // Month of Calendar starts from 0
                    String dueDateStr = selectedDay + "." + (selectedMonth + 1) + "." + selectedYear;
                    task.setDate(dueDateStr);
                    pickDateBtn.setText(dueDateStr);
                },
                currentYear,
                currentMonth,
                currentDay
        );
    }

    private void setPriorityDropDown() {
        Task.Priority currentPriority = task.getPriority();
        List<String> priorities = Arrays.asList(
                Task.Priority.NONE.name(),
                Task.Priority.LOW.name(),
                Task.Priority.NORMAL.name(),
                Task.Priority.HIGH.name(),
                Task.Priority.CRITICAL.name()
        );
        if (!currentPriority.equals(Task.Priority.NONE)) {
            priorities.set(0, currentPriority.name());
            priorities = priorities.stream().distinct().collect(Collectors.toList());
        }
        ArrayAdapter<String> dropDownPriorityAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item, priorities);
        dropDownPriorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskPrioritySpinner.setAdapter(dropDownPriorityAdapter);

        taskPrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                String selectedPriority = taskPrioritySpinner.getSelectedItem().toString();
                task.setPriority(Task.Priority.valueOf(selectedPriority));
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }
}