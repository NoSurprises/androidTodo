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

public class EditTask extends AppCompatActivity {

    private static final String TAG = "daywint";


    EditText editText;
    Button done;
    CalendarView cal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        cal = ((CalendarView) findViewById(R.id.calendarView));

        final SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy");

        editText = ((EditText) findViewById(R.id.editNameTask));
        done = (Button) findViewById(R.id.editingDone);

        // TODO add check;
        String name = getIntent().getStringExtra("name");
        setText(name);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent fromIntent = getIntent();
                int hash = getIntent().getIntExtra("hash", 0);

                Log.d(TAG, "Hash " + hash);
                Intent intent = new Intent();
                String newTaskname = editText.getText().toString();
                intent.putExtra("name", newTaskname); // TODO check user input
                String taskDate = sdf.format(cal.getDate());
                intent.putExtra("hash", hash);
                intent.putExtra("date", taskDate);

                Log.d(TAG, "Finishing editing task, setting the following extras: name " + newTaskname + ", " +
                        "hash " + hash + ", date " + taskDate);
                setResult(RESULT_OK, intent);
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
