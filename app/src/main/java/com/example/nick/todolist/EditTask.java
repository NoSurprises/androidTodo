package com.example.nick.todolist;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.jar.Attributes;

public class EditTask extends AppCompatActivity {

    public static final int EDITING_FINISHED = 1111;
    public static final int EDITING_INTERRUPTED = 11111;
    public static final String NAME_FIELD = "name";
    public static final String ID_FIELD = "id";
    public static final String DATE_FIELD = "date";
    private static final String TAG = "daywint";



    EditText editText;
    Button done;
    CalendarView cal;
    Calendar deadlineDate;


    @Override
    public boolean onSupportNavigateUp() {
        setResult(EDITING_INTERRUPTED);
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

        // getting the data
        Intent intent = getIntent();
        String name = intent.getStringExtra(NAME_FIELD);
        final long id = intent.getLongExtra(ID_FIELD, -1);


        cal = ((CalendarView) findViewById(R.id.calendarView));
        editText = ((EditText) findViewById(R.id.editNameTask));
        done = (Button) findViewById(R.id.editingDone);
        deadlineDate = Calendar.getInstance();
        deadlineDate.set(Calendar.HOUR_OF_DAY, 23);
        deadlineDate.set(Calendar.MINUTE, 59);
        deadlineDate.set(Calendar.SECOND, 0);


        setText(name);


        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                deadlineDate.set(i, i1, i2);
            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent resultIntent = new Intent();

                String newTaskname = editText.getText().toString();
                long taskDate = cal.getDate();
                Log.d(TAG, "onClick in EditTask: task date is " + new Date(taskDate));
                resultIntent.putExtra(NAME_FIELD, newTaskname);
                resultIntent.putExtra(ID_FIELD, id);
                resultIntent.putExtra(DATE_FIELD, deadlineDate.getTime().getTime());

                setResult(EDITING_FINISHED, resultIntent);
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
    }
}
