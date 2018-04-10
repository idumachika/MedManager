package com.example.iduma.medmanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import MedHelperClass.ReminderDbAdapter;

/**
 * Created by iduma on 4/7/18.
 */

public class ReminderService extends  WakeReminderIntentService {
    Notification myNotication;


    public ReminderService() {
        super("ReminderService");
    }

    @Override
    void doReminderWork(Intent intent) {
        Log.d("ReminderService", "Doing work.");
        Long rowId = intent.getExtras().getLong(ReminderDbAdapter.KEY_ROWID);

        NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, UsersListActivity.class);
        notificationIntent.putExtra(ReminderDbAdapter.KEY_ROWID, rowId);

        PendingIntent pi = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

//        Notification note=new Notification(android.R.drawable.stat_sys_warning, getString(R.string.notify_new_task_message), System.currentTimeMillis());
//        //note.setLatestEventInfo(this, getString(R.string.notify_new_task_title), getString(R.string.notify_new_task_message), pi);
//        note.defaults |= Notification.DEFAULT_SOUND;
//        note.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        // An issue could occur if user ever enters over 2,147,483,647 tasks. (Max int value).
//        // I highly doubt this will ever happen. But is good to note.
//        int id = (int)((long)rowId);
//        mgr.notify(id, note);
        Notification.Builder builder = new Notification.Builder(ReminderService.this);

        builder.setAutoCancel(false);
        builder.setTicker("this is ticker text");
        builder.setContentTitle("MedManager Notification");
        builder.setContentText("You have a new message");
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentIntent(pi);
        builder.setOngoing(true);
        builder.setSubText("This is subtext...");   //API level 16
        builder.setNumber(100);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        builder.build();

        myNotication = builder.getNotification();
        mgr.notify(11, myNotication);



    }


    
}
