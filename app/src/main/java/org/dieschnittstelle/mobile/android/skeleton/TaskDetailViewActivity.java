package org.dieschnittstelle.mobile.android.skeleton;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import androidx.lifecycle.ViewModelProvider;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityTaskDetailViewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.Task;
import org.dieschnittstelle.mobile.android.skeleton.viewmodel.TaskDetailViewModel;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class TaskDetailViewActivity extends AppCompatActivity {
    protected static final String TASK_DETAIL_VIEW_KEY = "taskDetailViewObject";
    protected static final int RESULT_DELETE_OK = 99;
    private Task task;
    private TaskDetailViewModel viewModel;
    private Spinner taskPrioritySpinner;
    TextView taskDateTextView;
    private Button pickDateBtn;
    TextView taskTimeTextView;
    private Button pickTimeBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.viewModel = new ViewModelProvider(this).get(TaskDetailViewModel.class);
        if (viewModel.getTask() == null) {
            task = (Task) getIntent().getSerializableExtra(TASK_DETAIL_VIEW_KEY);
            if (task == null) {
                task = new Task();
            }
            this.viewModel.setTask(task);
        }

        ActivityTaskDetailViewBinding taskDetailViewBinding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_task_detail_view
        );
        taskDetailViewBinding.setTaskDetailViewModel(this.viewModel);
        taskDetailViewBinding.setLifecycleOwner(this);

        pickDateBtn = findViewById(R.id.btnPickDueDate);
        taskDateTextView = findViewById(R.id.taskDate);
        setDueDate();

        pickTimeBtn = findViewById(R.id.btnPickTime);
        taskTimeTextView = findViewById(R.id.taskTime);
        setTimeLimit();

        taskPrioritySpinner = findViewById(R.id.dropdownPriority);
        setPriorityDropDown();

        this.viewModel.isTaskOnSave().observe(this, onSave -> {
            if (onSave) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(TASK_DETAIL_VIEW_KEY, task);
                this.setResult(TaskDetailViewActivity.RESULT_OK, returnIntent);
                this.finish();
            }
        });

        this.viewModel.isTaskOnDelete().observe(this, onDelete -> {
            if (onDelete) {
                deleteAlertDialog();
            }
        });
    }

    private void deleteAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(android.R.layout.select_dialog_item);
        builder.setMessage("Delete Mission \"" + task.getName() + "\"?");
        builder.setPositiveButton("Delete", (dialog, id) -> deleteIntent());
        builder.setNegativeButton("Cancel", (dialog, id) -> {
           // do nothing
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteIntent() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(TASK_DETAIL_VIEW_KEY, task);
        this.setResult(TaskDetailViewActivity.RESULT_DELETE_OK, returnIntent);
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
            } else {
                String currentDueDateStr = task.getDate();
                currentDay = Integer.parseInt(currentDueDateStr.split("\\.")[0]);
                // Month of Calendar starts from 0
                currentMonth = Integer.parseInt(currentDueDateStr.split("\\.")[1]) - 1;
                currentYear = Integer.parseInt(currentDueDateStr.split("\\.")[2]);
            }
            datePickerDialog = setDatePickerDialog(currentYear, currentMonth, currentDay);
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

    private void setTimeLimit() {
        pickTimeBtn.setOnClickListener(view -> {
            Calendar calendar;
            int currentHour, currentMinute;
            TimePickerDialog timePickerDialog;

            if (task.getTime() == null || task.getTime().isBlank()) {
                calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar.get(Calendar.MINUTE);
            } else {
                String currentTimeLimitStr = task.getTime();
                currentHour = Integer.parseInt(currentTimeLimitStr.split(":")[0]);
                currentMinute = Integer.parseInt(currentTimeLimitStr.split(":")[1]);
            }
            timePickerDialog = setTimePickerDialog(currentHour, currentMinute);
            timePickerDialog.show();
        });
    }

    private TimePickerDialog setTimePickerDialog(int currentHour, int currentMinute) {
        return new TimePickerDialog(
                this,
                (timePickerView, selectedHour, selectedMinute) -> {
                    String selectedHourStr = String.valueOf(selectedHour);
                    String selectedMinuteStr = String.valueOf(selectedMinute);
                    // convert single digit time to double digit. e.g.: 9h 0m -> 09:00
                    if (selectedHour < 10) {
                        selectedHourStr = "0" + selectedHour;
                    }
                    if (selectedMinute < 10) {
                        selectedMinuteStr = "0" + selectedMinute;
                    }
                    String timeLimitStr = selectedHourStr + ":" + selectedMinuteStr;
                    task.setTime(timeLimitStr);
                    pickTimeBtn.setText(timeLimitStr);
                },
                currentHour,
                currentMinute,
                true
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