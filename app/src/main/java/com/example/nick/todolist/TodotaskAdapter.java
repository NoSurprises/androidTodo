package com.example.nick.todolist;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.nick.todolist.MainMenu.TAG;

/**
 * Created by Nick on 11/25/2017.
 */

public class TodotaskAdapter extends RecyclerView.Adapter<TodotaskAdapter.TodotaskViewholder> {
    private final Context mContext;
    private final List<MainMenu.TodoTask> todoTasks;
    private final List<MainMenu.TodoTask> todoActiveTasks;

    public TodotaskAdapter(Context context, List<MainMenu.TodoTask> todoTasks) {
        this.mContext = context;
        this.todoTasks = todoTasks;

        todoActiveTasks = new ArrayList<>();
        for (MainMenu.TodoTask todoTask : todoTasks) {
            if (!todoTask.isFinished()) {
                todoActiveTasks.add(todoTask);
            }
        }

    }

    @Override
    public TodotaskViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.one_activity, parent, false);
        return new TodotaskViewholder(view);
    }

    @Override
    public void onBindViewHolder(TodotaskViewholder holder, int position) {

        List<MainMenu.TodoTask> usingSource;
        if (MainMenu.showAllChecked) {
            usingSource = todoTasks;
        }
        else {
            usingSource = todoActiveTasks;
        }
        MainMenu.TodoTask todoTask = usingSource.get(position);
        if (todoTask == null)
            return;

        holder.mNameTextView.setText(todoTask.name);
        holder.mDeadlineTextView.setText(todoTask.timeLeft(todoTask.dateDeadline));
        holder.mFinishedCheckBox.setChecked(todoTask.isFinished());
        holder.mTask = todoTask;



        int valueForColors = (int) (255 * ((float) todoTask.completionPoints / todoTask.MAX_COMPLETION_POINTS));
        holder.mNameTextView.setBackgroundColor(Color.rgb(255-valueForColors, 255, 255-valueForColors));
        holder.mNameTextView.getBackground().setAlpha(30);


    }

    @Override
    public int getItemCount() {
        return todoTasks.size();
    }

    public void tasksUpdated() {
        Log.d(TAG, "tasksUpdated: new count " + todoTasks.size());

        this.notifyDataSetChanged();
    }

    public class TodotaskViewholder extends RecyclerView.ViewHolder  {

        TextView mNameTextView;
        TextView mDeadlineTextView;
        CheckBox mFinishedCheckBox;
        private View mView;
        MainMenu.TodoTask mTask;

        public TodotaskViewholder(View itemView) {
            super(itemView);

            mNameTextView = itemView.findViewById(R.id.textOfTask);
            mDeadlineTextView = itemView.findViewById(R.id.deadlineDate);
            mFinishedCheckBox = itemView.findViewById(R.id.finishedCheckBox);
            mView = itemView;


            mFinishedCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: checked " + mNameTextView.getText().toString());
                    mTask.completionPoints = 3;
                    if (!((CheckBox) view).isChecked()) {
                        Log.d(TAG, "onClick: removeFromDb completion");

                        mTask.completionPoints = 0;
                    }
                    mTask.updateTask(mTask.id, mTask.name, mTask.completionPoints,
                            mTask.dateDeadline);
                }
            });

            mNameTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    PopupMenu menu = new PopupMenu(mContext, mNameTextView);
                    menu.inflate(R.menu.item_context_menu);
                    menu.show();

                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit : {
                                    mTask.startEditing();
                                    break;
                                }
                                case R.id.remove : {
                                    //removeTask(mTask);
                                    Toast.makeText(mContext, "Deleting", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            return true;
                        }
                    });
                    return true;
                }
            });


        }

        List<Object> items =new ArrayList<>();
        Map<Integer,Object> deletedItems = new HashMap<>();


        public void hideItem(final int position) {
            deletedItems.put(position, items.get(position));
            items.remove(position);
            notifyItemRemoved(position);
        }
        void show() {
            mView.setVisibility(View.VISIBLE);
        }
    }


}
