package org.dieschnittstelle.mobile.android.skeleton.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.dieschnittstelle.mobile.android.skeleton.model.ITaskDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskListViewModel extends ViewModel {
    private ITaskDatabaseOperation taskDbOperation;
    private final List<Task> taskList = new ArrayList<>();
    private boolean initialised;

    public enum ProcessingState {RUNNING_LONG, RUNNING, DONE}
    private final MutableLiveData<ProcessingState> processingState = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public TaskListViewModel() {
    }

    public void setTaskDbOperation(ITaskDatabaseOperation taskDbOperation) {
        this.taskDbOperation = taskDbOperation;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    public MutableLiveData<ProcessingState> getProcessingState() {
        return processingState;
    }

    public void createTask(Task taskFromDetailView) {
        processingState.setValue(ProcessingState.RUNNING);
        new Thread(() -> {
            Task createdTask = taskDbOperation.createTask(taskFromDetailView);
            getTaskList().add(createdTask);
            processingState.postValue(ProcessingState.DONE);
        }).start();
    }

    public void readAllTasks() {
        processingState.setValue(ProcessingState.RUNNING_LONG);
        new Thread(() -> {
            List<Task> tasks = taskDbOperation.readAllTasks();
            getTaskList().addAll(tasks);
            processingState.postValue(ProcessingState.DONE);
        }).start();
    }

    public void readTask(long id) {

    }

    public void updateTask(Task taskFromDetailView) {
        processingState.setValue(ProcessingState.RUNNING_LONG);
        executorService.execute(() -> {
            boolean isUpdated = taskDbOperation.updateTask(taskFromDetailView);
            if (isUpdated) {
                Task selectedTask = getTaskList().stream()
                        .filter(task -> task.getId() == (taskFromDetailView.getId()))
                        .findAny()
                        .orElse(new Task());
                selectedTask.setName(taskFromDetailView.getName());
                selectedTask.setDescription(taskFromDetailView.getDescription());
                selectedTask.setDate(taskFromDetailView.getDate());
                selectedTask.setCompleted(taskFromDetailView.isCompleted());
                selectedTask.setFavorite(taskFromDetailView.isFavorite());
                selectedTask.setPriority(taskFromDetailView.getPriority());
                processingState.postValue(ProcessingState.DONE);
            }
        });
    }

    public void deleteTask(long id) {
    }
}
