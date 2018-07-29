package com.example.nick.todolist;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;


import com.example.nick.todolist.data.TodoDBHelper;
import com.example.nick.todolist.data.TodotaskContract;

import java.util.Calendar;
import java.util.Date;

public class BootReceiver extends BroadcastReceiver {

    public static final String ACTION_ALARM_TICK = "alarm_tick";
    public static final String TAG = "BootReceiver";
    public static final boolean DEBUG = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (DEBUG) Log.d(TAG, "onReceive " + intent);
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            setupAlarm(context, 0);
        } else if (ACTION_ALARM_TICK.equals(intent.getAction())) {
            onAlarmTick(context);
        }
    }

    private void setupAlarm(Context context, int addDays) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent wakeUpIntent = new Intent(context.getApplicationContext(), BootReceiver.class)
                .setAction(ACTION_ALARM_TICK);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, wakeUpIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, addDays);
        Date wakeupTime = calendar.getTime();

        if (wakeupTime.before(new Date())) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            wakeupTime = calendar.getTime();
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeupTime.getTime(), alarmIntent);
        if (DEBUG) Log.d(TAG, "alarm set to " + TodoDBHelper.DATABASE_TIME_FORMAT.format(wakeupTime));
    }

    private void onAlarmTick(Context context) {
        if (DEBUG) Log.d(TAG, "onAlarmTick");
        TodoDBHelper helper = new TodoDBHelper(context);
        Cursor tasks = helper.getAllTasks(TodotaskContract.TodoEntry.DATE_DEADLINE);

        int todayTasksCount = getTodayTasksCount(tasks);
        int allTasksCount = tasks.getCount();

        tasks.close();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("Tasks")
                .setSmallIcon(R.drawable.ic_search_black_24dp)
                .setContentText("There are " + todayTasksCount + " today tasks, and " +
                allTasksCount + " tasks at all.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat.from(context).notify(1, builder.build());

        setupAlarm(context, 1);
    }

    private int getTodayTasksCount(Cursor tasks) {
        int todayTasksCount = 0;
        Date tonight = getTonightsDate();

        while (tasks.moveToNext()) {
            Date deadline = TodoDBHelper.getDeadline(tasks);
            if (deadline.after(tonight)) {
                break;
            }
            todayTasksCount += 1;
        }
        return todayTasksCount;
    }

    private Date getTonightsDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        return cal.getTime();
    }
}
