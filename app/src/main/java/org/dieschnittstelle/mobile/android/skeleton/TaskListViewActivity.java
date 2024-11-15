package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.databinding.StructuredTaskViewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.ITaskCRUDOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.LocalTaskCRUDOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.Task;
import org.dieschnittstelle.mobile.android.skeleton.model.TaskCRUDOperation;

import java.util.ArrayList;
import java.util.List;

public class TaskListViewActivity extends AppCompatActivity {

    protected static final int REQUEST_CODE_DETAIL_VIEW_EDIT_CALL = 1;
    protected static final int REQUEST_CODE_DETAIL_VIEW_ADD_CALL = 2;
//    private ViewGroup taskListView;
    private ListView taskListView;
    private List<Task> taskList = new ArrayList<>();
    private ArrayAdapter<Task> taskListViewAdapter;
    private FloatingActionButton addTaskAction;

    private ITaskCRUDOperation taskCRUDOperation;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_view);
        taskListView = findViewById(R.id.taskListView);
        taskCRUDOperation = new TaskCRUDOperation();
//        taskCRUDOperation = new LocalTaskCRUDOperation(this);

//        taskListViewAdapter = new ArrayAdapter<>(this, R.layout.task_view, taskList);
//        taskListViewAdapter = new ArrayAdapter<>(this, R.layout.structured_task_view, R.id.taskName, taskList);
        taskListViewAdapter = new ArrayAdapter<>(this, R.layout.structured_task_view, taskList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View recyclableTaskView, @NonNull ViewGroup parent) {
                View taskView;
                StructuredTaskViewBinding bind;
                Task taskFromList = getItem(position);

                // recyclableTaskView do not exist, then create one
                if (recyclableTaskView == null) {
                    bind = DataBindingUtil.inflate(
                            getLayoutInflater(),
                            R.layout.structured_task_view,
                            null,
                            false
                    );
                    taskView = bind.getRoot();
                    taskView.setTag(bind);

                // recyclableTaskView exist, then reuse and access it with binding object
                } else {
                    taskView = recyclableTaskView;
                    bind = (StructuredTaskViewBinding) taskView.getTag();
                }
                bind.setTask(taskFromList);
                return taskView;

//                ViewGroup taskListView = (ViewGroup) getLayoutInflater().inflate(R.layout.structured_task_view, null);
//                                ((TextView) taskListView.findViewById(R.id.taskName)).setText(taskFromList.getName());
//                ((CheckBox) taskListView.findViewById(R.id.taskCompleted)).setChecked(taskFromList.isCompleted());
//                ((CheckBox) taskListView.findViewById(R.id.taskCompleted))
//                        .setOnCheckedChangeListener(
//                                (buttonView, isChecked) -> taskFromList.setCompleted(isChecked)
//                        );
//
//                return taskListView;
            }
        };
        taskListView.setAdapter(taskListViewAdapter);
        taskListView.setOnItemClickListener((parent, view, position, id) -> {
            Task selectedTask = taskListViewAdapter.getItem(position);
            showTaskDetailView(selectedTask);
        });

//        taskList.forEach(task -> {
//            TextView taskView = (TextView) getLayoutInflater().inflate(R.layout.task_view, null);
//            taskView.setText(task.getName());
//            taskView.setOnClickListener(view -> showTaskDetailView(task));
//
//            taskListView.addView(taskView);
//        });

        addTaskAction = findViewById(R.id.addTaskAction);
        addTaskAction.setOnClickListener(view -> this.showNewTaskDetailView());

        this.progressBar = findViewById(R.id.progressBar);
        this.progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            List<Task> tasks = this.taskCRUDOperation.readAllTasks();
            this.taskList.addAll(tasks);
            runOnUiThread(() -> {
                this.progressBar.setVisibility(View.GONE);
                this.taskListViewAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    protected void showTaskDetailView(Task task) {
        Intent callTaskDetailViewIntent = new Intent(this, TaskDetailViewActivity.class);
        callTaskDetailViewIntent.putExtra(TaskDetailViewActivity.TASK_DETAIL_VIEW_KEY, task);
        startActivityForResult(callTaskDetailViewIntent, REQUEST_CODE_DETAIL_VIEW_EDIT_CALL);
    }

    protected void showNewTaskDetailView() {
        Intent callTaskDetailViewIntent = new Intent(this, TaskDetailViewActivity.class);
        startActivityForResult(callTaskDetailViewIntent, REQUEST_CODE_DETAIL_VIEW_ADD_CALL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_DETAIL_VIEW_EDIT_CALL && resultCode == TaskDetailViewActivity.RESULT_OK) {
            Task taskFromDetailView = (Task) data.getSerializableExtra(TaskDetailViewActivity.TASK_DETAIL_VIEW_KEY);
            boolean isUpdated = this.taskCRUDOperation.updateTask(taskFromDetailView);
            if (isUpdated) {
                Task selectedTask = taskList.stream()
                        .filter(task -> task.getId() == (taskFromDetailView.getId()))
                        .findAny()
                        .orElse(new Task());
                selectedTask.setName(taskFromDetailView.getName());
                selectedTask.setDescription(taskFromDetailView.getDescription());
                selectedTask.setCompleted(taskFromDetailView.isCompleted());
                // TODO: update DB
                taskListViewAdapter.notifyDataSetChanged();
                showMessage(getString(R.string.task_updated_feedback_message) + " " + taskFromDetailView.getName() + " description: " + taskFromDetailView.getDescription());
            }
        }
        if (requestCode == REQUEST_CODE_DETAIL_VIEW_ADD_CALL && resultCode == TaskDetailViewActivity.RESULT_OK) {
            Task taskFromDetailView = (Task) data.getSerializableExtra(TaskDetailViewActivity.TASK_DETAIL_VIEW_KEY);

            // update DB
            new Thread(() -> {
                Task createdTask = this.taskCRUDOperation.createTask(taskFromDetailView);
                taskList.add(createdTask);
                runOnUiThread(() -> {
                    taskListViewAdapter.notifyDataSetChanged();
                });
            }).start();

            showMessage(getString(R.string.task_added_feedback_message) + " " + taskFromDetailView.getName() + " description: " + taskFromDetailView.getDescription());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(R.id.taskListViewActivity), message, Snackbar.LENGTH_SHORT).show();
    }
}
