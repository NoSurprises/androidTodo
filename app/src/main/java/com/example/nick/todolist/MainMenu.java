package com.example.nick.todolist;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {

    MenuItem add;
    MenuItem clear;
    MenuItem showAll;

    AddHandler addHandler;
    LinearLayout activities;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        context = getApplicationContext();
        activities = (LinearLayout) findViewById(R.id.activities);
        addHandler = new AddHandler(activities, context);

        add = (MenuItem)findViewById(R.id.add);
        clear = (MenuItem)findViewById(R.id.clear);
        showAll = (MenuItem)findViewById(R.id.showAll);

        add.setOnMenuItemClickListener(addHandler);
    }


}
