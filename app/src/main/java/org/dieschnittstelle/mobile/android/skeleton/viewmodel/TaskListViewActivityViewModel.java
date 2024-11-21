package org.dieschnittstelle.mobile.android.skeleton.viewmodel;

import androidx.lifecycle.ViewModel;

import org.dieschnittstelle.mobile.android.skeleton.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskListViewActivityViewModel extends ViewModel {
    private List<Task> taskList = new ArrayList<>();
    private boolean initialised;

    public TaskListViewActivityViewModel() {
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
}
