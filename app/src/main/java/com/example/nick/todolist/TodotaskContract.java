package com.example.nick.todolist;

import android.provider.BaseColumns;

/**
 * Created by Nick on 11/20/2017.
 */

public class TodotaskContract {
    public static final class TodoEntry implements BaseColumns {
        public static String NAME = "name";
        public static String COMPLETION = "completion";
        public static String DATE_CREATED = "created";
        public static String DATE_DEADLINE = "deadline";

    }
}
