package com.github.vipulasri.timelineview.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String taskTitle = intent.getStringExtra("TaskTitle");
        String taskText = intent.getStringExtra("TaskText");
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(taskTitle, taskText);
        notificationHelper.getManager().notify(1, nb.build());
    }
}