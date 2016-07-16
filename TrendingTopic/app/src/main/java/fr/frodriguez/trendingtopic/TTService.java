package fr.frodriguez.trendingtopic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Florian Rodriguez on 16/01/16.
 */
public class TTService extends Service {

    public void onCreate() {
        Log.d(Utils.LOG_TAG, "TTService onCreate");
    }

    public void onDestroy() {
        Log.d(Utils.LOG_TAG, "TTService onDestroy");

        // cancel all alarms
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarmReceiver = new Intent(this, TTAlarmReceiver.class);
        PendingIntent pendingIntentAlarmReceiver = PendingIntent.getBroadcast(this, 0, intentAlarmReceiver, 0);
        alarmManager.cancel(pendingIntentAlarmReceiver);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Utils.LOG_TAG, "TTService startCommand");

        Intent intentAlarmReceiver = new Intent(this, TTAlarmReceiver.class);
        PendingIntent pendingIntentAlarmReceiver = PendingIntent.getBroadcast(this, 0, intentAlarmReceiver, 0);
        // broadcast the intent every 15min
        ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                .setInexactRepeating(
                        AlarmManager.RTC,
                        System.currentTimeMillis(),
                        AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                        //10000, // 10sec, for debug
                        pendingIntentAlarmReceiver);

        // restart the service if closed by system
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
