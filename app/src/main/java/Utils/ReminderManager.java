package Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.iduma.medmanager.OnAlarmReceiver;

import java.util.Calendar;

import MedHelperClass.ReminderDbAdapter;

/**
 * Created by iduma on 4/7/18.
 */

public class ReminderManager {


    private Context mContext;
    private AlarmManager mAlarmManager;

    public ReminderManager(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    }

    public void setReminder(Long taskId, Calendar when) {

        Intent i = new Intent(mContext, OnAlarmReceiver.class);
        i.putExtra(ReminderDbAdapter.KEY_ROWID, (long)taskId);

        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, i, PendingIntent.FLAG_ONE_SHOT);

        mAlarmManager.set(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pi);
    }
}
