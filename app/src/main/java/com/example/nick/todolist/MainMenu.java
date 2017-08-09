package com.example.nick.todolist;

import android.content.Context;
import android.icu.util.Calendar;
import android.icu.util.ChineseCalendar;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;


public class MainMenu extends AppCompatActivity {

    MenuItem add;
    MenuItem clear;
    MenuItem showAll;

    ArrayList<TodoTask> todos;

    LinearLayout activities;
    Context context;

    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        todos = new ArrayList<>();

        context = getApplicationContext();
        activities = (LinearLayout) findViewById(R.id.activities);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add: {

                TodoTask newTask = new TodoTask();
                View newElem = getLayoutInflater().inflate(R.layout.one_activity, activities);
                LinearLayout ll = ((LinearLayout) newElem);
                ConstraintLayout newTodo = (ConstraintLayout) ll.getChildAt(ll.getChildCount() - 1);


                EditText todoText = ((EditText) newTodo.getChildAt(1));

                todoText.setText(newTask.getTimeCreated().toString());
                todos.add(newTask);


                Toast.makeText(context, " Child count " + String.valueOf(todos.size()),
                        Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.clear: {

                todos = new ArrayList<>();
                activities.removeAllViews();
                break;

            }


            case R.id.showAll: {
                Toast.makeText(context, "not implemented yet", Toast.LENGTH_SHORT).show();
                break;

            }

        }
        return true;
    }
}
