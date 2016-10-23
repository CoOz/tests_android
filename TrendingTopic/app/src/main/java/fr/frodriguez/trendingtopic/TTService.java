package fr.frodriguez.trendingtopic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import fr.frodriguez.trendingtopic.utils.Logger;
import fr.frodriguez.trendingtopic.utils.TTIntents;
import fr.frodriguez.trendingtopic.utils.Utils;

/**
 * By Florian Rodriguez on 16/01/16.
 */
public class TTService extends Service {

    Logger _log;


    public void onCreate() {
        _log = new Logger(Utils.getAppName(this), this);
        _log.d("TTService onCreate");
    }

    public void onDestroy() {
        _log.d("TTService onDestroy");

        // cancel all alarms
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarmReceiver = new Intent(TTIntents.CHECK_TT);
        PendingIntent pendingIntentAlarmReceiver = PendingIntent.getBroadcast(this, Utils.NOTIFICATION_ID, intentAlarmReceiver, 0);
        alarmManager.cancel(pendingIntentAlarmReceiver);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        _log.d("TTService startCommand");

        Intent intentCheckTT = new Intent(TTIntents.CHECK_TT);
        intentCheckTT.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        PendingIntent pendingIntentCheckTT = PendingIntent.getBroadcast(this, Utils.NOTIFICATION_ID, intentCheckTT, PendingIntent.FLAG_UPDATE_CURRENT);

        // check new TTs every 15min
        ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).setRepeating(
                AlarmManager.RTC,
                System.currentTimeMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, //5*1000, // 5sec, for debug
                pendingIntentCheckTT
        );

        // restart the service if closed by system
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
