package com.example.nick.todolist;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.jar.Attributes;


public class MainMenu extends AppCompatActivity {

    public static final String TAG = "daywint";

    MenuItem showAll;

    Menu optionsMenu;

    ArrayList<TodoTask> todos = new ArrayList<>();

    LinearLayout activities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // creating the list of todos
        activities = (LinearLayout) findViewById(R.id.activities);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu, menu);
        optionsMenu = menu;
        showAll = menu.findItem(R.id.showAll);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.add: {

                //inflating the list with the new taskTodo
                View newTaskView = getLayoutInflater().inflate(R.layout.one_activity, activities);
                newTaskView = ((LinearLayout) newTaskView).getChildAt(0); // TODO help!!
                Log.d(TAG, "Inflating finished, created " + newTaskView);
                //creating the object of the new task
                TodoTask newTask = new TodoTask((ConstraintLayout) newTaskView);

                todos.add(newTask);
                break;
            }

            case R.id.clear: {

                todos = new ArrayList<>();
                activities.removeAllViews();
                break;

            }


            case R.id.showAll: {

                showAll.setChecked(!showAll.isChecked());
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

                break;

            }
            case R.id.removeFinished:

                try {
                    int count = 0;
                    for (TodoTask todo : todos) {
                        if (todo.isFinished()) {
                            count++;
                        }
                    }
                    TodoTask[] toRemove = new TodoTask[count];
                    count = 0;
                    for (TodoTask todo : todos) {

                        if (todo.isFinished()) {
                            toRemove[count++] = todo;
                        }
                    }
                    for (TodoTask task : toRemove) {
                        task.removeTask();
                    }
                    break;
                } catch (Exception e) {
                    Log.d(TAG, " " + e);
                }

        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        for (TodoTask todo : todos) {
            // TODO rename method
            todo.updateTimeCreated();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Creating new task activity is finished. In result now");
        if (data == null) {
            return;
        }

        Log.d(TAG, "Data is not null, getting extras");
        String taskName = data.getStringExtra("name");
        int hash = data.getIntExtra("hash", 0);
        String date = data.getStringExtra("date");
        Log.d(TAG, "Got the following results: name " + taskName + ", hash " + hash + ", date " + date);


//        String taskName = data.getExtras().get("name").toString();
//        String hash = data.getExtras().get("hash").toString();
//        String date = data.getExtras().get("date").toString();
        Log.d(TAG, "date is " + date);


        TodoTask changedTask = null;

        for (TodoTask todo : todos) {
            if (todo.hashCode() == hash) {
                changedTask = todo;
                break;
            }
        }
        if (changedTask != null) {
            changedTask.setText(taskName);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM;yyyy");

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            c.setTime(new Date(System.currentTimeMillis()));
        }
        c.add(Calendar.DATE, 1); // adding one day
        changedTask.setDeadlineDate(c.getTime());


    }


    boolean hideFinishedTasks() {
        return !showAll.isChecked();
    }


    private class TodoTask {

        private ArrayList<RemoveTask> removeListeners;
        private ConstraintLayout todoObject;
        private Date deadlineDate;
        private Date dateCreated;

        private int completionPoints = 0;
        private TextView textTimeCreated;
        private TextView textOfTask;
        private boolean finished;

        private String text;


        TodoTask(ConstraintLayout todoObject) {
            this.todoObject = todoObject;
            textOfTask = todoObject.findViewById(R.id.textView);

            removeListeners = new ArrayList<>();

            Calendar cal = Calendar.getInstance();
            dateCreated = cal.getTime();

            setText("New task..");


            textTimeCreated = todoObject.findViewById(R.id.timeCreated);

            textOfTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addCompletionPoint();
                }
            });


            textOfTask.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {
                    // create popupMenu

                    PopupMenu popupMenu = new PopupMenu(MainMenu.this, view);
                    popupMenu.inflate(R.menu.item_context_menu);

                    final TodoTask taskObject = findTaskByText(((TextView) view));
                    if (taskObject == null) {
                        return false;
                    }
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit:
                                    Log.d(TAG, "editing item " + taskObject.text);
                                    switchToEditingActivity();
                                    break;
                                case R.id.remove:
                                    Log.d(TAG, "removing item " + taskObject);
                                    // removing task
                                    taskObject.removeTask();
                                    break;
                                default:
                                    return false;
                            }
                            return true;
                        }
                    });

                    popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                        @Override
                        public void onDismiss(PopupMenu menu) {
                            updateTimeCreated(); // not the best place for updating the dateCreated.

                        }
                    });
                    popupMenu.show();

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

            switchToEditingActivity();

        }

        private void switchToEditingActivity() {
            Intent intent = new Intent(MainMenu.this, EditTask.class);
            intent.putExtra("hash", this.hashCode());
            intent.putExtra("name", text);
            MainMenu.this.startActivityForResult(intent, 1);

        }

        @Override
        public int hashCode() {
            return text.hashCode() + 31 * dateCreated.hashCode();
        }


        void removeTask() {
            activities.removeView(todoObject);
            for (RemoveTask removeListener : removeListeners) {
                removeListener.removeTask(this);
            }
        }

        private TodoTask findTaskByText(TextView view) {
            int index = 0;
            for (TodoTask createdTask : todos) {
                if (createdTask.textOfTask.equals(view)) {
                    Log.d(TAG, "task found on " + index + "place out of " + todos.size());
                    return createdTask;

                }
                index++;
            }

            return null;
        }

        private void resetTask() {
            ((CheckBox) todoObject.findViewById(R.id.checkBox)).setChecked(false);
            textOfTask.setPaintFlags(textOfTask.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            finished = false;
        }

        void updateTimeCreated() {
            textTimeCreated.setText(getTimePeriod(new Date(), deadlineDate));
        }

        private String getTimePeriod(Date first, Date second) {
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
            }
            if (timeBetween < 2400) {
                return timeBetween / 60 + "m";
            }
            if (timeBetween / 2400 < 24) {
                return timeBetween / 60 / 60 + "h";
            }
            return timeBetween / 2400 / 24 + "d";


        }

        void setText(String text) {
            // TODO check user input
            this.text = text;
            textOfTask.setText(text);
        }

        void setDeadlineDate(Date date) {
            deadlineDate = date;
        }

        private void addCompletionPoint() {
            if (completionPoints < 3) {
                completionPoints++;

            } else {
                finishTask();
            }
        }

        private void finishTask() {
            ((CheckBox) todoObject.findViewById(R.id.checkBox)).setChecked(true);
            textOfTask.setPaintFlags(textOfTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            finished = true;

            if (hideFinishedTasks()) {
                hide();
            }

        }

        boolean isFinished() {
            return finished;
        }

        void hide() {
            // TODO add animation
            todoObject.setVisibility(View.GONE);

        }

        void show() {

            todoObject.setVisibility(View.VISIBLE);
        }
    }


    interface RemoveTask {
        void removeTask(TodoTask task);
    }
}
