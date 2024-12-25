package org.dieschnittstelle.mobile.android.skeleton.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.dieschnittstelle.mobile.android.skeleton.model.ITaskDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.LocalTaskDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.RemoteTaskDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.Task;
import org.dieschnittstelle.mobile.android.skeleton.util.DateConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskListViewModel extends ViewModel {
    private ITaskDatabaseOperation taskDbOperation;
    private final List<Task> taskList = new ArrayList<>();
    private static final Comparator<Task> SORT_BY_COMPLETED_AND_NAME = Comparator.comparing(Task::isCompleted).thenComparing(Task::getName);
    private static final Comparator<Task> SORT_BY_PRIO_AND_DATE = Comparator.comparing(Task::getPriority).thenComparing(task -> task.getExpiry() != null);
    private Comparator<Task> currentSorter = SORT_BY_COMPLETED_AND_NAME;
    public enum ProcessingState {
        CREATE_FAIL,
        READ_FAIL,
        UPDATE_REMOTE_FAIL,
        UPDATE_LOCAL_FAIL,
        DELETE_REMOTE_FAIL,
        DELETE_LOCAL_FAIL,
        CONNECT_REMOTE_FAIL,
        RUNNING_LONG,
        RUNNING,
        DONE,
    }

    private final MutableLiveData<ProcessingState> processingState = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private Context applicationContext;
    private LocalTaskDatabaseOperation localDatabase;
    private RemoteTaskDatabaseOperation remoteDatabase;

    public void setTaskDbOperation(ITaskDatabaseOperation dbOp) {
        taskDbOperation = dbOp;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void initializeDb() {
        localDatabase = new LocalTaskDatabaseOperation(this.applicationContext);
        remoteDatabase = new RemoteTaskDatabaseOperation();
    }

    public MutableLiveData<ProcessingState> getProcessingState() {
        return processingState;
    }

    public void setCurrentSorter(Comparator<Task> sorter) {
        currentSorter = sorter;
    }

    public void createTask(Task taskFromDetailView) {
        processingState.setValue(ProcessingState.RUNNING_LONG);

        executorService.execute(() -> {
            try {
                taskList.add(localDatabase.createTask(taskFromDetailView));
                taskList.sort(currentSorter);
                remoteDatabase.createTask(taskFromDetailView);
            } catch (Exception e) {
                // assuming local always successful
                processingState.postValue(ProcessingState.CREATE_FAIL);
                return;
            }

            processingState.postValue(ProcessingState.DONE);
        });
    }

    public void readAllTasks() {
        processingState.setValue(ProcessingState.RUNNING_LONG);

        executorService.execute(() -> {
            try {
                // reading from initialised db
                taskList.addAll(taskDbOperation.readAllTasks());
                taskList.sort(currentSorter);

            } catch (Exception e) {
                processingState.postValue(ProcessingState.READ_FAIL);
                return;
            }

            processingState.postValue(ProcessingState.DONE);
        });
    }

    public void updateTask(Task task) {
        processingState.setValue(ProcessingState.RUNNING_LONG);

        executorService.execute(() -> {
            boolean isUpdated = localDatabase.updateTask(task);
            if (isUpdated) {

                // update the tasklist with new task
                taskList.removeIf(t -> t.getId() == task.getId());
                taskList.add(task);
                taskList.sort(currentSorter);

                // update the remote db with updated task
                try {
                    remoteDatabase.updateTask(task);
                } catch(Exception e) {
                    processingState.postValue(ProcessingState.UPDATE_REMOTE_FAIL);
                    return;
                }

                processingState.postValue(ProcessingState.DONE);

            } else {
                processingState.postValue(ProcessingState.UPDATE_LOCAL_FAIL);
            }
        });
    }

    public void deleteTask(long id) {
        processingState.setValue(ProcessingState.RUNNING_LONG);

        executorService.execute(() -> {

            boolean isDeleted = localDatabase.deleteTask(id);
            if (isDeleted) {
                taskList.removeIf(t -> t.getId() == id);

                try {
                    remoteDatabase.deleteTask(id);
                } catch (Exception ignored) {
                    processingState.postValue(ProcessingState.DELETE_REMOTE_FAIL);
                    return;
                }

                processingState.postValue(ProcessingState.DONE);

            } else {
                processingState.postValue(ProcessingState.DELETE_LOCAL_FAIL);

            }
        });
    }

    public void deleteAllTasksFromLocal() {
        processingState.setValue(ProcessingState.RUNNING_LONG);

        executorService.execute(() -> {

            boolean isSuccess = localDatabase.deleteAllTasks();
            if (isSuccess && taskDbOperation instanceof LocalTaskDatabaseOperation) {
                taskList.clear();
            }

            processingState.postValue(isSuccess ? ProcessingState.DONE : ProcessingState.DELETE_LOCAL_FAIL);
        });
    }

    public void deleteAllTasksFromRemote() {
        processingState.setValue(ProcessingState.RUNNING_LONG);

        executorService.execute(() -> {

            boolean isSuccess = remoteDatabase.deleteAllTasks();
            if (isSuccess && taskDbOperation instanceof RemoteTaskDatabaseOperation) {
                taskList.clear();
            }

            processingState.postValue(isSuccess ? ProcessingState.DONE : ProcessingState.DELETE_REMOTE_FAIL);
        });
    }

    public String toDueDateString(Long expiry) {
        String dateTime = DateConverter.toDateString(expiry);
        if (dateTime.isBlank()) {
            return dateTime;
        }
        return dateTime.split(" ")[0];
    }

    public boolean isExpiredDate(Long expiry) {
        return expiry != null && expiry <= DateConverter.fromDate(Calendar.getInstance().getTime());
    }

    public void sortTasksByCompletedAndName() {
        processingState.setValue(ProcessingState.RUNNING);
        setCurrentSorter(SORT_BY_COMPLETED_AND_NAME);
        taskList.sort(currentSorter);
        processingState.postValue(ProcessingState.DONE);
    }

    public void sortTasksByPrioAndDate() {
        processingState.setValue(ProcessingState.RUNNING);
        setCurrentSorter(SORT_BY_PRIO_AND_DATE);
        taskList.sort(currentSorter);
        processingState.postValue(ProcessingState.DONE);
    }

    /**
     * Compare tasks between remote DB and local DB
     * if there are no local tasks, all tasks are transmitted from remote to local DB.
     * if there are local tasks, then all tasks on remote DB are deleted and local tasks are transferred to remote DB.
     */
    public void synchronizeDb() {
        try {
            List<Task> localTasks = localDatabase.readAllTasks();
            List<Task> remoteTasks = remoteDatabase.readAllTasks();

            // synchronize logic
            if (localTasks.isEmpty()) {
                remoteTasks.forEach(task -> localDatabase.createTask(task));
            } else {
                remoteDatabase.deleteAllTasks();
                localTasks.forEach(localTask -> remoteDatabase.createTask(localTask));
            }

            // update the model tasklist
            taskList.addAll(localTasks.isEmpty() ? remoteTasks : localTasks);
            taskList.sort(currentSorter);
            processingState.postValue(ProcessingState.DONE);

        } catch (Exception e) {
            processingState.postValue(ProcessingState.CONNECT_REMOTE_FAIL);
        }
    }

    public void setContext(Context ctx)
    {
        applicationContext = ctx;
    }

    public void setLocalTaskDatabaseOperation()
    {
        taskDbOperation = localDatabase;
    }

    public void setRemoteTaskDatabaseOperation()
    {
        taskDbOperation = remoteDatabase;
    }
}
