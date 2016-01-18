package fr.frodriguez.trendingtopic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import twitter4j.Trend;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Florian Rodriguez on 18/01/16.
 */
public class TTAlarmReceiver extends BroadcastReceiver {

    private static String SHARED_PREFERENCES_FILE_KEY = "fr.frodriguez.trendingtopic.lastTT";
    private static String SHARED_PREFERENCES_KEY = "lastTT";
    private static int NOTIFICATION_ID = 0;
    private static int TWITTER_WOEID_WORLD = 1;
    private static int TWITTER_WOEID_FRANCE = 23424819;


    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("DEBUG TT", "AlarmManager onReceive");

        final Context contextThread = context;

        // thread pour l'accès à internet
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                // service d'authentification OAUTH pour utiliser l'api twitter
                ConfigurationBuilder cb = new ConfigurationBuilder();
                cb.setDebugEnabled(true)
                        .setOAuthConsumerKey("Zn4nPDBCy6AQctonRzVGlAsHj")
                        .setOAuthConsumerSecret("nLksHtxGDQFhMVJZRoh0BiOMPkQR2Gpu4OQsFLI6HTSsvhiWGA")
                        .setOAuthAccessToken("210685890-T4AN6FbswBPSQdbxIxVa599ojhBVbOxX8AgnBppu")
                        .setOAuthAccessTokenSecret("ca51aNstTB33j7NToPtKpXWVq8SkceiwmvKZYuMSxeUtb");
                Twitter twitter = new TwitterFactory(cb.build()).getInstance();

                try {
                    // chercher les TT, 1 = monde entier
                    Trend[] trends = twitter.getPlaceTrends(TWITTER_WOEID_WORLD).getTrends();

                    // si le 1er TT est dispo
                    if (trends[0] != null) {
                        Log.d("DEBUG TT", "First TT found");

                        String trendName = trends[0].getName(),
                                trendURL = trends[0].getURL();

                        // récupérer le précédent TT notifié
                        SharedPreferences sharedPreferences = contextThread.getSharedPreferences(SHARED_PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);
                        String lastTT = sharedPreferences.getString(SHARED_PREFERENCES_KEY, null);

                        // si aucun TT n'a été vu ou qu'il est différent du précédent sauvegardé
                        if(lastTT == null || !lastTT.equals(trendName)) {
                            Log.d("DEBUG TT", "New TT, saving in SharedPreferences");
                            // sauvegarder ce TT
                            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                            sharedPreferencesEditor.putString(SHARED_PREFERENCES_KEY, trendName);
                            sharedPreferencesEditor.commit();

                            // lancer la notification
                            notify(contextThread, trendName, trendURL);
                        }
                        // sinon, il s'agit du précédent TT notifié
                        else {
                            Log.d("DEBUG TT", "TT already notified");
                        }
                    }

                } catch (TwitterException e) {
                    e.printStackTrace();
                    Log.w("ERROR TT", "Network or Twitter services are unavailable");
                }
            }

            private void notify(Context contextNotifiy, String name, String url) {
                // ouvrir le navigateur sur l'url du TT
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                // paramétrage de la notification
                NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(contextNotifiy)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // visible partout (même sur lockscreen pour 5.x)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        // supprimée lorsque l'on clique dessus
                        .setAutoCancel(true)
                        // icone
                        .setSmallIcon(R.drawable.notification_twitter)
                        // contenu
                        .setContentTitle(contextNotifiy.getResources().getString(R.string.notification_title))
                        .setContentText(name + " " + contextNotifiy.getResources().getString(R.string.notification_content))
                        // lien navigateur
                        .setContentIntent(PendingIntent.getActivity(contextNotifiy, 0, intent, 0))
                        // activer la sonnerie par défaut
                        .setSound(Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.notif_sound))
                        //.setDefaults(Notification.DEFAULT_SOUND);
                ;

                // lancer la notification
                ((NotificationManager) contextNotifiy.getSystemService(Context.NOTIFICATION_SERVICE))
                        .notify(NOTIFICATION_ID, nBuilder.build());
            }

        });
        t.start();
    }
}
