package com.example.nick.todolist;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.nick.todolist.data.TodoDBHelper;
import com.example.nick.todolist.data.TodotaskContract;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "daywint";
    private String sortByPreference;
    private RecyclerView allTasksRecyclerView;
    private SharedPreferences sharedPreferences;
    private TodotaskAdapter todotaskAdapter;
    private SwipeRefreshLayout refreshLayout;
    private TextView todosCountView;
    private TextView emptyMessage;
    private TodoDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Creating the recycler view with all tasks
        bindViews();

        setupRefreshingGesture();
        connectToDb();
        initSharedPreferences();

        initSortingPreference();
        setupRecyclerView();

        updateCountTodos();

        addSwipeGesturesToRecyclerView();
    }

    private void bindViews() {
        allTasksRecyclerView = (RecyclerView) findViewById(R.id.activities);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_recycler_view);
        todosCountView = ((TextView) findViewById(R.id.todos_count));
        emptyMessage = (TextView) findViewById(R.id.empty_list_message);
    }

    private void showEmptyMessage() {
        allTasksRecyclerView.setVisibility(View.GONE);
        todosCountView.setVisibility(View.GONE);
        emptyMessage.setVisibility(View.VISIBLE);
    }

    private void hideEmptyMessage() {
        allTasksRecyclerView.setVisibility(View.VISIBLE);
        todosCountView.setVisibility(View.VISIBLE);
        emptyMessage.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshAdapterDataset();
        updateCountTodos();
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
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(TodoDBHelper.TABLE_NAME, TodotaskContract.TodoEntry._ID + "=" + id, null);
        database.close();
        refreshAdapterDataset();
        updateCountTodos();

    }

    private void connectToDb() {
        dbHelper = new TodoDBHelper(this);
    }

    private void refreshAdapterDataset() {
        todotaskAdapter.swapCursor(dbHelper.getAllTasks(sortByPreference));
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

    private void setupRecyclerView() {
        todotaskAdapter = new TodotaskAdapter(this, dbHelper.getAllTasks(sortByPreference));
        allTasksRecyclerView.setAdapter(todotaskAdapter);
        allTasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void initSortingPreference() {
        sortByPreference = sharedPreferences.getString(TodoDBHelper.SORT_COLUMN, TodotaskContract.TodoEntry.DATE_CREATED);
    }

    private void setNewSortingPreference(String preference) {
        sortByPreference = preference;
        sharedPreferences.edit().putString(TodoDBHelper.SORT_COLUMN, sortByPreference).apply();
    }

    private void setupRefreshingGesture() {
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
        manageEmptyMessage();
        todosCountView.setText("Tasks " + todotaskAdapter.getItemCount());
    }

    private void manageEmptyMessage() {
        if (todotaskAdapter.getItemCount() == 0) {
            showEmptyMessage();
        } else if (allTasksRecyclerView.getVisibility() == View.GONE) {
            hideEmptyMessage();
        }
    }

    private void removeAllTasks() {
        allTasksRecyclerView.removeAllViews();
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(TodoDBHelper.TABLE_NAME, null, null);
        database.close();
        refreshAdapterDataset();
    }

    private long addNewTask() {
        ContentValues cv = new ContentValues();
        cv.put(TodotaskContract.TodoEntry.NAME, "New task..");
        updateCountTodos();


        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        long newTask = database.insert(TodoDBHelper.TABLE_NAME, null, cv);
        database.close();
        return newTask;
    }


}

