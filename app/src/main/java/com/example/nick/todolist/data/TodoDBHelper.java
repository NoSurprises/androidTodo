package com.example.nick.todolist.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.nick.todolist.activities.MainActivity.TAG;

/**
 * Created by Nick on 11/20/2017. Database helper creates new databases and upgrades its structure
 * if needed
 */

public class TodoDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "todolist";
    public static final String SORT_COLUMN = "sort column";
    private static int DATABASE_VERSION = 10;

    public static SimpleDateFormat DATABASE_TIME_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());


    public TodoDBHelper(Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }

    public Cursor getAllTasks(String sortByPreference) {
        final SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(TodoDBHelper.TABLE_NAME, null, null,
                null, null, null,
                sortByPreference + " COLLATE NOCASE");


        return cursor;
    }

    public static Date getDeadline(Cursor c) {
        String rawDeadlineDate = getRawDeadline(c);
        return parseDeadline(rawDeadlineDate);
    }


    private static Date parseDeadline(String rawDeadlineDate) {
        Date deadline;

        try {
            deadline = DATABASE_TIME_FORMAT.parse(rawDeadlineDate);
        } catch (ParseException e) {
            // Set default value - today
            deadline = new Date();
        }
        return deadline;
    }
    private static String getRawDeadline(Cursor c) {
        return c.getString(c.getColumnIndex(TodotaskContract.TodoEntry.DATE_DEADLINE));
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.d(TAG, "onCreate: creating table");
        // create db
        String createDBquery = "CREATE TABLE " + TABLE_NAME + " ("+
                TodotaskContract.TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TodotaskContract.TodoEntry.NAME + " TEXT NOT NULL," +
                TodotaskContract.TodoEntry.DATE_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                TodotaskContract.TodoEntry.DATE_DEADLINE + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ");";

        sqLiteDatabase.execSQL(createDBquery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG, "onUpgrade: removing table");
        String dropQuery = "DROP TABLE IF EXISTS " + TABLE_NAME ;
        sqLiteDatabase.execSQL(dropQuery);
        onCreate(sqLiteDatabase);
    }
}
