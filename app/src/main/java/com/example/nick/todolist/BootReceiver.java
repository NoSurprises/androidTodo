package com.example.nick.todolist;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.nick.todolist.activities.MainActivity;
import com.example.nick.todolist.data.TodoDBHelper;
import com.example.nick.todolist.data.TodotaskContract;

import java.util.Calendar;
import java.util.Date;

public class BootReceiver extends BroadcastReceiver {

    public static final String ACTION_ALARM_TICK = "alarm_tick";
    public static final String TAG = "BootReceiver";
    public static final boolean DEBUG = true;
    public static final String SCHEDULE_ALARM = "schedule alarm";
    public static final String NOTIFICATION_CHANNEL_ID = "1001";
    public static final String NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (DEBUG) Log.d(TAG, "onReceive " + intent);
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
                SCHEDULE_ALARM.equals(intent.getAction())) {
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
        Date wakeupTime = getNextAlarmDate(addDays);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeupTime.getTime(), alarmIntent);
        if (DEBUG) Log.d(TAG, "alarm set to " + TodoDBHelper.DATABASE_TIME_FORMAT.format(wakeupTime));
    }

    private Date getNextAlarmDate(int addDays) {
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
        return wakeupTime;
    }

    private void onAlarmTick(Context context) {
        if (DEBUG) Log.d(TAG, "onAlarmTick");
        TodoDBHelper helper = new TodoDBHelper(context);
        Cursor tasks = helper.getAllTasks(TodotaskContract.TodoEntry.DATE_DEADLINE);

        int todayTasksCount = getTodayTasksCount(tasks);
        int allTasksCount = tasks.getCount();

        tasks.close();

        sendNotification(context, todayTasksCount, allTasksCount);
        setupAlarm(context, 1);
    }

    private void sendNotification(Context context, int todayTasksCount, int allTasksCount) {
        Intent openAppIntent = new Intent(context, MainActivity.class);
        PendingIntent openApp = PendingIntent.getActivity(context.getApplicationContext(), 0,
                openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Tasks")
                .setSmallIcon(R.drawable.ic_search_black_24dp)
                .setContentText("There are " + todayTasksCount + " today tasks, and " +
                        allTasksCount + " tasks at all.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(openApp);


        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = getNotificationChannel();

        manager.createNotificationChannel(notificationChannel);
        manager.notify(0 /* Request Code */, builder.build());
    }

    @NonNull
    private NotificationChannel getNotificationChannel() {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        return notificationChannel;
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
