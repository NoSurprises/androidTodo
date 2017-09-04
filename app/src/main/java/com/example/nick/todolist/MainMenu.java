package com.example.nick.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainMenu extends AppCompatActivity implements RemoveTask {


    private static final String TAG = "daywint";
    MenuItem showAll;

    Menu optionsMenu;

    ArrayList<TodoTask> todos;

    LinearLayout activities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        todos = new ArrayList<>();


        activities = (LinearLayout) findViewById(R.id.activities);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu, menu);
        optionsMenu = menu;
        showAll = menu.findItem(R.id.showAll);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.add: {

                //inflating the list with the new taskTodo
                getLayoutInflater().inflate(R.layout.one_activity, activities);

                //creating the object of the new task
                TodoTask newTask = new TodoTask(
                        (ConstraintLayout) activities.getChildAt(activities.getChildCount() - 1),
                        this);

                newTask.setOnRemoveTask(this);
                todos.add(newTask);
                break;
            }

            case R.id.clear: {

                todos = new ArrayList<>();
                activities.removeAllViews();
                break;

            }


            case R.id.showAll: {

                showAll.setChecked(!showAll.isChecked());
                if (!showAll.isChecked()) {
                    for (TodoTask todo : todos) {
                        if (todo.isFinished()) {
                            todo.hide();
                        }

                    }
                } else {
                    for (TodoTask todo : todos) {
                        todo.show();

                    }
                }

                break;

            }
            case R.id.removeFinished:

                try {
                    int count = 0;
                    for (TodoTask todo : todos) {
                        if (todo.isFinished()) {
                            count++;
                        }
                    }
                    TodoTask[] toRemove = new TodoTask[count];
                    count = 0;
                    for (TodoTask todo : todos) {

                        if (todo.isFinished()) {
                            toRemove[count++] = todo;
                        }
                    }
                    for (TodoTask task : toRemove) {
                        task.removeTask();
                    }
                    break;
                }
                catch (Exception e) {
                    Log.d(TAG, " " + e);
                }

        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        for (TodoTask todo : todos) {
            // TODO rename method
            todo.updateTimeCreated();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "activity finished");
        if (data == null) {
            return;
        }


        Log.d(TAG, "in MainMenu: hash " + data.getExtras().get("hash"));

        // TODO add check.
        String taskName = data.getExtras().get("name").toString();
        String hash = data.getExtras().get("hash").toString();
        String date = data.getExtras().get("date").toString();
        Log.d(TAG, "date is " + date);


        TodoTask changedTask = null;

        for (TodoTask todo : todos) {
            if (String.valueOf(todo.hashCode()).equals(hash)) {
                changedTask = todo;
            }
        }
        if (changedTask != null) {
            changedTask.setText(taskName);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM;yyyy");

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            c.setTime(new Date(System.currentTimeMillis()));
        }
        c.add(Calendar.DATE, 1); // adding one day
        changedTask.setDeadlineDate(c.getTime());



    }

    @Override
    public void removeTask(TodoTask task) {
        todos.remove(task);

    }

    boolean showFinishedTasks() {
        return !showAll.isChecked();
    }
}
