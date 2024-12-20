package org.dieschnittstelle.mobile.android.skeleton.viewmodel;

import android.view.inputmethod.EditorInfo;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.dieschnittstelle.mobile.android.skeleton.model.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TaskDetailViewModel extends ViewModel {
    private Task task;
    private MutableLiveData<Boolean> isTaskOnSave = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isTaskOnDelete = new MutableLiveData<>(false);
    private MutableLiveData<String> nameInputError = new MutableLiveData<>();
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY);

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void saveTask() {
        isTaskOnSave.setValue(true);
    }

    public MutableLiveData<Boolean> isTaskOnSave() {
        return isTaskOnSave;
    }

    public void deleteTask() { isTaskOnDelete.setValue(true); }

    public MutableLiveData<Boolean> isTaskOnDelete() {
        return isTaskOnDelete;
    }

    public boolean checkNameInputOnEnterKey(int keyId) {
        if (keyId == EditorInfo.IME_ACTION_NEXT || keyId == EditorInfo.IME_ACTION_DONE) {
            if (task.getName().length() <= 2) {
                this.nameInputError.setValue("Mission name is too short!");
                return true;
            }
        }
        return false;
    }

    public MutableLiveData<String> getNameInputError() {
        return nameInputError;
    }

    public boolean onNameInputChanged() {
        this.nameInputError.setValue(null);
        return false;
    }

    public String toDueDateString() {
        if (task.getExpiry() == null) {
            return "";
        }
        return DATE_FORMATTER.format(task.getExpiry()).split(" ")[0];
    }

    public String toTimeLimitString() {
        if (task.getExpiry() == null) {
            return "";
        }
        return DATE_FORMATTER.format(task.getExpiry()).split(" ")[1];
    }
}
