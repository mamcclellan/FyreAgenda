package org.mam.kch.fyreagenda.util;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import java.util.Calendar;

/**
 * Created by Kristofer on 12/19/2016.
 */

public class DailyAlarmReceiver extends Activity{
    // Sunday = 1, Monday = 2, etc.
    private int firstDayOfWeek = 2;
    final Context context = this;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if((int)Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == firstDayOfWeek){
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





}
