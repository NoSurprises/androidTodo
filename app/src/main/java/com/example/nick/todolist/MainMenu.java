package com.example.nick.todolist;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.nick.todolist.data.TodoDBHelper;
import com.example.nick.todolist.data.TodotaskContract;


public class MainMenu extends AppCompatActivity {

    public static final String TAG = "daywint";
    private static String sortByPreference;
    private RecyclerView mActivitiesRecyclerView;
    private SQLiteDatabase mDb;
    private SharedPreferences sp;
    private TodotaskAdapter mAdapter;

    public static String getSortingPreference() {
        return sortByPreference;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Creating the recycler view with all tasks
        mActivitiesRecyclerView = (RecyclerView) findViewById(R.id.activities);

        Log.d(TAG, "onCreate: connecting to mDb");
        // Connect to mDb
        SQLiteOpenHelper dbHelper = new TodoDBHelper(this);
        mDb = dbHelper.getWritableDatabase();

        // Get the data from shared preferences.
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        // Get sorting preference.
        sortByPreference = sp.getString(TodoDBHelper.SORT_COLUMN, TodotaskContract.TodoEntry.DATE_CREATED);

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
        mAdapter.swapCursor(getAllTasks());

    }

    private void updateCountTodos() {
        ((TextView) findViewById(R.id.todos_count)).setText("Tasks " + mAdapter.getItemCount());
    }

    private Cursor getAllTasks() {
        // TODO make asynktaskLoader
        return mDb.query(TodoDBHelper.TABLE_NAME, null, null, null, null, null, sortByPreference);
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

                ContentValues cv = new ContentValues();
                cv.put(TodotaskContract.TodoEntry.NAME, "New task..");
                long id = mDb.insert(TodoDBHelper.TABLE_NAME, null, cv);
                Log.d(TAG, "onOptionsItemSelected: inserted id " + id);
                mAdapter.startEditing(id);
                break;
            }

            case R.id.clear: {
                mActivitiesRecyclerView.removeAllViews();
                mDb.delete(TodoDBHelper.TABLE_NAME, null, null);
                mAdapter.swapCursor(getAllTasks());
                break;
            }
            case R.id.app_bar_switch: {
                // TODO: 12/12/2017 add sort menu item
                sp.edit().putString(TodoDBHelper.SORT_COLUMN, TodotaskContract.TodoEntry.DATE_DEADLINE).apply();
                break;
            }
        }
        return true;
    }
}

