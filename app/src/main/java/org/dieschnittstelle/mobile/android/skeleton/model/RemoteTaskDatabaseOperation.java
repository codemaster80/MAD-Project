package org.dieschnittstelle.mobile.android.skeleton.model;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class RemoteTaskDatabaseOperation implements ITaskDatabaseOperation {

    public interface ToDoRESTWebAPI {
        @POST("/api/todos")
        Call<Task> createTask(@Body Task task);

        @GET("/api/todos")
        Call<List<Task>> readAllTasks();

        @GET("/api/todos/{todoId}")
        Call<Task> readTask(@Path("todoId") long id);

        @PUT("/api/todos/{todoId}")
        Call<Task> updateTask(@Path("todoId") long id, @Body Task task);

        @DELETE("/api/todos")
        Call<Boolean> deleteAllTasks();

        @DELETE("/api/todos/{todoId}")
        Call<Boolean> deleteTask(@Path("todoId") long id);

        @PUT("/api/users/auth")
        Call<Task> authenticateUser(@Body User user);
    }

    private final ToDoRESTWebAPI toDoRESTWebAPI;

    public RemoteTaskDatabaseOperation() {
        Retrofit retrofitBuilder = new Retrofit.Builder()
                // Android emulator
                .baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        toDoRESTWebAPI = retrofitBuilder.create(ToDoRESTWebAPI.class);
    }

    @Override
    public Task createTask(Task task) {
        try {
            return toDoRESTWebAPI.createTask(task).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Task> readAllTasks() {
        try {
            return toDoRESTWebAPI.readAllTasks().execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Task readTask(long id) {
        try {
            return toDoRESTWebAPI.readTask(id).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateTask(Task task) {
        try {
            toDoRESTWebAPI.updateTask(task.getId(), task).execute().body();
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteAllTasks() {
        try {
            return Boolean.TRUE.equals(toDoRESTWebAPI.deleteAllTasks().execute().body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteTask(long id) {
        try {
            return Boolean.TRUE.equals(toDoRESTWebAPI.deleteTask(id).execute().body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
