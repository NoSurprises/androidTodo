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
    public void onBindViewHolder(final TodotaskViewholder holder, final int position) {

        Log.d(TAG, "onBindViewHolder: ");
        if (!mCursor.moveToPosition(position)) {
            // No such position at db
            return;
        }

        String name = mCursor.getString(mCursor.getColumnIndex(TodotaskContract.TodoEntry.NAME));
        long deadline = mCursor.getLong(mCursor.getColumnIndex(TodotaskContract.TodoEntry.DATE_DEADLINE));
        final int id = mCursor.getInt(mCursor.getColumnIndex(TodotaskContract.TodoEntry._ID));

        final ContentValues cv = new ContentValues();
        cv.put(TodotaskContract.TodoEntry.NAME, name);
        cv.put(TodotaskContract.TodoEntry.DATE_DEADLINE, deadline);
        cv.put(TodotaskContract.TodoEntry._ID, id);

        holder.mNameTextView.setText(name);
        holder.mDeadlineTextView.setText(timeLeft(deadline));

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

                                removeItem(id, position);
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

    private void removeItem(int id, int position) {
        mDb.delete(TodoDBHelper.TABLE_NAME, TodotaskContract.TodoEntry._ID+"="+id, null);
        notifyItemRemoved(position);
        swapCursor();
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
    }
    public void swapCursor() {
        Cursor newCursor = mDb.query(TodoDBHelper.TABLE_NAME, null, null, null, null, null, null);
        swapCursor(newCursor);
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
            mView = itemView;

        }

    }

    private void startEditing(long mId) {
        Intent intent = new Intent(mContext, EditTask.class);
        intent.putExtra(TodotaskContract.TodoEntry._ID, mId);

        mContext.startActivity(intent);

    }


}
