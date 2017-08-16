package com.example.nick.todolist;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.IdentityHashMap;

/**
 * Created by Nick on 8/9/2017.
 */

public class TodoTask {

    private Calendar cal = Calendar.getInstance();
    private ViewSwitcher viewSwitcher;

    private Context context;
    private ConstraintLayout todoObject;
    private Date timeCreated;
    private Date timeUpdated;
    private CustomListener listener = new CustomListener(this);
    private int completion = 0;
    private TextView textTimeCreated;


    public TodoTask(ConstraintLayout todoObject, Context context) {

        this.context = context;
        timeCreated = cal.getTime();
        timeUpdated = timeCreated;
        this.todoObject = todoObject;

        viewSwitcher = ((ViewSwitcher) todoObject.findViewById(R.id.switcher));
        viewSwitcher.showNext();
        ((EditText) viewSwitcher.findViewById(R.id.editText)).setOnFocusChangeListener(listener);
        viewSwitcher.setOnClickListener(listener);
        viewSwitcher.setOnLongClickListener(listener);
        SetText("New task..");
        textTimeCreated = (TextView) todoObject.findViewById(R.id.timeCreated);
        updateTimeCreated();

    }

    private void updateTimeCreated() {

        textTimeCreated.setText(getPeriod(new Date(), timeCreated));
    }

    public String getPeriod(Date first, Date second) {
        if (first.before(second)) {
            Date tmp = first;
            first = second;
            second = tmp;
        }
        Log.d("daywont", "first time " + first.toString());
        Log.d("daywont", "second time " + second.toString());
        long timeBetween = first.getTime() - second.getTime();
        // 1000 ms == 1 s
        // 60s = 1 min
        // 60 min = 1hour
        //24 hour == 1day

        Log.d("daywont", String.valueOf(timeBetween));
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

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeUpdated(Date date) {
        timeUpdated = date;
    }

    void SwitchEditingText() {
        viewSwitcher.showNext();
        TextView currentView = (TextView) viewSwitcher.getCurrentView();
        if (currentView instanceof EditText) {
            Log.d("daywont", "began editing");
            ((EditText) currentView).requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(((EditText) currentView), InputMethodManager.SHOW_IMPLICIT);

        }
        updateTimeCreated();
    }

    void SetText(String text) {
        ((TextView) viewSwitcher.getCurrentView()).setText(text);
        ((TextView) viewSwitcher.getNextView()).setText(text);
    }

    String getText() {
        return ((TextView) viewSwitcher.getCurrentView()).getText().toString();
    }


    void addCompletionPoint() {
        if (++completion > 2) {
            // task is finsished
            finishTask();

        }
    }

    void finishTask() {
        Log.d("daywont", "task " + getText() + " is finished");
    }
}

class CustomListener implements View.OnClickListener, View.OnLongClickListener,
        View.OnFocusChangeListener {
    TodoTask taskObject;

    CustomListener(TodoTask task) {

        taskObject = task;
    }

    @Override
    public void onClick(View view) {
        Log.d("daywont", "clicked on " + view.toString());
        taskObject.addCompletionPoint();

    }

    @Override
    public boolean onLongClick(View view) {

        Log.d("daywont", "long clicked on " + view.toString());

        taskObject.SwitchEditingText();
        return true;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        Log.d("daywont", "focus changed to " + b);

        if (!b) {
            taskObject.SetText(taskObject.getText());
            taskObject.SwitchEditingText();
        }
    }
}
