package org.dieschnittstelle.mobile.android.skeleton.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MockTaskDatabaseOperation implements ITaskDatabaseOperation {

    private static long idCounter = 0;
    private final List<Task> tasks = new ArrayList<>();
    DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMANY);

    public MockTaskDatabaseOperation() throws ParseException {
        createTask(new Task("Aufgabe 1", "Beschreibung 1", df.parse("01.01.2025 12:00"), false, false, Task.Priority.CRITICAL));
        createTask(new Task("Aufgabe 2", "Beschreibung 2", df.parse("05.12.2024 10:00"),false, false, Task.Priority.HIGH));
        createTask(new Task("Aufgabe 3", "Beschreibung 3", df.parse("01.10.2024 09:00"),false, false, Task.Priority.NORMAL));
        createTask(new Task("Aufgabe 4", "Beschreibung 4", df.parse("15.01.2024 08:00"),false, false, Task.Priority.LOW));
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
        return tasks.stream()
                .filter(existingTask -> existingTask.getId() == (id))
                .findAny()
                .orElse(null);
    }

    @Override
    public boolean updateTask(Task task) {
        Task selectedTask = tasks.stream()
                .filter(existingTask -> existingTask.getId() == (task.getId()))
                .findAny()
                .orElse(null);
        if (selectedTask == null) {
            return false;
        }
        selectedTask.setName(task.getName());
        selectedTask.setDescription(task.getDescription());
        selectedTask.setFavorite(task.isFavorite());
        selectedTask.setCompleted(task.isCompleted());
        selectedTask.setExpiry(task.getExpiry());
        selectedTask.setPriority(task.getPriority());
        return true;
    }

    @Override
    public boolean deleteAllTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
        return true;
    }

    @Override
    public boolean deleteTask(long id) {
        tasks.removeIf(existingTask -> existingTask.getId() == (id));
        return true;
    }
}
