package com.example.nick.todolist;


import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nick on 8/9/2017.
 */

class TodoTask {

    private ArrayList<RemoveTask> removeListeners;
    private static ArrayList<TodoTask> allTasks;
    private AppCompatActivity activity;
    private ConstraintLayout todoObject;
    private Date timeCreated;

    private final String TAG = "daywint";
    private int completion = 0;
    private TextView textTimeCreated;
    private TextView textOfTask;
    private boolean finished;

    static {
        allTasks = new ArrayList<>();
    }

    TodoTask(ConstraintLayout todoObject, final AppCompatActivity appCompatActivity) {
        this.todoObject = todoObject;
        this.activity = appCompatActivity;


        allTasks.add(this);
        textOfTask = todoObject.findViewById(R.id.textView);

        removeListeners = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        timeCreated = cal.getTime();
        // timeUpdated = timeCreated;
        
        setText("New task..");
        textTimeCreated = todoObject.findViewById(R.id.timeCreated);
        updateTimeCreated();



        textOfTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCompletionPoint();
            }
        });


        textOfTask.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                // create popupMenu

                PopupMenu popupMenu = new PopupMenu(activity, view);
                popupMenu.inflate(R.menu.item_context_menu);

                final TodoTask taskObject = findTaskByText(((TextView) view));
                if (taskObject == null){
                    return false;
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                Log.d(TAG, "editing item " + taskObject.textOfTask.getText());
                                switchToEditingActivity();
                                break;
                            case R.id.remove:
                                Log.d(TAG, "removing item " + taskObject);
                                // removing task
                                taskObject.removeTask();

                                break;

                            default:
                                return false;
                        }
                        return true;
                    }
                });

                popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu menu) {
                        updateTimeCreated();

                    }
                });
                popupMenu.show();

                return true;
            }
        });

        todoObject.findViewById(R.id.checkBox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFinished()) {
                    finishTask();
                } else {
                    resetTask();
                }
            }
        });


        switchToEditingActivity();

    }

    private void switchToEditingActivity() {
        Intent intent = new Intent(activity, EditTask.class);
        activity.startActivityForResult(intent, 1);


    }

    void setOnRemoveTask(RemoveTask listener) {
        removeListeners.add(listener);
    }

    void removeTask() {

        allTasks.remove(this);
        ((LinearLayout) (activity.findViewById(R.id.activities))).removeView(((View) textOfTask.getParent()));
        for (RemoveTask removeListener : removeListeners) {
            removeListener.removeTask(this);
        }

    }

    private TodoTask findTaskByText(TextView view) {
        int index = 0;
        for (TodoTask createdTask : allTasks) {
            if (createdTask.textOfTask.equals(view)) {
                Log.d(TAG, "task found on " + index + "place out of " + allTasks.size());
                return createdTask;

            }
            index++;
        }
        return null;
    }

    private void resetTask() {
        ((CheckBox) todoObject.findViewById(R.id.checkBox)).setChecked(false);
        textOfTask.setPaintFlags(textOfTask.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        finished = false;
    }

    private void updateTimeCreated() {

        textTimeCreated.setText(getTimePeriod(new Date(), timeCreated));
    }

    private String getTimePeriod(Date first, Date second) {
        if (first.before(second)) {
            Date tmp = first;
            first = second;
            second = tmp;
        }
        long timeBetween = first.getTime() - second.getTime();
        // 1000 ms == 1 s
        // 60s = 1 min
        // 60 min = 1hour
        //24 hour == 1day

        timeBetween /= 1000;
        if (timeBetween < 60) {
            return timeBetween + "s";
        } else if (timeBetween < 2400) {
            return timeBetween / 60 + "m";
        } else if (timeBetween / 2400 < 24) {
            return timeBetween / 60 / 60 + "h";
        }
        return timeBetween / 2400 / 60 + "d";


    }


    private void setText(String text) {
        textOfTask.setText(text);
    }

    private void addCompletionPoint() {
        if (completion < 3) {
            completion++;

        } else {
            finishTask();
        }
    }

    private void finishTask() {
        ((CheckBox) todoObject.findViewById(R.id.checkBox)).setChecked(true);
        textOfTask.setPaintFlags(textOfTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        finished = true;

        if (((MainMenu) activity).showFinishedTasks()) {
            hide();
        }

    }

    boolean isFinished() {
        return finished;
    }

    void hide() {
        // TODO add animation
        todoObject.setVisibility(View.GONE);

    }

    void show() {

        todoObject.setVisibility(View.VISIBLE);
    }
}


interface RemoveTask {
    void removeTask(TodoTask task);
}