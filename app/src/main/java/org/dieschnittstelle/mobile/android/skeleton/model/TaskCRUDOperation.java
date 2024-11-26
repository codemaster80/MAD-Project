package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.ArrayList;
import java.util.List;

public class TaskCRUDOperation implements ITaskCRUDOperation {

    private static long idCounter = 0;
    private final List<Task> tasks = new ArrayList<>();

    public TaskCRUDOperation() {
        createTask(new Task("Aufgabe 1", "Beschreibung 1", "01.01.2025", "12:00", false, false, Task.Priority.CRITICAL));
        createTask(new Task("Aufgabe 2", "Beschreibung 2", "05.12.2024", "10:00", false, false, Task.Priority.HIGH));
        createTask(new Task("Aufgabe 3", "Beschreibung 3", "01.10.2024", "09:00", false, false, Task.Priority.NORMAL));
        createTask(new Task("Aufgabe 4", "Beschreibung 4", "15.01.2024", "08:00", false, false, Task.Priority.LOW));
    }

    @Override
    public Task createTask(Task task) {
        task.setId(++idCounter);
        tasks.add(task);
        return task;
    }

    @Override
    public List<Task> readAllTasks() {
        return new ArrayList<>(tasks);
    }

    @Override
    public Task readTask(long id) {
        return null;
    }

    @Override
    public boolean updateTask(Task task) {
        return true;
    }

    @Override
    public boolean deleteTask(long id) {
        return false;
    }
}
