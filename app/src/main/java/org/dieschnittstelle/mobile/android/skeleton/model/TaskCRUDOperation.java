package org.dieschnittstelle.mobile.android.skeleton.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskCRUDOperation implements ITaskCRUDOperation {

    private List<Task> tasks = new ArrayList<>();

    public TaskCRUDOperation() {
        tasks.add(new Task("Aufgabe 1", "Beschreibung 1", false));
        tasks.add(new Task("Aufgabe 2", "Beschreibung 2", false));
        tasks.add(new Task("Aufgabe 3", "Beschreibung 3", false));
        tasks.add(new Task("Aufgabe 4", "Beschreibung 4", false));
    }

    @Override
    public Task createTask(Task task) {
        Task newTask =  new Task(task.getName(), task.getDescription(), task.isCompleted());
        tasks.add(newTask);
        return newTask;
    }

    @Override
    public List<Task> readAllTasks() {
        return new ArrayList<>(tasks);
    }

    @Override
    public Task readTask(UUID id) {
        return null;
    }

    @Override
    public boolean updateTask(Task task) {
        return false;
    }

    @Override
    public boolean deleteTask(UUID id) {
        return false;
    }
}
