package com.example.nick.todolist;

import android.content.Context;
import android.content.Intent;
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
import java.util.jar.Attributes;

public class EditTask extends AppCompatActivity {

    public static final int EDITING_FINISHED = 1111;
    public static final String NAME_FIELD = "name";
    public static final String ID_FIELD = "id";
    public static final String DATE_FIELD = "date";
    private static final String TAG = "daywint";



    EditText editText;
    Button done;
    CalendarView cal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        // getting the data
        Intent intent = getIntent();
        String name = intent.getStringExtra(NAME_FIELD);
        final long id = intent.getLongExtra(ID_FIELD, -1);


        cal = ((CalendarView) findViewById(R.id.calendarView));

        final SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy");

        editText = ((EditText) findViewById(R.id.editNameTask));
        done = (Button) findViewById(R.id.editingDone);

        // TODO add check
        setText(name);



        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent resultIntent = new Intent();

                String newTaskname = editText.getText().toString();
                String taskDate = sdf.format(cal.getDate());

                resultIntent.putExtra(NAME_FIELD, newTaskname);
                resultIntent.putExtra(ID_FIELD, id);
                resultIntent.putExtra(DATE_FIELD, taskDate);

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

    public void setText(String text) {
        editText.setText(text);
    }
}
