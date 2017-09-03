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

import java.util.ArrayList;


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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "activity finished");
        if (data == null) {
            return;
        }

        // TODO! receiving null data from EditTask activity. Trying to edit a new task and record the new name of it;
        Log.d(TAG, "received " + data.getStringExtra("name"));
    }

    @Override
    public void removeTask(TodoTask task) {
        todos.remove(task);

    }

    boolean showFinishedTasks() {
        return !showAll.isChecked();
    }
}
