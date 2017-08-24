package com.example.nick.todolist;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
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

class TodoTask {

    private Calendar cal;
    private ViewSwitcher viewSwitcher;

    private int[] completionColor;
    private Context context;
    private ConstraintLayout todoObject;
    private Date timeCreated;
    private Date timeUpdated;
    private int completion = 0;
    private TextView textTimeCreated;
    private TextView textOfTask;
    private boolean finished;


    TodoTask(ConstraintLayout todoObject, Context context) {

        completionColor = new int[3];

        completionColor[0] = Color.parseColor("#155015");
        completionColor[1] = Color.parseColor("#15AA15");
        completionColor[2] = Color.parseColor("#15FF15");

        this.context = context;
        timeCreated = cal.getTime();
        timeUpdated = timeCreated;
        this.todoObject = todoObject;

        viewSwitcher = ((ViewSwitcher) todoObject.findViewById(R.id.switcher));
        viewSwitcher.showNext();
        ((EditText) viewSwitcher.findViewById(R.id.editText)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {

                if (!isFocused) {
                    SetText(((EditText) view).getText().toString());
                    SwitchEditingText();
                }
            }
        });
        viewSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCompletionPoint();
            }
        });


        viewSwitcher.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                SwitchEditingText();
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
        cal = Calendar.getInstance();
        SetText("New task..");
        textTimeCreated = (TextView) todoObject.findViewById(R.id.timeCreated);
        updateTimeCreated();

        textOfTask = (TextView) viewSwitcher.findViewById(R.id.textView);

    }

    private void resetTask() {
        ((CheckBox) todoObject.findViewById(R.id.checkBox)).setChecked(false);
        textOfTask.setPaintFlags(textOfTask.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        finished = false;
    }

    private void updateTimeCreated() {

        textTimeCreated.setText(getPeriod(new Date(), timeCreated));
    }

    private String getPeriod(Date first, Date second) {
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

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeUpdated(Date date) {
        timeUpdated = date;
    }

    private void SwitchEditingText() {
        viewSwitcher.showNext();
        TextView currentView = (TextView) viewSwitcher.getCurrentView();
        if (currentView instanceof EditText) {
            ((EditText) currentView).requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(((EditText) currentView), InputMethodManager.SHOW_IMPLICIT);

        }
        updateTimeCreated();
    }

    private void SetText(String text) {
        ((TextView) viewSwitcher.getCurrentView()).setText(text);
        ((TextView) viewSwitcher.getNextView()).setText(text);
    }

    String getText() {
        return textOfTask.getText().toString();
    }

    private void addCompletionPoint() {
        if (completion < 3) {

            textOfTask.setTextColor(completionColor[completion]);
            completion++;

        } else {
            finishTask();
        }
    }

    private void finishTask() {
        ((CheckBox) todoObject.findViewById(R.id.checkBox)).setChecked(true);
        textOfTask.setPaintFlags(textOfTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        finished = true;
    }

    boolean isFinished() {
        return finished;
    }

    void hide() {
        todoObject.setVisibility(View.GONE);

    }

    void show() {

        todoObject.setVisibility(View.VISIBLE);
    }
}


