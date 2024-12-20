package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

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
import org.dieschnittstelle.mobile.android.skeleton.model.ITaskDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.LocalTaskDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.Task;
import org.dieschnittstelle.mobile.android.skeleton.viewmodel.TaskListViewModel;

import java.util.List;

public class TaskListViewActivity extends AppCompatActivity {
    private ListView taskListView;
    private ITaskDatabaseOperation taskDbOperation;
    private ArrayAdapter<Task> taskListViewAdapter;
    private FloatingActionButton addTaskAction;
    private ProgressBar progressBar;
    private TaskListViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_view);
        viewModel = new ViewModelProvider(this).get(TaskListViewModel.class);

        taskListView = findViewById(R.id.taskListView);
        taskDbOperation = ((TaskApplication) getApplication()).getTaskDatabaseOperation();
        viewModel.setTaskDbOperation(taskDbOperation);

        taskListViewAdapter = new TaskListAdapter(this, R.layout.structured_task_view, viewModel.getTaskList());
        taskListView.setAdapter(taskListViewAdapter);

        taskListView.setOnItemClickListener((parent, view, position, id) -> {
            Task selectedTask = taskListViewAdapter.getItem(position);
            showEditTaskDetailView(selectedTask);
        });

        addTaskAction = findViewById(R.id.addTaskAction);
        addTaskAction.setOnClickListener(view -> this.showNewTaskDetailView());

        progressBar = findViewById(R.id.progressBar);

        if (!viewModel.isInitialised()) {
            viewModel.readAllTasks(this);
        }
        viewModel.getProcessingState().observe(this, this::handleTaskProcessingState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_overview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sortItems) {
            this.viewModel.sortItems();
            return true;
        }
        else if (item.getItemId() == R.id.deleteAllLocalItems) {
            showMessage("Delete all local items...");
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
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
            case RUNNING_LONG:
                progressBar.setVisibility(View.VISIBLE);
                break;

            case DONE:
                progressBar.setVisibility(View.GONE);
                taskListViewAdapter.notifyDataSetChanged();
                break;

            case DB_INIT_CONNECT_FAIL:
                progressBar.setVisibility(View.GONE);
                showMessage(getString(R.string.task_db_connect_fail_message));
                ((TaskApplication) getApplication()).setTaskDatabaseOperation(new LocalTaskDatabaseOperation(this));
                taskDbOperation = ((TaskApplication) getApplication()).getTaskDatabaseOperation();
                viewModel.setTaskDbOperation(taskDbOperation);
                viewModel.readAllTasks(this);
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
                taskView.setBackgroundResource(taskFromList.getPriority().resourceId);
                taskView.setTag(taskViewBinding);
                taskViewBinding.setTaskListViewModel(viewModel);

                // recyclableTaskView exist, then reuse and access it with binding object
            } else {
                taskView = recyclableTaskView;
                taskView.setBackgroundResource(taskFromList.getPriority().resourceId);
                taskViewBinding = (StructuredTaskViewBinding) taskView.getTag();
            }
            taskViewBinding.setTask(taskFromList);
            return taskView;
        }
    }
}
