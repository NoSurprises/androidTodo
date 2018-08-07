package com.example.nick.todolist;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nick.todolist.activities.EditTaskActivity;
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

import static com.example.nick.todolist.activities.MainActivity.TAG;


public class TodotaskAdapter extends RecyclerView.Adapter<TodotaskAdapter.TodotaskViewholder> {
    private final Context context;
    private Cursor cursor;

    public TodotaskAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public TodotaskViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.one_activity, parent, false);
        return new TodotaskViewholder(view);
    }

    @Override
    public void onBindViewHolder(final TodotaskViewholder holder, final int position) {

        Log.d(TAG, "onBindViewHolder: ");
        if (!cursor.moveToPosition(position)) {
            // No such position at db
            return;
        }

        String name = getName();
        name = getStringAsTitle(name);
        Date deadline = TodoDBHelper.getDeadline(cursor);
        final int id = getId();

        createAndSetUpContentValues(name, id);

        setNameDeadlineToHolder(holder, name, deadline);
        setTagWithIdToHolder(holder, id);
    }

    @NonNull
    private String getStringAsTitle(String name) {
        String[] words = name.split(" ");
        StringBuilder sb = new StringBuilder(words.length);
        for (String word : words) {
            sb.append(getWordAsTitle(word) + " ");
        }
        return sb.toString();

    }

    private String getWordAsTitle(String word) {
        if (word.length() > 0) {
            word = word.substring(0, 1).toUpperCase() + word.substring(1);
        }
        return word;
    }

    void setNameDeadlineToHolder(TodotaskViewholder holder, String name, Date deadline) {
        holder.nameText.setText(name);
        holder.deadlineText.setText(timeLeft(deadline.getTime()));
    }

    void setTagWithIdToHolder(TodotaskViewholder holder, int id) {
        holder.itemView.setTag(id);
    }

    private void createAndSetUpContentValues(String name, int id) {
        final ContentValues cv = createContentValues();
        setUpContentValues(name, id, cv);
    }

    private void setUpContentValues(String name, int id, ContentValues cv) {
        cv.put(TodotaskContract.TodoEntry.NAME, name);
        cv.put(TodotaskContract.TodoEntry.DATE_DEADLINE, getRawDeadline());
        cv.put(TodotaskContract.TodoEntry._ID, id);
    }

    @NonNull
    private ContentValues createContentValues() {
        return new ContentValues();
    }

    private int getId() {
        return cursor.getInt(cursor.getColumnIndex(TodotaskContract.TodoEntry._ID));
    }


    private String getRawDeadline() {
        return getColumn(cursor.getColumnIndex(TodotaskContract.TodoEntry.DATE_DEADLINE));
    }

    private String getName() {
        return getColumn(cursor.getColumnIndex(TodotaskContract.TodoEntry.NAME));
    }

    private String getColumn(int columnIndex) {
        return cursor.getString(columnIndex);
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
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }


    public void swapCursor(Cursor cursor) {
        if (this.cursor != null) {
            this.cursor.close();
        }
        this.cursor = cursor;
        this.notifyDataSetChanged();


    }


    public void startEditing(long mId) {
        Intent intent = new Intent(context, EditTaskActivity.class);
        intent.putExtra(TodotaskContract.TodoEntry._ID, mId);

        context.startActivity(intent);

    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboardManager.setPrimaryClip(clip);
    }

    class TodotaskViewholder extends RecyclerView.ViewHolder {

        TextView nameText;
        TextView deadlineText;


        TodotaskViewholder(final View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.textOfTask);
            deadlineText = itemView.findViewById(R.id.deadlineDate);


            nameText.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(final View view) {
                    PopupMenu options = new PopupMenu(view.getContext(), view);
                    options.inflate(R.menu.task_options_menu);
                    options.show();

                    options.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.options_edit:
                                    startEditing(((int) itemView.getTag()));
                                    return true;
                                case R.id.options_copy_text:
                                    copyToClipboard("Text of todo task", nameText.getText().toString());
                                    Toast.makeText(view.getContext(), "Copied: " + nameText.getText(), Toast.LENGTH_SHORT).show();
                                    return true;
                            }
                            return false;
                        }
                    });

                    return true;
                }
            });
        }
    }


}
