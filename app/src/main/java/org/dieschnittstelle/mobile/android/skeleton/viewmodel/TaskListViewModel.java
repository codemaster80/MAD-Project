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
    private boolean initialised;
    private static final Comparator<Task> SORT_BY_COMPLETED_AND_NAME = Comparator.comparing(Task::isCompleted).thenComparing(Task::getName);
    private static final Comparator<Task> SORT_BY_PRIO_AND_DATE = Comparator.comparing(Task::getPriority).thenComparing(Task::getExpiry);
    private Comparator<Task> currentSorter = SORT_BY_COMPLETED_AND_NAME;
    public enum ProcessingState {DB_INIT_CONNECT_FAIL, RUNNING_LONG, RUNNING, DONE}
    private final MutableLiveData<ProcessingState> processingState = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public TaskListViewModel() {}

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

    public void setCurrentSorter(Comparator<Task> currentSorter) {
        this.currentSorter = currentSorter;
    }

    public void createTask(Task taskFromDetailView, Context ctxForLocalDB) {
        processingState.setValue(ProcessingState.RUNNING);
        new Thread(() -> {
            Task createdTask = taskDbOperation.createTask(taskFromDetailView);
            // also create task on remote DB
            if (taskDbOperation instanceof LocalTaskDatabaseOperation) {
                try {
                    this.setTaskDbOperation(new RemoteTaskDatabaseOperation());
                } catch (Exception ignored) {}
                taskDbOperation.createTask(createdTask);
                this.setTaskDbOperation(new LocalTaskDatabaseOperation(ctxForLocalDB));
            }
            getTaskList().add(createdTask);
            doSort();
            processingState.postValue(ProcessingState.DONE);
        }).start();
    }

    public void readAllTasks(Context ctxForLocalDB) {
        processingState.setValue(ProcessingState.RUNNING_LONG);
        new Thread(() -> {
            try {
                List<Task> tasks = taskDbOperation.readAllTasks();
                if (taskDbOperation instanceof RemoteTaskDatabaseOperation) {
                    replaceAllTasks(tasks, ctxForLocalDB);
                }
                getTaskList().addAll(tasks);
                doSort();
                processingState.postValue(ProcessingState.DONE);
            } catch (Exception e) {
                processingState.postValue(ProcessingState.DB_INIT_CONNECT_FAIL);
            }
        }).start();
    }

    public void updateTask(Task taskFromDetailView, Context ctxForLocalDB) {
        processingState.setValue(ProcessingState.RUNNING_LONG);
        executorService.execute(() -> {
            boolean isUpdated = taskDbOperation.updateTask(taskFromDetailView);
            if (isUpdated) {
                Task selectedTask = getTaskList().stream()
                        .filter(task -> task.getId() == (taskFromDetailView.getId()))
                        .findAny()
                        .orElse(new Task());

                // also update task on remote DB
                if (taskDbOperation instanceof LocalTaskDatabaseOperation) {
                    try {
                        this.setTaskDbOperation(new RemoteTaskDatabaseOperation());
                    } catch (Exception ignored) {}
                    taskDbOperation.updateTask(selectedTask);
                    this.setTaskDbOperation(new LocalTaskDatabaseOperation(ctxForLocalDB));
                }
                selectedTask.setName(taskFromDetailView.getName());
                selectedTask.setDescription(taskFromDetailView.getDescription());
                selectedTask.setExpiry(taskFromDetailView.getExpiry());
                selectedTask.setCompleted(taskFromDetailView.isCompleted());
                selectedTask.setFavorite(taskFromDetailView.isFavorite());
                selectedTask.setPriority(taskFromDetailView.getPriority());
                doSort();
                processingState.postValue(ProcessingState.DONE);
            }
        });
    }

    public void deleteTask(long id, Context ctxForLocalDB) {
        processingState.setValue(ProcessingState.RUNNING);
        executorService.execute(() -> {
            Task task = new Task();
            task.setId(id);
            boolean isDeleted = taskDbOperation.deleteTask(id);
            if (isDeleted) {
                // also delete task on remote DB
                if (taskDbOperation instanceof LocalTaskDatabaseOperation) {
                    try {
                        this.setTaskDbOperation(new RemoteTaskDatabaseOperation());
                    } catch (Exception ignored) {}
                    taskDbOperation.deleteTask(id);
                    this.setTaskDbOperation(new LocalTaskDatabaseOperation(ctxForLocalDB));
                }
                getTaskList().remove(task);
                processingState.postValue(ProcessingState.DONE);
            }
        });
    }

    public void deleteAllTasksFromLocal(Context ctxForLocalDB) {
        processingState.setValue(ProcessingState.RUNNING);
        executorService.execute(() -> {
            boolean isDeleted = false;
            if (taskDbOperation instanceof RemoteTaskDatabaseOperation) {
                this.setTaskDbOperation(new LocalTaskDatabaseOperation(ctxForLocalDB));
                isDeleted = taskDbOperation.deleteAllTasks();
                this.setTaskDbOperation(new RemoteTaskDatabaseOperation());
            } else if (taskDbOperation instanceof LocalTaskDatabaseOperation) {
                isDeleted = taskDbOperation.deleteAllTasks();
            }
            if (isDeleted) {
                getTaskList().clear();
                processingState.postValue(ProcessingState.DONE);
            }
        });
    }

    public void deleteAllTasksFromRemote(Context ctxForLocalDB) {
        processingState.setValue(ProcessingState.RUNNING);
        executorService.execute(() -> {
            boolean isDeleted = false;
            if (taskDbOperation instanceof LocalTaskDatabaseOperation) {
                this.setTaskDbOperation(new RemoteTaskDatabaseOperation());
                isDeleted = taskDbOperation.deleteAllTasks();
                this.setTaskDbOperation(new LocalTaskDatabaseOperation(ctxForLocalDB));
            } else if (taskDbOperation instanceof RemoteTaskDatabaseOperation) {
                isDeleted = taskDbOperation.deleteAllTasks();
            }
            if (isDeleted) {
                getTaskList().clear();
                processingState.postValue(ProcessingState.DONE);
            }
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

    public void sortTasks() {
        processingState.setValue(ProcessingState.RUNNING);
        this.setCurrentSorter(SORT_BY_COMPLETED_AND_NAME);
        doSort();
        processingState.postValue(ProcessingState.DONE);
    }

    private void doSort() {
        getTaskList().sort(currentSorter);
    }

    public void sortTasksByPrioAndDate() {
        processingState.setValue(ProcessingState.RUNNING);
        this.setCurrentSorter(SORT_BY_PRIO_AND_DATE);
        doSort();
        processingState.postValue(ProcessingState.DONE);
    }

    /**
     * Compare tasks between remote DB and local DB
     * if there are no local tasks, all tasks are transmitted from remote to local DB.
     * if there are local tasks, then all tasks on remote DB are deleted and local tasks are transferred to remote DB.
     *
     * @param tasksFromRemoteDB List of tasks from remote DB
     * @param ctxForLocalDB Application context for local DB creation
     */
    public void replaceAllTasks(List<Task> tasksFromRemoteDB, Context ctxForLocalDB) {
        this.setTaskDbOperation(new LocalTaskDatabaseOperation(ctxForLocalDB));
        List<Task> localTasks = taskDbOperation.readAllTasks();
        if (localTasks.isEmpty()) {
            tasksFromRemoteDB.forEach(remoteTask -> taskDbOperation.createTask(remoteTask));
            this.setTaskDbOperation(new RemoteTaskDatabaseOperation());
        } else {
            this.setTaskDbOperation(new RemoteTaskDatabaseOperation());
            taskDbOperation.deleteAllTasks();
            localTasks.forEach(localTask -> taskDbOperation.createTask(localTask));
        }
    }
}
