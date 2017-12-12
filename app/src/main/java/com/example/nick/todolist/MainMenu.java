package com.example.nick.todolist;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.nick.todolist.data.TodoDBHelper;
import com.example.nick.todolist.data.TodotaskContract;

import java.util.Date;


public class MainMenu extends AppCompatActivity {

    public static final String TAG = "daywint";
    private static final String CHECK_SHOWALL_PREF = "show_all";
    private RecyclerView mActivitiesRecyclerView;
    private SQLiteDatabase mDb;
    private SQLiteOpenHelper dbHelper;
    private SharedPreferences sp;
    private TodotaskAdapter mAdapter;
    static boolean showAllChecked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Creating the recycler view with all tasks
        mActivitiesRecyclerView = (RecyclerView) findViewById(R.id.activities);

        Log.d(TAG, "onCreate: connecting to mDb");
        // Connect to mDb
        dbHelper = new TodoDBHelper(this);
        mDb = dbHelper.getWritableDatabase();

        // Get the data from shared preferences.
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the cursor from database.
        Cursor cursor = getAllTasks();
        Log.d(TAG, "onCreate: in mDb found " + cursor.getCount());


        mAdapter = new TodotaskAdapter(this, cursor, mDb);
        mActivitiesRecyclerView.setAdapter(mAdapter);
        mActivitiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateCountTodos();
        
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        mAdapter.swapCursor(getAllTasks());

    }

    private void updateCountTodos() {
        // TODO make counter
        int countFinished = 0;

        ((TextView) findViewById(R.id.todos_count)).setText("Finished " + countFinished +"/"+mAdapter.getItemCount());
    }

    private Cursor getAllTasks() {
        // TODO make asynktaskLoader
        return mDb.query(TodoDBHelper.TABLE_NAME, null, null, null, null, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.add: {

                // TODO add value to the mDb
                ContentValues cv = new ContentValues();
                cv.put(TodotaskContract.TodoEntry.NAME, "New task.." );

                long id = mDb.insert(TodoDBHelper.TABLE_NAME, null, cv);
                Log.d(TAG, "onOptionsItemSelected: inserted id " + id);
                mAdapter.startEditing(id);
                break;
            }

            case R.id.clear: {
                mActivitiesRecyclerView.removeAllViews();
                mDb.delete(TodoDBHelper.TABLE_NAME, null, null);
                mAdapter.swapCursor();
                break;
            }

        }
        return true;
    }






}
