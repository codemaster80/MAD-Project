package org.dieschnittstelle.mobile.android.skeleton;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityTaskDetailViewBinding;
import org.dieschnittstelle.mobile.android.skeleton.model.Task;
import org.dieschnittstelle.mobile.android.skeleton.util.DateConverter;
import org.dieschnittstelle.mobile.android.skeleton.viewmodel.TaskDetailViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TaskDetailViewActivity extends AppCompatActivity {
    private static final int READ_CONTACTS_REQUEST_CODE = 1111;
    protected static final String TASK_DETAIL_VIEW_KEY = "taskDetailViewObject";
    protected static final int RESULT_DELETE_OK = 99;
    private Task task;
    private TaskDetailViewModel viewModel;
    private Spinner taskPrioritySpinner;
    TextView taskDateTextView;
    private Button pickDateBtn;
    TextView taskTimeTextView;
    private Button pickTimeBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TaskDetailViewModel.class);
        if (viewModel.getTask() == null) {
            task = (Task) getIntent().getSerializableExtra(TASK_DETAIL_VIEW_KEY);
            if (task == null) {
                task = new Task();
            }

            viewModel.setTask(task);
            getContactInformations();
        }

        ActivityTaskDetailViewBinding taskDetailViewBinding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_task_detail_view
        );
        taskDetailViewBinding.setTaskDetailViewModel(this.viewModel);
        taskDetailViewBinding.setLifecycleOwner(this);

        pickDateBtn = findViewById(R.id.btnPickDueDate);
        taskDateTextView = findViewById(R.id.taskDate);
        setDueDate();

        pickTimeBtn = findViewById(R.id.btnPickTime);
        taskTimeTextView = findViewById(R.id.taskTime);
        setTimeLimit();

        taskPrioritySpinner = findViewById(R.id.dropdownPriority);
        setPriorityDropDown();

        this.viewModel.isTaskOnSave().observe(this, onSave -> {
            if (onSave) {
                String date = (String) pickDateBtn.getText();
                String time = (String) pickTimeBtn.getText();
                // DateFormat String 01.01.2025 01:00
                Long expiryLong = DateConverter.fromDateString(date + " " + time);
                task.setExpiry(expiryLong);
                Intent returnIntent = new Intent();
                returnIntent.putExtra(TASK_DETAIL_VIEW_KEY, task);
                this.setResult(TaskDetailViewActivity.RESULT_OK, returnIntent);
                this.finish();
            }
        });

        this.viewModel.isTaskOnDelete().observe(this, onDelete -> {
            if (onDelete) {
                deleteAlertDialog();
            }
        });

        // setup listview from selected contacts of the task
        final ListView selectedContacts = findViewById(R.id.selectedContacts);
        ArrayAdapter<String> selectedContactsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, viewModel.getTask().getContacts());
        selectedContacts.setAdapter(selectedContactsAdapter);

        // setup spinner from available contacts
        final Spinner contactsSpinner = findViewById(R.id.contactsDropdown);
        ArrayAdapter<String> contactAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, viewModel.getAvailableContacts());
        contactsSpinner.setAdapter(contactAdapter);
        contactsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) { return; }
                String selectedContact = contactsSpinner.getSelectedItem().toString();

                if (viewModel.getTask().getContacts().stream().noneMatch(contact -> Objects.equals(contact, selectedContact))) {
                    viewModel.getTask().getContacts().add(selectedContact);
                }

                contactsSpinner.setSelection(0, false);
                selectedContactsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void deleteAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(android.R.layout.select_dialog_item);
        builder.setMessage("Delete Mission \"" + task.getName() + "\"?");
        builder.setPositiveButton("Delete", (dialog, id) -> deleteIntent());
        builder.setNegativeButton("Cancel", (dialog, id) -> {
           // do nothing
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteIntent() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(TASK_DETAIL_VIEW_KEY, task);
        this.setResult(TaskDetailViewActivity.RESULT_DELETE_OK, returnIntent);
        this.finish();
    }

    private void setDueDate() {
        pickDateBtn.setOnClickListener(view -> {
            Calendar calendar;
            int currentYear, currentMonth, currentDay;
            DatePickerDialog datePickerDialog;

            if (task.getExpiry() == 0) {
                calendar = Calendar.getInstance();
                currentYear = calendar.get(Calendar.YEAR);
                currentMonth = calendar.get(Calendar.MONTH);
                currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            } else {
                String dateStr = DateConverter.toDateString(task.getExpiry()).split(" ")[0];
                currentDay = Integer.parseInt(dateStr.split("\\.")[0]);
                // Month of Calendar.java starts from 0
                currentMonth = Integer.parseInt(dateStr.split("\\.")[1]) - 1;
                currentYear = Integer.parseInt(dateStr.split("\\.")[2]);
            }
            datePickerDialog = setDatePickerDialog(currentYear, currentMonth, currentDay);
            datePickerDialog.show();
        });
    }

    @NonNull
    private DatePickerDialog setDatePickerDialog(int currentYear, int currentMonth, int currentDay) {
        return new DatePickerDialog(
                this,
                (datePickerView, selectedYear, selectedMonth, selectedDay) -> {
                    // Month of Calendar.java starts from 0
                    String dueDateStr = to2Digits(selectedDay) + "." + to2Digits(selectedMonth + 1) + "." + selectedYear;
                    pickDateBtn.setText(dueDateStr);
                },
                currentYear,
                currentMonth,
                currentDay
        );
    }

    private void setTimeLimit() {
        pickTimeBtn.setOnClickListener(view -> {
            Calendar calendar;
            int currentHour, currentMinute;
            TimePickerDialog timePickerDialog;

            if(task.getExpiry() == 0) {
                calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar.get(Calendar.MINUTE);
            } else {
                String timeStr = DateConverter.toDateString(task.getExpiry()).split(" ")[1];
                currentHour = Integer.parseInt(timeStr.split(":")[0]);
                currentMinute = Integer.parseInt(timeStr.split(":")[1]);
            }
            timePickerDialog = setTimePickerDialog(currentHour, currentMinute);
            timePickerDialog.show();
        });
    }

    private TimePickerDialog setTimePickerDialog(int currentHour, int currentMinute) {
        return new TimePickerDialog(
                this,
                (timePickerView, selectedHour, selectedMinute) -> {
                    // convert single digit time to double digit. e.g.: 9h 0m -> 09:00
                    String timeLimitStr = to2Digits(selectedHour) + ":" + to2Digits(selectedMinute);
                    pickTimeBtn.setText(timeLimitStr);
                },
                currentHour,
                currentMinute,
                true
        );
    }

    private void setPriorityDropDown() {
        Task.Priority currentPriority = task.getPriority();
        List<String> priorities = Arrays.asList(
                Task.Priority.NONE.name(),
                Task.Priority.LOW.name(),
                Task.Priority.NORMAL.name(),
                Task.Priority.HIGH.name(),
                Task.Priority.CRITICAL.name()
        );
        if (!currentPriority.equals(Task.Priority.NONE)) {
            priorities.set(0, currentPriority.name());
            priorities = priorities.stream().distinct().collect(Collectors.toList());
        }
        ArrayAdapter<String> dropDownPriorityAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item, priorities);
        dropDownPriorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskPrioritySpinner.setAdapter(dropDownPriorityAdapter);

        taskPrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                String selectedPriority = taskPrioritySpinner.getSelectedItem().toString();
                task.setPriority(Task.Priority.valueOf(selectedPriority));
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private String to2Digits(int number) {
        if (number < 10) {
            return "0" + number;
        }
        return String.valueOf(number);
    }

    private void getContactInformations() {
        if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_REQUEST_CODE);
        } else {
            setupContactList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_CONTACTS_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupContactList();
        }
    }

    private void setupContactList() {
        viewModel.getAvailableContacts().addAll(queryContacts());
    }

    @SuppressLint("Range")
    private ArrayList<String> queryContacts() {

        // https://stackoverflow.com/a/12562234
        ArrayList<String> contacts = new ArrayList<>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur != null && pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contacts.add(name);
                    }

                    if (pCur != null) {
                        pCur.close();
                    }
                }
            }
        }

        if (cur != null) {
            cur.close();
        }

        return contacts;
    }

}