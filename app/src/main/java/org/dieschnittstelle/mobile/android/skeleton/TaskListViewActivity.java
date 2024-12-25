package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.databinding.StructuredTaskViewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.Task;
import org.dieschnittstelle.mobile.android.skeleton.viewmodel.TaskListViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TaskListViewActivity extends AppCompatActivity {
    private ListView taskListView;
    private ArrayAdapter<Task> taskListViewAdapter;
    private FloatingActionButton addTaskAction;
    private ProgressBar progressBar;
    private TaskListViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_view);

        viewModel = new ViewModelProvider(this).get(TaskListViewModel.class);
        viewModel.setContext(this.getApplicationContext());
        viewModel.setTaskDbOperation(((TaskApplication) getApplication()).getTaskDatabaseOperation());
        viewModel.getProcessingState().observe(this, this::handleTaskProcessingState);
        viewModel.initializeDb();
        viewModel.synchronizeDb();

        taskListViewAdapter = new TaskListAdapter(this, R.layout.structured_task_view, viewModel.getTaskList());
        taskListView = findViewById(R.id.taskListView);
        taskListView.setAdapter(taskListViewAdapter);

        addTaskAction = findViewById(R.id.addTaskAction);
        addTaskAction.setOnClickListener(view -> this.showNewTaskDetailView());

        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_overview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sortTasks) {
            showMessage("Sorting all done missions...");
            this.viewModel.sortTasksByCompletedAndName();
            return true;
        }

        if (item.getItemId() == R.id.sortTasksByPrio) {
            showMessage("Sorting all missions by priority...");
            this.viewModel.sortTasksByPrioAndDate();
            return true;
        }

        if (item.getItemId() == R.id.deleteAllLocalTasks) {
            showMessage("Deleting all missions from local database...");
            this.viewModel.deleteAllTasksFromLocal();
            return true;
        }

        if (item.getItemId() == R.id.deleteAllRemoteTasks) {
            showMessage("Deleting all missions from remote database...");
            this.viewModel.deleteAllTasksFromRemote();
            return true;
        }

        if (item.getItemId() == R.id.syncLocalRemoteDB) {
            showMessage("Syncing all missions between database...");
            viewModel.synchronizeDb();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showEditTaskDetailView(Task task) {
        Intent callTaskDetailViewIntent = new Intent(this, TaskDetailViewActivity.class);
        callTaskDetailViewIntent.putExtra(TaskDetailViewActivity.TASK_DETAIL_VIEW_KEY, task);
        taskDetailViewForEditLauncher.launch(callTaskDetailViewIntent);
    }

    private void showNewTaskDetailView() {
        Intent callTaskDetailViewIntent = new Intent(this, TaskDetailViewActivity.class);
        taskDetailViewForAddLauncher.launch(callTaskDetailViewIntent);
    }

    private final ActivityResultLauncher<Intent> taskDetailViewForEditLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            activityResult -> {
                if (activityResult.getResultCode() == TaskDetailViewActivity.RESULT_OK) {
                    Task taskFromDetailView = (Task) activityResult.getData().getSerializableExtra(TaskDetailViewActivity.TASK_DETAIL_VIEW_KEY);
                    viewModel.updateTask(taskFromDetailView);
                    taskListViewAdapter.notifyDataSetChanged();
                    showMessage(getString(R.string.task_updated_feedback_message) + " " + taskFromDetailView.getName());
                } else if (activityResult.getResultCode() == TaskDetailViewActivity.RESULT_DELETE_OK) {
                    Task taskFromDetailView = (Task) activityResult.getData().getSerializableExtra(TaskDetailViewActivity.TASK_DETAIL_VIEW_KEY);
                    viewModel.deleteTask(taskFromDetailView.getId());
                    showMessage(getString(R.string.task_deleted_feedback_message) + " " + taskFromDetailView.getName());
                }
            }
    );

    private final ActivityResultLauncher<Intent> taskDetailViewForAddLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            activityResult -> {
                if (activityResult.getResultCode() == TaskDetailViewActivity.RESULT_OK) {
                    Task taskFromDetailView = (Task) activityResult.getData().getSerializableExtra(TaskDetailViewActivity.TASK_DETAIL_VIEW_KEY);
                    viewModel.createTask(taskFromDetailView);
                    showMessage(getString(R.string.task_added_feedback_message) + " " + taskFromDetailView.getName());
                }
            }
    );

    private void handleTaskProcessingState(TaskListViewModel.ProcessingState processingState) {
        switch (processingState) {
            case CREATE_FAIL:
                progressBar.setVisibility(View.GONE);
                taskListViewAdapter.notifyDataSetChanged();
                showMessage(getString(R.string.remote_db_create_fail_message));
                break;
            case RUNNING_LONG:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case DONE:
                progressBar.setVisibility(View.GONE);
                taskListViewAdapter.notifyDataSetChanged();
                break;
            case CONNECT_REMOTE_FAIL:
                progressBar.setVisibility(View.GONE);
                showMessage(getString(R.string.task_db_connect_fail_message));
                viewModel.setLocalTaskDatabaseOperation();
                viewModel.readAllTasks();
                break;
            case UPDATE_REMOTE_FAIL:
                progressBar.setVisibility(View.GONE);
                //taskListViewAdapter.notifyDataSetChanged();
                showMessage(getString(R.string.remote_db_update_fail_message));
                break;
            case UPDATE_LOCAL_FAIL:
                progressBar.setVisibility(View.GONE);
                showMessage(getString(R.string.local_db_update_fail_message));
                break;
            case DELETE_REMOTE_FAIL:
                progressBar.setVisibility(View.GONE);
                taskListViewAdapter.notifyDataSetChanged();
                showMessage(getString(R.string.remote_db_delete_fail_message));
                break;
            case DELETE_LOCAL_FAIL:
                progressBar.setVisibility(View.GONE);
                showMessage(getString(R.string.local_db_delete_fail_message));
                break;
            case READ_FAIL:
                progressBar.setVisibility(View.GONE);
                taskListViewAdapter.notifyDataSetChanged();
                showMessage(getString(R.string.db_read_fail_message));
                break;
        }
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(R.id.taskListViewActivity), message, Snackbar.LENGTH_SHORT).show();
    }

    private class TaskListAdapter extends ArrayAdapter<Task> {
        public TaskListAdapter(Context owner, int resourceId, List<Task> taskList) {
            super(owner, resourceId, taskList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View recyclableTaskView, @NonNull ViewGroup parent) {
            View taskView;
            StructuredTaskViewBinding taskViewBinding;
            Task taskFromList = getItem(position);

            // recyclableTaskView do not exist, then create one
            if (recyclableTaskView == null) {
                taskViewBinding = DataBindingUtil.inflate(
                        getLayoutInflater(),
                        R.layout.structured_task_view,
                        null,
                        false
                );
                taskView = taskViewBinding.getRoot();
                taskView.setTag(taskViewBinding);
                taskViewBinding.setTaskListViewModel(viewModel);

                // recyclableTaskView exist, then reuse and access it with binding object
            } else {
                taskView = recyclableTaskView;
                taskViewBinding = (StructuredTaskViewBinding) taskView.getTag();
            }

            taskViewBinding.setTask(taskFromList);

            taskView.setBackgroundResource(taskFromList.getPriority().resourceId);
            taskView.setOnClickListener(v -> {
                Task selectedTask = taskListViewAdapter.getItem(position);
                showEditTaskDetailView(selectedTask);
            });

            TextView dueDateView = taskView.findViewById(R.id.taskDate);
            setDueDateColor(taskFromList.getExpiry(), dueDateView);

            Spinner prioritySpinner = taskView.findViewById(R.id.dropdownPriority);
            setPriorityDropDown(taskFromList, prioritySpinner, taskView);

            return taskView;
        }
    }

    private void setDueDateColor(Long expiry, TextView dueDateView) {
        int color = viewModel.isExpiredDate(expiry)
                ? Color.RED
                : Color.DKGRAY;

        dueDateView.setTextColor(color);
    }

    private void setPriorityDropDown(Task taskFromList, Spinner prioritySpinner, View taskView) {
        List<String> priorities = Arrays.asList(
                Task.Priority.NONE.name(),
                Task.Priority.LOW.name(),
                Task.Priority.NORMAL.name(),
                Task.Priority.HIGH.name(),
                Task.Priority.CRITICAL.name()
        );

        if (!taskFromList.getPriority().equals(Task.Priority.NONE)) {
            priorities.set(0, taskFromList.getPriority().name());
            priorities = priorities.stream().distinct().collect(Collectors.toList());
        }

        ArrayAdapter<String> dropDownPriorityAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item, priorities);

        dropDownPriorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        prioritySpinner.setAdapter(dropDownPriorityAdapter);
        prioritySpinner.setSelection(0, false);
        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                String selectedPriority = prioritySpinner.getSelectedItem().toString();
                taskFromList.setPriority(Task.Priority.valueOf(selectedPriority));
                taskView.setBackgroundResource(taskFromList.getPriority().resourceId);
                viewModel.updateTask(taskFromList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }
}
