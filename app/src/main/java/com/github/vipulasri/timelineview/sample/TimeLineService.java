package com.github.vipulasri.timelineview.sample;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.github.vipulasri.timelineview.sample.model.TimeLineModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by Teresuki Kitsune
 */


public class TimeLineService extends Service {

     //ArrayList<TimeLineModel> mDataList = new ArrayList<TimeLineModel>();
    public static boolean isRunning = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        Toast.makeText(this, "Service is Runnning!", Toast.LENGTH_SHORT).show();

//        Bundle args = intent.getBundleExtra("BUNDLE");
        ArrayList<TimeLineModel> mDataListReceived = (ArrayList<TimeLineModel>) intent.getExtras().get("TimelineTasks");

        upcomingTaskNotification(mDataListReceived);
        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("RestartService");
        broadcastIntent.setClass(this, RestartService.class);
        this.sendBroadcast(broadcastIntent);
    }

    public void upcomingTaskNotification ( ArrayList<TimeLineModel> mDataList)
    {
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, TimelineAlarmManager.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);


        Calendar myCalendar = Calendar.getInstance();
        Calendar rightnow = Calendar.getInstance();

        int taskHour;
        int taskMinute;
        int taskHourNotif = 0;
        int taskMinuteNotif = 0;

        String taskNotifTitle = null;
        String taskNotifText = null;

        String test = mDataList.get(0).getDate();

        for(int i =0; i < mDataList.size(); i++) {
            taskHour = Integer.parseInt(mDataList.get(i).getDate().substring(0, 2));
            taskMinute = Integer.parseInt(mDataList.get(i).getDate().substring(3, 5));

            if (taskHour > rightnow.get(Calendar.HOUR_OF_DAY)) {
                taskHourNotif = taskHour;
                taskMinuteNotif = taskMinute;
                taskNotifTitle = mDataList.get(i).getMessage();
                taskNotifText = mDataList.get(i).getDate();
                break;
            } else if (taskHour == rightnow.get(Calendar.HOUR_OF_DAY) && taskMinute > rightnow.get(Calendar.MINUTE)) {
                taskHourNotif = taskHour;
                taskMinuteNotif = taskMinute;
                taskNotifTitle = mDataList.get(i).getMessage();
                taskNotifText = mDataList.get(i).getDate();
                break;
            }
        } //end for loop

//            if(taskHourNotif != Integer.parseInt(null) && taskMinuteNotif != Integer.parseInt(null))
            {
                myCalendar.set(Calendar.HOUR_OF_DAY, taskHourNotif);
                myCalendar.set(Calendar.MINUTE, taskMinuteNotif);
                myCalendar.set(Calendar.SECOND, 0);

                if(taskNotifTitle != null && taskNotifText != null)
                {
                    startAlarm(myCalendar, taskNotifTitle, taskNotifText);
                }
            }





    }// End Upcoming Task Notification

    public void startAlarm(Calendar c, String taskTitle, String taskText)
    {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("TaskTitle", taskTitle);
        intent.putExtra("TaskText", taskText);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, getRandomNumber(-10000, 10000) ,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        //Toast.makeText(this, "Alarm set successfully", Toast.LENGTH_SHORT).show();


    }

    public int getRandomNumber(int min, int max) {
        // min (inclusive) and max (exclusive)
        Random r = new Random();
        return r.nextInt(max - min) + min;
    }


}



