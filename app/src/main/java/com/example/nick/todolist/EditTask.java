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
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import com.example.nick.todolist.data.TodoDBHelper;
import com.example.nick.todolist.data.TodotaskContract;

import java.util.Calendar;
import java.util.Date;

public class EditTask extends AppCompatActivity {

    private static final String TAG = "daywint";



    EditText editText;
    Button done;
    CalendarView cal;
    Calendar deadlineDate;
    private SQLiteDatabase mDb;
    private String mName;
    private ContentValues mCv;
    private long mDeadline;
    private long mId;


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TodoDBHelper dbHelper = new TodoDBHelper(this);
        mDb = dbHelper.getWritableDatabase();

        // getting the data
        Intent intent = getIntent();
        mId = intent.getLongExtra(TodotaskContract.TodoEntry._ID, -1);



        cal = ((CalendarView) findViewById(R.id.calendarView));
        editText = ((EditText) findViewById(R.id.editNameTask));
        done = (Button) findViewById(R.id.editingDone);
        deadlineDate = Calendar.getInstance();


        Cursor cursor = mDb.query(false, TodoDBHelper.TABLE_NAME, null,
                TodotaskContract.TodoEntry._ID + "=" + mId, null, null, null, null, null);

        Log.d(TAG, "onCreate: found " + cursor);
        Log.d(TAG, "onCreate: size " + cursor.getCount() +" with id " + mId );

        cursor.moveToFirst();
        mName = cursor.getString(cursor.getColumnIndex(TodotaskContract.TodoEntry.NAME));
        mDeadline = cursor.getLong(cursor.getColumnIndex(TodotaskContract.TodoEntry.DATE_DEADLINE));
        cursor.close();


        deadlineDate.setTime(new Date(mDeadline));
        deadlineDate.set(Calendar.HOUR_OF_DAY, 23);
        deadlineDate.set(Calendar.MINUTE, 59);
        deadlineDate.set(Calendar.SECOND, 0);
        cal.setDate(deadlineDate.getTime().getTime());

        mCv = new ContentValues();
        mCv.put(TodotaskContract.TodoEntry.NAME, mName);
        mCv.put(TodotaskContract.TodoEntry.DATE_DEADLINE, mDeadline);
        mCv.put(TodotaskContract.TodoEntry._ID, mId);
        setText(mName);


        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                deadlineDate.set(i, i1, i2);
                mDeadline =deadlineDate.getTime().getTime();
                mCv.put(TodotaskContract.TodoEntry.DATE_DEADLINE, mDeadline);
            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mName = editText.getText().toString();
                mCv.put(TodotaskContract.TodoEntry.NAME, mName);
                mDb.update(TodoDBHelper.TABLE_NAME, mCv, TodotaskContract.TodoEntry._ID+"="+mId,null);
                finish();
            }
        });




        // show keyboard
        editText.requestFocus();
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.toggleSoftInput(InputMethodManager.SHOW_FORCED,  InputMethodManager.HIDE_IMPLICIT_ONLY);
        editText.selectAll();



    }

    @Override
    public void finish() {
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        super.finish();

    }

    public void setText(String text) {
        editText.setText(text);
        mName = text;
    }
}
