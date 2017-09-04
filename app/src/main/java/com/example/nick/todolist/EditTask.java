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

public class EditTask extends AppCompatActivity {

    private static final String TAG = "daywint";

    Bundle fromBundle;

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

        fromBundle = getIntent().getExtras();
        // TODO add check;
        String name = fromBundle.get("name").toString();
        setText(name);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String hash = fromBundle.get("hash").toString();

                Intent intent = new Intent();
                intent.putExtra("name", editText.getText()); // TODO check user input

                intent.putExtra("hash", hash);
                intent.putExtra("date", sdf.format(cal.getDate()));


                setResult(RESULT_OK, intent);
                finish();

            }
        });



        editText.requestFocus();
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.toggleSoftInput(InputMethodManager.SHOW_FORCED,  InputMethodManager.HIDE_IMPLICIT_ONLY);
        editText.selectAll();


    }

    public void setText(String text) {
        editText.setText(text);
    }
}
