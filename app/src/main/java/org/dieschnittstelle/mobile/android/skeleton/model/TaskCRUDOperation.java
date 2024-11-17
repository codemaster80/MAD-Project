package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.ArrayList;
import java.util.List;

public class TaskCRUDOperation implements ITaskCRUDOperation {

    private final List<Task> tasks = new ArrayList<>();

    public TaskCRUDOperation() {
        tasks.add(new Task("Aufgabe 1", "Beschreibung 1", "01.01.2025", "12:00", false, false));
        tasks.add(new Task("Aufgabe 2", "Beschreibung 2", "05.12.2024", "10:00", false, false));
        tasks.add(new Task("Aufgabe 3", "Beschreibung 3", "01.10.2024", "09:00", false, false));
        tasks.add(new Task("Aufgabe 4", "Beschreibung 4", "15.01.2024", "08:00", false, false));
    }

    @Override
    public Task createTask(Task task) {
        Task newTask =  new Task(task.getName(), task.getDescription(), task.getDate(), task.getTime(), task.isFavorite(), task.isCompleted());
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
