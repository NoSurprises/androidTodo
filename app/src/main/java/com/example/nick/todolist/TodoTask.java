package com.example.nick.todolist;

import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

    private ConstraintLayout todoObject;
    private Date timeCreated;
    private Date timeUpdated;
    CustomListener listener = new CustomListener(this);
    int completion = 0;


    public TodoTask(ConstraintLayout todoObject) {

        timeCreated = cal.getTime();
        timeUpdated = timeCreated;
        this.todoObject = todoObject;

        viewSwitcher = ((ViewSwitcher) todoObject.findViewById(R.id.switcher));
        viewSwitcher.showNext();
        ((EditText) viewSwitcher.findViewById(R.id.editText)).setOnFocusChangeListener(listener);
        viewSwitcher.setOnClickListener(listener);
        viewSwitcher.setOnLongClickListener(listener);
        SetText(getTimeCreated().toString());

    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeUpdated(Date date) {
        timeUpdated = date;
    }

    void SwitchEditingText() {
        viewSwitcher.showNext();
    }

    void SetText(String text) {
        Log.d("daywont", "set text to " + text);
        Log.d("daywont", "current view " + viewSwitcher.getCurrentView().toString());
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
