package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.ArrayList;
import java.util.List;

public class TaskCRUDOperation implements ITaskCRUDOperation {

    private final List<Task> tasks = new ArrayList<>();

    public TaskCRUDOperation() {
        tasks.add(new Task("Aufgabe 1", "Beschreibung 1", false, false));
        tasks.add(new Task("Aufgabe 2", "Beschreibung 2", false, false));
        tasks.add(new Task("Aufgabe 3", "Beschreibung 3", false, false));
        tasks.add(new Task("Aufgabe 4", "Beschreibung 4", false, false));
    }

    @Override
    public Task createTask(Task task) {
        Task newTask =  new Task(task.getName(), task.getDescription(), task.isCompleted(), task.isFavorite());
        tasks.add(newTask);
        return newTask;
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
