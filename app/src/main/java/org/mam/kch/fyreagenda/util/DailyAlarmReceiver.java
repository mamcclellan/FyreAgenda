package org.mam.kch.fyreagenda.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import java.util.Calendar;

public class DailyAlarmReceiver extends BroadcastReceiver {
    // Sunday = 1, Monday = 2, etc.
    private int firstDayOfWeek = 4;
    // This value is defined and consumed by app code, so any value will work.
    // There's no significance to this sample using 0.
    public static final int REQUEST_CODE = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        Toast.makeText(context, "Alarm is working", Toast.LENGTH_LONG).show();

        /*if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            this.setAlarm(context);
        }*/
        if(today == firstDayOfWeek){
            Task.loadTasks(context);
            endWeek();
        }
    }

    public void setFirstDayOfWeek(int i){
        if(i>=1 && i<=7)
            firstDayOfWeek = i;
    }

    public void endWeek(){
        for (int i = 0; i < Task.THISWEEK.size(); ) {
            Task.TaskItem item = Task.THISWEEK.get(i);
            if (item.getTaskComplete()) {
                Task.archiveItem(item);
            } else
                i++;
        }
        for (int i = 0; i < Task.NEXTWEEK.size(); ) {
            Task.TaskItem item = Task.NEXTWEEK.get(i);
            if (item.getTaskComplete()) {
                Task.archiveItem(item);
            } else
                Task.moveItemToNewListAtBottom(item, Task.TaskType.THISWEEK);
        }

        for (int i = 0; i < Task.THISMONTH.size(); ) {
            Task.TaskItem item = Task.THISMONTH.get(i);
            if (item.getTaskComplete()) {
                Task.archiveItem(item);
            } else
                Task.moveItemToNewListAtBottom(item, Task.TaskType.NEXTWEEK);
        }
    }


    public void setAlarm(Context context)
    {
        Intent intent = new Intent(context, DailyAlarmReceiver.class);
        intent.setAction(Intent.ACTION_MAIN);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        // Set the alarm to start at approximately 2:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 3);

        alarmMgr.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    public void setOnetimeTimer(Context context){
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DailyAlarmReceiver.class);
        intent.putExtra("onetime", Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pi);
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, DailyAlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }




}
