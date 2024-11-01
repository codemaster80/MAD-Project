package org.dieschnittstelle.mobile.android.skeleton.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Task implements Serializable {
    private UUID id;
    private String name;
    private String description;
    private boolean completed;

    public Task() {
    }

    public Task(String name, String description, boolean completed) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.completed = completed;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @NonNull
    @Override
    public String toString() {
        return this.getName();
    }
}
