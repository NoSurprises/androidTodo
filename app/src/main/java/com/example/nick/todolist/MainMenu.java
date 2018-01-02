package com.example.nick.todolist;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.nick.todolist.data.TodoDBHelper;
import com.example.nick.todolist.data.TodotaskContract;


public class MainMenu extends AppCompatActivity {

    public static final String TAG = "daywint";
    private String sortByPreference;
    private RecyclerView allTasksRecyclerView;
    private SQLiteDatabase db;
    private SharedPreferences sharedPreferences;
    private TodotaskAdapter todotaskAdapter;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Creating the recycler view with all tasks
        allTasksRecyclerView = (RecyclerView) findViewById(R.id.activities);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_recycler_view);

        setUpRefreshingGesture();
        connectToDb();
        getSharedPreferences();
        setUpSortingPreference();
        setUpRecyclerView();

        updateCountTodos();

        addSwipeGesturesToRecyclerView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshAdapterDataset();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.add: {

                long id = addNewTask();
                todotaskAdapter.startEditing(id);
                break;
            }

            case R.id.clear: {
                removeAllTasks();
                break;
            }
            case R.id.submenu_date_created: {
                setNewSortingPreference(TodotaskContract.TodoEntry.DATE_CREATED);
                refreshAdapterDataset();
                break;
            }
            case R.id.submenu_data_deadline: {
                setNewSortingPreference(TodotaskContract.TodoEntry.DATE_DEADLINE);
                refreshAdapterDataset();
                break;
            }
            case R.id.submenu_alphabetical: {
                setNewSortingPreference(TodotaskContract.TodoEntry.NAME);
                refreshAdapterDataset();
                break;
            }

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    void removeItem(long id) {
        db.delete(TodoDBHelper.TABLE_NAME, TodotaskContract.TodoEntry._ID + "=" + id, null);
        refreshAdapterDataset();
    }

    private void connectToDb() {
        SQLiteOpenHelper dbHelper = new TodoDBHelper(this);
        db = dbHelper.getWritableDatabase();
    }

    private void refreshAdapterDataset() {
        todotaskAdapter.swapCursor(getAllTasks());
    }

    private void addSwipeGesturesToRecyclerView() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {
                    int id = (int) viewHolder.itemView.getTag();
                    removeItem(id);
                }
            }
        }).attachToRecyclerView(allTasksRecyclerView);
    }

    private void setUpRecyclerView() {
        todotaskAdapter = new TodotaskAdapter(this, getAllTasks());
        allTasksRecyclerView.setAdapter(todotaskAdapter);
        allTasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void setUpSortingPreference() {
        sortByPreference = sharedPreferences.getString(TodoDBHelper.SORT_COLUMN, TodotaskContract.TodoEntry.DATE_CREATED);
    }

    private void setNewSortingPreference(String preference) {
        sortByPreference = preference;
        sharedPreferences.edit().putString(TodoDBHelper.SORT_COLUMN, sortByPreference).apply();
    }

    private void setUpRefreshingGesture() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                loadNewData();
                cancelAnimation();
            }

            private void cancelAnimation() {
                refreshLayout.setRefreshing(false);
            }

            private void loadNewData() {
                refreshAdapterDataset();
                updateCountTodos();
            }
        });
    }

    private void updateCountTodos() {
        ((TextView) findViewById(R.id.todos_count)).setText("Tasks " + todotaskAdapter.getItemCount());
    }

    private void removeAllTasks() {
        allTasksRecyclerView.removeAllViews();
        db.delete(TodoDBHelper.TABLE_NAME, null, null);
        refreshAdapterDataset();
    }

    private long addNewTask() {
        ContentValues cv = new ContentValues();
        cv.put(TodotaskContract.TodoEntry.NAME, "New task..");

        return db.insert(TodoDBHelper.TABLE_NAME, null, cv);
    }

    private Cursor getAllTasks() {
        // TODO make asynktaskLoader
        return db.query(TodoDBHelper.TABLE_NAME, null, null, null, null, null,
                sortByPreference + " COLLATE NOCASE");
    }
}

