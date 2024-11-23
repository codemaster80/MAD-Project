package org.dieschnittstelle.mobile.android.skeleton.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.dieschnittstelle.mobile.android.skeleton.R;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class Task implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String description;
    private String date;
    private String time;
    private boolean completed;
    private boolean favorite;
    private String priority = "NONE";

    public Task() {
    }

    public Task(String name, String description, String date, String time, boolean completed, boolean favorite, String priority) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.completed = completed;
        this.favorite = favorite;
        this.priority = priority;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
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

    public int toPriorityColorResource() {
        switch (this.priority.toUpperCase()) {
            case "CRITICAL":
                return R.color.colorTaskPrioCritical;
            case "HIGH":
                return R.color.colorTaskPrioHigh;
            case "NORMAL":
                return R.color.colorTaskPrioNormal;
            case "LOW":
                return R.color.colorTaskPrioLow;
            default:
                return R.color.colorTaskPrioNone;
        }
    }
}
