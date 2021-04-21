package com.github.vipulasri.timelineview.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.github.vipulasri.timelineview.sample.model.TimeLineModel;

import java.util.ArrayList;
import java.util.Calendar;

public class TimelineAlarmManager extends BroadcastReceiver {



    ArrayList<TimeLineModel> mDataList;
    TimeLineModel currentNextNotifiedTask;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Repeated Action here
        Toast.makeText(context, "Broadcast Received", Toast.LENGTH_SHORT).show();
        Calendar rightNow = Calendar.getInstance();
        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY); // In 24 Hours format
        int currentMinute = rightNow.get(Calendar.MINUTE);

        Toast.makeText(context,"CURRENT: " + currentHour + " Hour and " + currentMinute + " minute" ,Toast.LENGTH_SHORT).show();

    }



}
