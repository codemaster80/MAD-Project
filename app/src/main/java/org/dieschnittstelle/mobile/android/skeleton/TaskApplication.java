package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Application;

import org.dieschnittstelle.mobile.android.skeleton.model.ITaskDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.IUserDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.LocalTaskDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.LocalUserDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.MockTaskDatabaseOperation;
import org.dieschnittstelle.mobile.android.skeleton.model.RemoteTaskDatabaseOperation;

public class TaskApplication extends Application {

    private ITaskDatabaseOperation taskDatabaseOperation;
    private IUserDatabaseOperation userDatabaseOperation;

    @Override
    public void onCreate() {
        super.onCreate();
//        this.taskDatabaseOperation = new MockTaskDatabaseOperation();
//        this.taskDatabaseOperation = new LocalTaskDatabaseOperation(this);
        this.taskDatabaseOperation = new RemoteTaskDatabaseOperation();
        this.userDatabaseOperation = new LocalUserDatabaseOperation(this);
    }

    public ITaskDatabaseOperation getTaskDatabaseOperation() {
        return taskDatabaseOperation;
    }

    public void setTaskDatabaseOperation(ITaskDatabaseOperation taskDatabaseOperation) {
        this.taskDatabaseOperation = taskDatabaseOperation;
    }

    public IUserDatabaseOperation getUserDatabaseOperation() { return userDatabaseOperation; }

    public void setUserDatabaseOperation(IUserDatabaseOperation userDatabaseOperation) {
        this.userDatabaseOperation = userDatabaseOperation;
    }
}
