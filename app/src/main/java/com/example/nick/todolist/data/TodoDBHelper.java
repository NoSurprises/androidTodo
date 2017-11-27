package com.example.nick.todolist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.nick.todolist.data.TodotaskContract;

import static com.example.nick.todolist.MainMenu.TAG;

/**
 * Created by Nick on 11/20/2017.
 */

public class TodoDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "todolist.db";
    public static String TABLE_NAME = "todolist";
    public static int DATABASE_VERSION = 7;


    public TodoDBHelper(Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.d(TAG, "onCreate: creating table");
        // create db
        String createDBquery = "CREATE TABLE " + TABLE_NAME + " ("+
                TodotaskContract.TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TodotaskContract.TodoEntry.NAME + " TEXT NOT NULL," +
                TodotaskContract.TodoEntry.COMPLETION + " INTEGER NOT NULL," +
                TodotaskContract.TodoEntry.DATE_CREATED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                TodotaskContract.TodoEntry.DATE_DEADLINE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + ");";

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
