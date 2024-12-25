package org.dieschnittstelle.mobile.android.skeleton.model;

import android.content.Context;
import android.util.Log;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;

import java.util.List;

public class LocalTaskDatabaseOperation implements ITaskDatabaseOperation {

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

        @Query("DELETE FROM task")
        void deleteAllTasks();

        @Delete
        void deleteTask(Task task);
    }

    @Database(entities = {Task.class}, version = 1)
    public abstract static class TaskDatabase extends RoomDatabase {

        public abstract SQLiteTaskCRUDOperation getDao();
    }

    private final TaskDatabase taskDatabase;

    public LocalTaskDatabaseOperation(Context context) {
        taskDatabase = Room.databaseBuilder(
                context.getApplicationContext(),
                TaskDatabase.class,
                "task-db"
        )
                // clear DB when schema changed
                .fallbackToDestructiveMigration()
                .build();
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
        return taskDatabase.getDao().readTask(id);
    }

    @Override
    public boolean updateTask(Task task) {
        taskDatabase.getDao().updateTask(task);
        return true;
    }

    @Override
    public boolean deleteAllTasks() {
        taskDatabase.getDao().deleteAllTasks();
        return true;
    }

    @Override
    public boolean deleteTask(long id) {
        Task task = new Task();
        task.setId(id);
        taskDatabase.getDao().deleteTask(task);
        return true;
    }

    @Override
    public boolean authenticateUser(User user) {
        Log.i("Login", "Class: LocalTaskDatabaseOperation Method: authenticateUser");
        return true;
    }
}
