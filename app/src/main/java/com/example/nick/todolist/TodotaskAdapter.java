package com.example.nick.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nick.todolist.data.TodotaskContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.nick.todolist.MainMenu.TAG;

/**
 * Created by Nick on 11/25/2017. adapter class of the recycler view. Binds information
 * with view holders
 */

class TodotaskAdapter extends RecyclerView.Adapter<TodotaskAdapter.TodotaskViewholder> {
    private final Context mContext;
    private Cursor mCursor;

    private SimpleDateFormat databaseTimeFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    TodotaskAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
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
        String rawDeadlineDate =
                mCursor.getString(mCursor.getColumnIndex(TodotaskContract.TodoEntry.DATE_DEADLINE));

        Date deadline;
        try {
            deadline = databaseTimeFormat.parse(rawDeadlineDate);
        } catch (ParseException e) {
            // Set default value - today
            deadline = new Date();
        }
        final int id = mCursor.getInt(mCursor.getColumnIndex(TodotaskContract.TodoEntry._ID));

        final ContentValues cv = new ContentValues();
        cv.put(TodotaskContract.TodoEntry.NAME, name);
        cv.put(TodotaskContract.TodoEntry.DATE_DEADLINE, rawDeadlineDate);
        cv.put(TodotaskContract.TodoEntry._ID, id);

        holder.mNameTextView.setText(name);
        holder.mDeadlineTextView.setText(timeLeft(deadline.getTime()));

        holder.itemView.setTag(id);
    }



    /**
     * Return the time in short format to the specified date
     *
     * @param deadline the end point of the time interval
     * @return String representation of the time left
     */
    private String timeLeft(long deadline) {
        Date now = new Date();
        long difference = deadline - now.getTime();


        List<TimeUnit> units = new ArrayList<>(EnumSet.allOf(TimeUnit.class));
        Collections.reverse(units);
        Map<TimeUnit, Long> result = new LinkedHashMap<>();
        long milliesRest = difference;
        for (TimeUnit unit : units) {
            // cutting off the most valuable part of the rest millies
            long diff = unit.convert(milliesRest, TimeUnit.MILLISECONDS);
            long diffInMilliesForUnit = unit.toMillis(diff);
            milliesRest = milliesRest - diffInMilliesForUnit;
            result.put(unit, diff);
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


    void swapCursor(Cursor cursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = cursor;
        this.notifyDataSetChanged();
    }


    void startEditing(long mId) {
        Intent intent = new Intent(mContext, EditTask.class);
        intent.putExtra(TodotaskContract.TodoEntry._ID, mId);

        mContext.startActivity(intent);

    }

    class TodotaskViewholder extends RecyclerView.ViewHolder {

        TextView mNameTextView;
        TextView mDeadlineTextView;


        TodotaskViewholder(final View itemView) {
            super(itemView);

            mNameTextView = itemView.findViewById(R.id.textOfTask);
            mDeadlineTextView = itemView.findViewById(R.id.deadlineDate);


            mNameTextView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View view) {
                    startEditing(((int) itemView.getTag()));
                    return true;
                }
            });
        }
    }


}
