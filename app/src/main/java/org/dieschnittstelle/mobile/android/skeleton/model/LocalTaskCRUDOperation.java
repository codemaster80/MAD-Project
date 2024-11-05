package org.dieschnittstelle.mobile.android.skeleton.model;

import android.content.Context;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;

import java.util.List;
import java.util.UUID;

public class LocalTaskCRUDOperation implements ITaskCRUDOperation{

    @Dao
    public static interface SQLiteTaskCRUDOperation {

        @Insert
        public UUID createTask(Task task);

        @Query("SELECT * FROM task")
        public List<Task> readAllTasks();

        @Query("SELECT * FROM task WHERE id=(:id)")
        public Task readTask(UUID id);

        @Update
        public void updateTask(Task task);

        @Delete
        public void deleteTask(Task task);
    }

    @Database(entities = {Task.class}, version = 1)
    public abstract static class TaskDatabase extends RoomDatabase {

        public abstract SQLiteTaskCRUDOperation getDao();
    }

    private TaskDatabase taskDatabase;

    public LocalTaskCRUDOperation(Context context) {
        taskDatabase = Room.databaseBuilder(
                context.getApplicationContext(),
                TaskDatabase.class,
                "task-db"
        ).build();
    }

    @Override
    public Task createTask(Task task) {
        UUID newTaskId = taskDatabase.getDao().createTask(task);
        task.setId(newTaskId);
        return task;
    }

    @Override
    public List<Task> readAllTasks() {
        return taskDatabase.getDao().readAllTasks();
    }

    @Override
    public Task readTask(UUID id) {
        return null;
    }

    @Override
    public boolean updateTask(Task task) {
        return true;
    }

    @Override
    public boolean deleteTask(UUID id) {
        return false;
    }
}
