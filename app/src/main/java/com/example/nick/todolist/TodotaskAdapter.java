package com.example.nick.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.example.nick.todolist.data.TodoDBHelper;
import com.example.nick.todolist.data.TodotaskContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.nick.todolist.MainMenu.TAG;

/**
 * Created by Nick on 11/25/2017.
 */

public class TodotaskAdapter extends RecyclerView.Adapter<TodotaskAdapter.TodotaskViewholder> {
    private final Context mContext;
    private Cursor mCursor;
    private SQLiteDatabase mDb;
    public static final int MAX_COMPLETION_POINTS = 3;

    public TodotaskAdapter(Context context, Cursor cursor, SQLiteDatabase mDb) {
        this.mContext = context;
        this.mCursor = cursor;
        this.mDb = mDb;
    }

    @Override
    public TodotaskViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.one_activity, parent, false);
        return new TodotaskViewholder(view);
    }

    @Override
    public void onBindViewHolder(final TodotaskViewholder holder, int position) {

        Log.d(TAG, "onBindViewHolder: ");
        if (!mCursor.moveToPosition(position)) {
            // No such position at db
            return;
        }

        String name = mCursor.getString(mCursor.getColumnIndex(TodotaskContract.TodoEntry.NAME));
        long deadline = mCursor.getLong(mCursor.getColumnIndex(TodotaskContract.TodoEntry.DATE_DEADLINE));
        final int completion = mCursor.getInt(mCursor.getColumnIndex(TodotaskContract.TodoEntry.COMPLETION));
        final int id = mCursor.getInt(mCursor.getColumnIndex(TodotaskContract.TodoEntry._ID));

        final ContentValues cv = new ContentValues();
        cv.put(TodotaskContract.TodoEntry.NAME, name);
        cv.put(TodotaskContract.TodoEntry.DATE_DEADLINE, deadline);
        cv.put(TodotaskContract.TodoEntry.COMPLETION, completion);
        cv.put(TodotaskContract.TodoEntry._ID, id);

        holder.mNameTextView.setText(name);
        holder.mFinishedCheckBox.setChecked(completion == MAX_COMPLETION_POINTS);
        if (completion != MAX_COMPLETION_POINTS)
        {
            holder.mDeadlineTextView.setText(timeLeft(deadline));
        }
        else {
            holder.mDeadlineTextView.setText("");
        }


        holder.mNameTextView.setOnClickListener(new View.OnClickListener() {
            int lCompletion = completion;
            ContentValues lCv = cv;

            @Override
            public void onClick(View v) {
                if (lCompletion == MAX_COMPLETION_POINTS)
                    return;

                lCompletion++;
                if (lCompletion == MAX_COMPLETION_POINTS) {
                    holder.mFinishedCheckBox.setChecked(true);
                }
                lCv.put(TodotaskContract.TodoEntry.COMPLETION, lCompletion);

                // Updating the entry in the db
                mDb.update(TodoDBHelper.TABLE_NAME, lCv,
                        TodotaskContract.TodoEntry._ID+"="+lCv.getAsInteger(TodotaskContract.TodoEntry._ID), null);

                holder.updateBackground(lCompletion);


            }
        });
        holder.mFinishedCheckBox.setOnClickListener(new View.OnClickListener() {
            int lCompletion = completion;
            ContentValues lCv = cv;

            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: checked " + holder.mNameTextView.getText().toString());
                lCompletion = 3;
                if (!((CheckBox) view).isChecked()) {
                    Log.d(TAG, "onClick: removeFromDb completion");

                    lCompletion = 0;
                }

                lCv.put(TodotaskContract.TodoEntry.COMPLETION, lCompletion);

                // Updating the entry in the db
                mDb.update(TodoDBHelper.TABLE_NAME, lCv,
                        TodotaskContract.TodoEntry._ID+"="+lCv.getAsInteger(TodotaskContract.TodoEntry._ID), null);


                holder.updateBackground(lCompletion);

            }
        });
        holder.mNameTextView.setOnLongClickListener(new View.OnLongClickListener() {
            int tmp = 0; //todo delete, just to hide the code block
            @Override
            public boolean onLongClick(View view) {
                PopupMenu menu = new PopupMenu(mContext, holder.mNameTextView);
                menu.inflate(R.menu.item_context_menu);
                menu.show();

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit : {
                                startEditing(id);
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

        holder.updateBackground(completion);

    }

    /**
     * Return the time in short format to the specified date
     * @param deadline the end point of the time interval
     * @return String representation of the time left
     */
    String timeLeft(long deadline) {
        Date now = new Date();
        long difference = deadline - now.getTime();


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


    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


    public void swapCursor(Cursor cursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = cursor;
        this.notifyDataSetChanged();
    }

    public class TodotaskViewholder extends RecyclerView.ViewHolder  {

        TextView mNameTextView;
        TextView mDeadlineTextView;
        CheckBox mFinishedCheckBox;
        private View mView;


        public TodotaskViewholder(View itemView) {
            super(itemView);

            mNameTextView = itemView.findViewById(R.id.textOfTask);
            mDeadlineTextView = itemView.findViewById(R.id.deadlineDate);
            mFinishedCheckBox = itemView.findViewById(R.id.finishedCheckBox);
            mView = itemView;

        }
        void updateBackground(int completionPoints) {
            int valueForColors = (int) (255 * ((float) completionPoints / MAX_COMPLETION_POINTS));
            mNameTextView.setBackgroundColor(Color.rgb(255-valueForColors, 255, 255-valueForColors));
            mNameTextView.getBackground().setAlpha(30);
        }
    }

    private void startEditing(long mId) {
        Intent intent = new Intent(mContext, EditTask.class);
        intent.putExtra(TodotaskContract.TodoEntry._ID, mId);

        mContext.startActivity(intent);

    }


}
