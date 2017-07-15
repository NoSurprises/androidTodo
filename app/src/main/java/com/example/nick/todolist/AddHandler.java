package com.example.nick.todolist;

import android.content.Context;

import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Nick on 7/13/2017.
 */

public class AddHandler implements MenuItem.OnMenuItemClickListener {


    LinearLayout activities;
    Context context;
    int index = 0;


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        TextView newElem = new TextView(context);
        newElem.setText("Item is " + index++);
        activities.addView(newElem);
        return true;
    }

    public AddHandler(LinearLayout activities, Context context) {
        this.activities = activities;
        this.context = context;
    }
}
