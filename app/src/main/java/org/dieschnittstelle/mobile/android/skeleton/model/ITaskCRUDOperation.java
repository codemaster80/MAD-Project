package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.List;
import java.util.UUID;

public interface ITaskCRUDOperation {

    public Task createTask(Task task);

    public List<Task> readAllTasks();

    public Task readTask(UUID id);

    public boolean updateTask(Task task);

    public boolean deleteTask(UUID id);
}
