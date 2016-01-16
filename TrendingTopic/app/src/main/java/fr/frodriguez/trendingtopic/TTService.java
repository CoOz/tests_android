package fr.frodriguez.trendingtopic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Florian Rodriguez on 16/01/16.
 */
public class TTService extends Service{

//    private static int TIMING = 900000;
    // pour le debug réduit à 10sec
    private static int TIMING = 10000;

    private Timer timer;
    private TimerTask timerTask;


    public void onCreate() {
        Log.d("DEBUG", "Create service");
        // créer le timer ici pour pouvoir le supprimer dans onDestroy et libérer le thread
        timer = new Timer();
    }

    public void onDestroy() {
        Log.d("DEBUG", "Destroy service");
        // supprimer le timer sinon le thread reste actif
        timer.cancel();
    }

    // démarrage, après onCreate
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("DEBUG", "Service startCommand");

        final Context context = this;

        //TODO alarmManager au lieu de timerTask (trop lourd)

        // créer la tache répétitive qui va s'exécuter en tache de fond
        timerTask = new TimerTask() {
            public void run() {
                TTCheck.check(context);
            }
        };
        // répéter toutes les 15min
        timer.schedule(timerTask, 0, TIMING);

        // permet de redémarrer le service s'il est fermé par le système
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
