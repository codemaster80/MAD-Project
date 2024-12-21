package org.dieschnittstelle.mobile.android.skeleton.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.dieschnittstelle.mobile.android.skeleton.R;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class Task implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String description;
    private Long expiry;
    @SerializedName("done")
    private boolean completed;
    @SerializedName("favourite")
    private boolean favorite;
    private Priority priority = Priority.NONE;

    public Task() {
    }

    public Task(String name, String description, Long expiry, boolean completed, boolean favorite, Priority priority) {
        this.name = name;
        this.description = description;
        this.expiry = expiry;
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

    public Long getExpiry() {
        return expiry;
    }

    public void setExpiry(Long expiry) {
        this.expiry = expiry;
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

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
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

    public enum Priority {
        CRITICAL(R.color.colorTaskPrioCritical),
        HIGH(R.color.colorTaskPrioHigh),
        NORMAL(R.color.colorTaskPrioNormal),
        LOW(R.color.colorTaskPrioLow),
        NONE(R.color.colorTaskPrioNone);

        public final int resourceId;

        Priority(int resourceId) {
            this.resourceId = resourceId;
        }
    }
}
