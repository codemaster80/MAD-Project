package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.List;

public interface ITaskDatabaseOperation {

    Task createTask(Task task);

    List<Task> readAllTasks();

    Task readTask(long id);

    boolean updateTask(Task task);

    boolean deleteTask(long id);
}
