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

public class LocalTaskCRUDOperation implements ITaskCRUDOperation{

    @Dao
    public interface SQLiteTaskCRUDOperation {

        @Insert
        long createTask(Task task);

        @Query("SELECT * FROM task")
        List<Task> readAllTasks();

        @Query("SELECT * FROM task WHERE id=(:id)")
        Task readTask(long id);

        @Update
        void updateTask(Task task);

        @Delete
        void deleteTask(Task task);
    }

    @Database(entities = {Task.class}, version = 1)
    public abstract static class TaskDatabase extends RoomDatabase {

        public abstract SQLiteTaskCRUDOperation getDao();
    }

    private final TaskDatabase taskDatabase;

    public LocalTaskCRUDOperation(Context context) {
        taskDatabase = Room.databaseBuilder(
                context.getApplicationContext(),
                TaskDatabase.class,
                "task-db"
        ).build();
    }

    @Override
    public Task createTask(Task task) {
        long newTaskId = taskDatabase.getDao().createTask(task);
        task.setId(newTaskId);
        return task;
    }

    @Override
    public List<Task> readAllTasks() {
        return taskDatabase.getDao().readAllTasks();
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