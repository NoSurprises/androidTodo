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
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class MainMenu extends AppCompatActivity {

    public static final String TAG = "daywint";
    private static final String CHECK_SHOWALL_PREF = "show_all";
    private MenuItem showAll;
    private List<TodoTask> todos = new ArrayList<>();
    private LinearLayout activities;
    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // creating the list of todos
        activities = (LinearLayout) findViewById(R.id.activities);

        Log.d(TAG, "onCreate: connecting to db");
        // connect to db
        dbHelper = new TodoDBHelper(this);
        db = dbHelper.getWritableDatabase();

        // Get the data from shared preferences.
        sp = PreferenceManager.getDefaultSharedPreferences(this);



        // Get the data from database.
        Cursor cursor = getAllTasks();
        cursor.moveToNext();
        Log.d(TAG, "onCreate: in db found " + cursor.getCount());
        for (int i = 0; i < cursor.getCount(); i++) {

            ConstraintLayout newTaskView = (ConstraintLayout) getLayoutInflater().inflate(R.layout.one_activity, null);
            activities.addView(newTaskView);

            Log.d(TAG, "onCreate: creating new TodoTask " + cursor.getString(cursor.getColumnIndex(TodotaskContract.TodoEntry.NAME)));
//                //creating the object of the new task
            String name = cursor.getString(cursor.getColumnIndex(TodotaskContract.TodoEntry.NAME));
            int completion = cursor.getInt(cursor.getColumnIndex(TodotaskContract.TodoEntry.COMPLETION));
            long id = cursor.getInt(cursor.getColumnIndex(TodotaskContract.TodoEntry._ID));
            long dateDeadline = cursor.getLong(cursor.getColumnIndex(TodotaskContract.TodoEntry.DATE_DEADLINE));
            Log.d(TAG, "onCreate: from db: " + name + " " + completion + " " + id);
            TodoTask newTask = new TodoTask(newTaskView, name, completion, null, new Date(dateDeadline), id);

            cursor.moveToNext();
            todos.add(newTask);
        }
        cursor.close();

        updateCountTodos();
    }

    private void updateCountTodos() {
        int countFinished = 0;
        for (TodoTask todo: todos) {
            if (todo.isFinished()) {
                countFinished++;
            }
        }
        ((TextView) findViewById(R.id.todos_count)).setText("Finished " + countFinished +"/"+todos.size());
    }

    private Cursor getAllTasks() {
        // TODO make asynktaskLoader
        return db.query(TodoDBHelper.TABLE_NAME, null, null, null, null, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        // Get the data from the sharedPreferences.
        boolean showAllPref = sp.getBoolean(CHECK_SHOWALL_PREF, true);

        showAll = menu.findItem(R.id.showAll);
        showAll.setChecked(showAllPref);
        manageShowAll();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.add: {

                //inflating the new layout
                ConstraintLayout newTaskView = (ConstraintLayout) getLayoutInflater().inflate(R.layout.one_activity, null);
                activities.addView(newTaskView);
                //creating the object of the new task
                TodoTask newTask = new TodoTask(newTaskView);

                todos.add(newTask);

                break;
            }

            case R.id.clear: {

                todos.clear();
                activities.removeAllViews();
                db.delete(TodoDBHelper.TABLE_NAME, null, null);

                break;
            }
            case R.id.showAll: {

                showAll.setChecked(!showAll.isChecked());

                manageShowAll();

                sp.edit().putBoolean(CHECK_SHOWALL_PREF, showAll.isChecked()).apply();
                break;
            }
            case R.id.removeFinished: {
                List<TodoTask> finished = new LinkedList<>();

                for (TodoTask todo : todos) {
                    if (todo.isFinished()) {
                        Log.d(TAG, "onOptionsItemSelected: task " + todo);
                        finished.add(todo);
                    }
                }
                for (TodoTask todo : finished) {
                    removeTask(todo);
                }
                finished.clear();
                break;
            }
        }
        return true;
    }

    private void manageShowAll() {
        if (!showAll.isChecked()) {
            for (TodoTask todo : todos) {
                if (todo.isFinished()) {
                    todo.hide();
                }
            }
        } else {
            for (TodoTask todo : todos) {
                todo.show();
            }
        }
    }

    private void removeTask(TodoTask todo) {
        // Removing task from db
        todo.removeFromDb();

        // Deleting the view
        activities.removeView(todo.taskView);
        Log.d(TAG, "removeTask: " + todo.taskView);

        // Remove reference
        todos.remove(todo);

        // update the counter
        updateCountTodos();

    }

    @Override
    protected void onResume() {
        super.onResume();
//
//        for (TodoTask todo : todos) {
//            // TODO rename method
//            todo.updateTimeCreated();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == EditTask.EDITING_FINISHED) {
            if (data == null) {
                return;
            }

            String taskName = data.getStringExtra(EditTask.NAME_FIELD);
            long id = data.getLongExtra(EditTask.ID_FIELD, -1);
            Date date = new Date(data.getLongExtra(EditTask.DATE_FIELD, new Date().getTime()));

            Log.d(TAG, "onActivityResult: received " + taskName + " " + id + " " + date);

            TodoTask changedTask = null;
            for (TodoTask todo : todos) {
                if (todo.id == id) {
                    changedTask = todo;
                    break;
                }
            }
            assert changedTask != null;


            changedTask.updateTask(id, taskName, changedTask.completionPoints, date);
            changedTask.setText(changedTask.name);

        }
    }

    private class TodoTask {
        long id;
        String name;
        int completionPoints;
        Date dateCreated;
        Date dateDeadline;

        ConstraintLayout taskView;
        static final int MAX_COMPLETION_POINTS = 3;


        public TodoTask(ConstraintLayout taskView, String name, int completionPoints, Date dateCreated, Date dateDeadline) {
            this(taskView, name, completionPoints, dateCreated, dateDeadline, -1);

            // adding to the db
            ContentValues cv = new ContentValues();
            cv.put(TodotaskContract.TodoEntry.NAME, name);
            cv.put(TodotaskContract.TodoEntry.COMPLETION, completionPoints);
            id = db.insert(TodoDBHelper.TABLE_NAME, null, cv);

            startEditing();
        }


        public TodoTask(final ConstraintLayout taskView, String name, final int completionPoints, Date dateCreated, Date dateDeadline, long id) {
            this.taskView = taskView;
            this.name = name;
            this.completionPoints = completionPoints;
            this.dateCreated = dateCreated;
            this.dateDeadline = dateDeadline;
            this.id = id;

            updateBackground();

            Log.d(TAG, "TodoTask: completion points " + completionPoints);

            setText(name);

            if (completionPoints == MAX_COMPLETION_POINTS) {
                ((CheckBox) taskView.getChildAt(0)).setChecked(true);
            }

            taskView.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: checked " + taskView);
                    TodoTask.this.completionPoints = 3;
                    if (!((CheckBox) view).isChecked()) {
                        Log.d(TAG, "onClick: removeFromDb completion");

                        TodoTask.this.completionPoints = 0;
                    }
                    updateTask(TodoTask.this.id,TodoTask.this.name, TodoTask.this.completionPoints,
                             TodoTask.this.dateDeadline);
                }
            });

            taskView.getChildAt(2).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    PopupMenu menu = new PopupMenu(MainMenu.this, taskView.getChildAt(2));
                    menu.inflate(R.menu.item_context_menu);
                    menu.show();

                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit : {
                                    startEditing();
                                    break;
                                }
                                case R.id.remove : {
                                    removeTask(TodoTask.this);
                                    break;
                                }

                            }

                            return true;
                        }
                    });
                    return true;
                }
            });


            taskView.getChildAt(2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addCompletionPoint();
                }
            });
        }




        public TodoTask(ConstraintLayout newTaskView) {
            this(newTaskView, "New task", 0, null, new Date());
        }

        void setText(String name) {
            ((TextView) taskView.getChildAt(2)).setText(name);
        }

        void startEditing() {
            Intent intent = new Intent(MainMenu.this, EditTask.class);
            intent.putExtra(EditTask.NAME_FIELD, name);
            intent.putExtra(EditTask.ID_FIELD, id);

            MainMenu.this.startActivityForResult(intent, EditTask.EDITING_FINISHED);

        }

        void updateTask(long id, String name, int completionPoints,  Date dateDeadline) {
            this.name = name;
            this.completionPoints = completionPoints;
            this.dateDeadline = dateDeadline;

            ContentValues cv = new ContentValues();
            cv.put(TodotaskContract.TodoEntry.NAME, name);
            cv.put(TodotaskContract.TodoEntry.COMPLETION, completionPoints);
            cv.put(TodotaskContract.TodoEntry.DATE_DEADLINE, dateDeadline.getTime());

            db.update(TodoDBHelper.TABLE_NAME, cv, TodotaskContract.TodoEntry._ID +"="+id, null);

            updateBackground();
            updateCountTodos();

            if (!showAll.isChecked() && isFinished()) {
                hide();
            }

        }

        private void updateTime() {
            ((TextView) taskView.getChildAt(1)).setText(timeLeft(dateDeadline));
        }

        private String timeLeft(Date deadline) {
            Date now = new Date();
            long difference = deadline.getTime() - now.getTime();


            List<TimeUnit> units = new ArrayList<>(EnumSet.allOf(TimeUnit.class));
            Collections.reverse(units);
            Map<TimeUnit,Long> result = new LinkedHashMap<>();
            long milliesRest = difference;
            for ( TimeUnit unit : units ) {
                // cutting off the most valuable part of the rest millies
                long diff = unit.convert(milliesRest,TimeUnit.MILLISECONDS);
                long diffInMilliesForUnit = unit.toMillis(diff);
                milliesRest = milliesRest - diffInMilliesForUnit;
                result.put(unit,diff);
            }


            for (TimeUnit unit : units) {
                if (result.get(unit) != 0) {
                    return String.valueOf(result.get(unit)) + " " + unit.toString();
                }
            }

            return "";
        }

        void updateBackground() {
            updateTime();
            int valueForColors = (int) (255 * ((float) completionPoints / MAX_COMPLETION_POINTS));
            taskView.getChildAt(2).setBackgroundColor(Color.rgb(255-valueForColors, 255, 255-valueForColors));
            taskView.getChildAt(2).getBackground().setAlpha(30);
        }

        void removeFromDb() {
            Log.d(TAG, "removeFromDb: removing " + id);
            db.delete(TodoDBHelper.TABLE_NAME, TodotaskContract.TodoEntry._ID + "=" + id, null);

        }

        public boolean isFinished() {
            return completionPoints == MAX_COMPLETION_POINTS;
        }

        public void hide() {
            taskView.setVisibility(View.GONE);
        }

        public void show() {
            taskView.setVisibility(View.VISIBLE);
        }

        public void addCompletionPoint() {
            if (!isFinished()){
                completionPoints++;

                if (completionPoints == MAX_COMPLETION_POINTS) {
                    // set checked
                    ((CheckBox) taskView.getChildAt(0)).setChecked(true);
                }

                updateTask(id, name, completionPoints, dateDeadline);
            }

        }
    }


}
