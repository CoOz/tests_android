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
        Log.d("DEBUG TT", "Create service");
    }

    public void onDestroy() {
        Log.d("DEBUG TT", "Destroy service");

        // annuler les alarmes, si le service est arrêté par le système ou l'utilisateur
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentAlarmReceiver = new Intent(this, TTAlarmReceiver.class);
        PendingIntent pendingIntentAlarmReceiver = PendingIntent.getBroadcast(this, 0, intentAlarmReceiver, 0);
        alarmManager.cancel(pendingIntentAlarmReceiver);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("DEBUG TT", "Service startCommand");

        // récupérer l'alarmReceiver qui s'occupera d'appeler périodiquement l'AlarmReceiver
        Intent intentAlarmReceiver = new Intent(this, TTAlarmReceiver.class);
        PendingIntent pendingIntentAlarmReceiver = PendingIntent.getBroadcast(this, 0, intentAlarmReceiver, 0);
        // lancer l'intent à interval régulier (toutes les 15min)
        ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                .setInexactRepeating(
                        AlarmManager.RTC,
                        System.currentTimeMillis(),
                        AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                        //10000, // 10sec, pour debug
                        pendingIntentAlarmReceiver);


        // permet de redémarrer le service s'il est fermé par le système
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
