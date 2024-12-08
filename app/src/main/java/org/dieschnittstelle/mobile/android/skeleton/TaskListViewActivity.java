package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import org.dieschnittstelle.mobile.android.skeleton.model.RoomTaskDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.Task;
import org.dieschnittstelle.mobile.android.skeleton.model.MockTaskDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.viewmodel.TaskListViewModel;

import java.util.List;

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

        taskListView = findViewById(R.id.taskListView);
//        ITaskDatabaseOperation taskDbOperation = new MockTaskDatabaseOperation();
        ITaskDatabaseOperation taskDbOperation = new RoomTaskDatabaseOperation(this);
        viewModel.setTaskDbOperation(taskDbOperation);

        taskListViewAdapter = new TaskListAdapter(this, R.layout.structured_task_view, viewModel.getTaskList());
        taskListView.setAdapter(taskListViewAdapter);

        taskListView.setOnItemClickListener((parent, view, position, id) -> {
            Task selectedTask = taskListViewAdapter.getItem(position);
            showTaskDetailView(selectedTask);
        });

        addTaskAction = findViewById(R.id.addTaskAction);
        addTaskAction.setOnClickListener(view -> this.showNewTaskDetailView());

        this.progressBar = findViewById(R.id.progressBar);
        viewModel.getProcessingState().observe(this, processingState -> {
            if (processingState == TaskListViewModel.ProcessingState.RUNNING_LONG) {
                this.progressBar.setVisibility(View.VISIBLE);
            }
            else if (processingState == TaskListViewModel.ProcessingState.DONE) {
                this.progressBar.setVisibility(View.GONE);
                this.taskListViewAdapter.notifyDataSetChanged();
            }
        });

        if (!viewModel.isInitialised()) {
            viewModel.readAllTasks();
        }
    }

    private void showTaskDetailView(Task task) {
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
