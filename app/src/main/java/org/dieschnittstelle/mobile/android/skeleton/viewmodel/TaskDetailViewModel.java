package org.dieschnittstelle.mobile.android.skeleton.viewmodel;

import android.view.inputmethod.EditorInfo;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.dieschnittstelle.mobile.android.skeleton.model.Task;
import org.dieschnittstelle.mobile.android.skeleton.util.DateConverter;

import java.util.ArrayList;
import java.util.List;

public class TaskDetailViewModel extends ViewModel {
    private Task task;
    private final MutableLiveData<Boolean> isTaskOnSave = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isTaskOnDelete = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isContactOnDelete = new MutableLiveData<>(false);
    private final MutableLiveData<String> nameInputError = new MutableLiveData<>();
    private ArrayList<String> availableContacts = new ArrayList<>(List.of("Select a contact..."));

    public ArrayList<String> getAvailableContacts() { return availableContacts; }

    public void setAvailableContacts(ArrayList<String> availableContacts) { this.availableContacts = availableContacts; }

    public Task getTask() { return task; }

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

    public void deleteContact(String contact) {
        task.getContacts().remove(contact);
        isContactOnDelete.setValue(true);
    }

    public MutableLiveData<Boolean> isContactOnDelete() {
        return isContactOnDelete;
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
        String dateTime = DateConverter.toDateString(task.getExpiry());
        if (dateTime.isBlank()) {
            return dateTime;
        }
        return dateTime.split(" ")[0];
    }

    public String toTimeLimitString() {
        String dateTime = DateConverter.toDateString(task.getExpiry());
        if (dateTime.isBlank()) {
            return dateTime;
        }
        return dateTime.split(" ")[1];
    }
}
