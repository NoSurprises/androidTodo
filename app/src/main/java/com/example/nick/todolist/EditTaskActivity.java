package com.example.nick.todolist;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import com.example.nick.todolist.data.TodoDBHelper;
import com.example.nick.todolist.data.TodotaskContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditTaskActivity extends AppCompatActivity {

    private static final String TAG = "EditTask";

    private EditText taskText;
    private Button done;
    private CalendarView cal;
    private Calendar deadlineDate;
    private SQLiteDatabase mDb;
    private String taskName;
    private ContentValues contentValues;
    private String deadline;
    private long id;
    private SimpleDateFormat databaseTimeFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        displayHomeButton();
        connectToDatabase();

        getDataFromIntent();
        bindDataToViews();

        // Content values will help to store changed data to database
        setUpContentValues();

        setCalendarChangeListener();
        setDoneClickListener();

        showKeyboard();
    }

    @Override
    protected void onStop() {
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        super.onStop();
    }

    private void setDoneClickListener() {
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskName = taskText.getText().toString();
                contentValues.put(TodotaskContract.TodoEntry.NAME, taskName);
                updateTaskInDatabase();
                finish();
            }
        });
    }

    private void setCalendarChangeListener() {
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                deadlineDate.set(i, i1, i2);
                deadline = databaseTimeFormat.format(deadlineDate.getTime());
                contentValues.put(TodotaskContract.TodoEntry.DATE_DEADLINE, deadline);
            }
        });
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();

        id = intent.getLongExtra(TodotaskContract.TodoEntry._ID, -1);
        cal = ((CalendarView) findViewById(R.id.calendarView));
        taskText = ((EditText) findViewById(R.id.editNameTask));
        done = (Button) findViewById(R.id.editingDone);
        deadlineDate = Calendar.getInstance();
    }

    private void bindDataToViews() {
        setTasknameAndDeadline();
        bindTaskNameToView();

        parseDeadline();
        removeTimestampFromDeadline();
        setUpCalendarData();
    }

    private void setUpContentValues() {
        createContentValues();
        addNameDeadlineIdToContentValues();
    }

    private void addNameDeadlineIdToContentValues() {
        contentValues.put(TodotaskContract.TodoEntry.NAME, taskName);
        contentValues.put(TodotaskContract.TodoEntry.DATE_DEADLINE, databaseTimeFormat.format(deadlineDate.getTime()));
        contentValues.put(TodotaskContract.TodoEntry._ID, id);
    }

    private void setUpCalendarData() {
        cal.setDate(deadlineDate.getTime().getTime());
    }

    private void parseDeadline() {
        try {
            deadlineDate.setTime(databaseTimeFormat.parse(deadline));
        } catch (ParseException e) {
            // Set default value - today
            deadlineDate.setTime(new Date());
        }
    }

    private void removeTimestampFromDeadline() {
        deadlineDate.set(Calendar.HOUR_OF_DAY, 23);
        deadlineDate.set(Calendar.MINUTE, 59);
        deadlineDate.set(Calendar.SECOND, 0);
    }

    private void setTasknameAndDeadline() {
        Cursor cursor = getTaskToEditFromDatabase();
        cursor.moveToFirst();

        taskName = cursor.getString(cursor.getColumnIndex(TodotaskContract.TodoEntry.NAME));
        deadline = cursor.getString(cursor.getColumnIndex(TodotaskContract.TodoEntry.DATE_DEADLINE));

        cursor.close();
    }

    private void updateTaskInDatabase() {
        mDb.update(TodoDBHelper.TABLE_NAME, contentValues, TodotaskContract.TodoEntry._ID + "=" + id, null);
    }

    private void bindTaskNameToView() {
        taskText.setText(taskName);

    }

    private void connectToDatabase() {
        TodoDBHelper dbHelper = new TodoDBHelper(this);
        mDb = dbHelper.getWritableDatabase();
    }

    private Cursor getTaskToEditFromDatabase() {
        return mDb.query(false, TodoDBHelper.TABLE_NAME, null,
                TodotaskContract.TodoEntry._ID + "=" + id, null, null, null, null, null);
    }

    private void displayHomeButton() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void showKeyboard() {
        taskText.requestFocus();
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        taskText.selectAll();
    }

    private void createContentValues() {
        contentValues = new ContentValues();
    }
}
