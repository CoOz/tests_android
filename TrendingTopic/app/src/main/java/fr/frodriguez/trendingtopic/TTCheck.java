package fr.frodriguez.trendingtopic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Florian Rodriguez on 16/01/16.
 */
public class TTCheck {

    public static void check(Context context) {
        Log.d("DEBUG TT", "checking TT");

        // service d'authentification pour utiliser l'api twitter
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("Zn4nPDBCy6AQctonRzVGlAsHj")
                .setOAuthConsumerSecret("nLksHtxGDQFhMVJZRoh0BiOMPkQR2Gpu4OQsFLI6HTSsvhiWGA")
                .setOAuthAccessToken("210685890-T4AN6FbswBPSQdbxIxVa599ojhBVbOxX8AgnBppu")
                .setOAuthAccessTokenSecret("ca51aNstTB33j7NToPtKpXWVq8SkceiwmvKZYuMSxeUtb");
        Twitter twitter = new TwitterFactory(cb.build()).getInstance();

        try {
            // chercher les TT, 1 = monde entier
            Trends placeTrends = twitter.getPlaceTrends(1);
            Trend[] trends = placeTrends.getTrends();


            // si le 1er TT est dispo
            if(trends[0] != null) {
                Log.d("DEBUG TT", "First TT found");

                //TODO stocker le dernier TT et vérifier avant notification

                // lancer la notification
                notify(context, trends[0].getName(), trends[0].getURL());
            }

        } catch (TwitterException e) {
            e.printStackTrace();
            Log.w("ERROR TT", "Network or Twitter services are unavailable");
        }
    }


    // notification du nouveau TT
    private static void notify(Context context, String name, String url) {
        // ouvrir le navigateur sur l'url du TT
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        // paramétrage de la notification
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // visible partout (même sur lockscreen pour 5.x)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                // supprimée lorsque l'on clique dessus
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notification_twitter)
                .setContentTitle(context.getResources().getString(R.string.notification_title))
                .setContentText(name + " " + context.getResources().getString(R.string.notification_content))
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, 0))
                // activer la sonnerie par défaut
                .setDefaults(Notification.DEFAULT_SOUND);

        // lancer la notification
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(0, nBuilder.build());
    }

}
