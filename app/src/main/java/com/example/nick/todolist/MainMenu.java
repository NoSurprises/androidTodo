package com.example.nick.todolist;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainMenu extends AppCompatActivity {

    MenuItem add;
    MenuItem clear;
    MenuItem showAll;

    LinearLayout activities;
    Context context;

    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

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

                View newElem = getLayoutInflater().inflate(R.layout.one_activity, activities);
                LinearLayout ll
                        = ((LinearLayout) newElem);


                Toast.makeText(context, "Child count " + String.valueOf(ll.getChildCount()), Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.clear: {
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
