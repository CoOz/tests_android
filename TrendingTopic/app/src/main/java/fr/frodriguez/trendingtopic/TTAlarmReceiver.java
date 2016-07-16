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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import twitter4j.Trend;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Florian Rodriguez on 18/01/16.
 */
public class TTAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(Utils.LOG_TAG, "AlarmManager onReceive");

        // thread because of internet access
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // authentification to use twitter api
                ConfigurationBuilder cb = new ConfigurationBuilder();
                cb.setDebugEnabled(true)
                        .setOAuthConsumerKey("Zn4nPDBCy6AQctonRzVGlAsHj")
                        .setOAuthConsumerSecret("nLksHtxGDQFhMVJZRoh0BiOMPkQR2Gpu4OQsFLI6HTSsvhiWGA")
                        .setOAuthAccessToken("210685890-T4AN6FbswBPSQdbxIxVa599ojhBVbOxX8AgnBppu")
                        .setOAuthAccessTokenSecret("ca51aNstTB33j7NToPtKpXWVq8SkceiwmvKZYuMSxeUtb");
                Twitter twitter = new TwitterFactory(cb.build()).getInstance();

                try {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.SHARED_PREF, Context.MODE_PRIVATE);

                    // get TTs from woeid
                    int woeid = sharedPreferences.getInt(Utils.SHARED_PREF_WOEID, Utils.SHARED_PREF_WOEID_DEFAULT);
                    Trend[] trends = twitter.getPlaceTrends(woeid).getTrends();

                    // if the 1st TT is available
                    if (trends[0] != null) {
                        Log.d(Utils.LOG_TAG, "TT found for woeid " + woeid);

                        String trendName = trends[0].getName(),
                                trendURL = trends[0].getURL();

                        // get already notified TTs
                        String TTStr = sharedPreferences.getString(Utils.SHARED_PREF_TT, Utils.SHARED_PREF_TT_DEFAULT);
                        JSONObject TTJson;
                        if(TTStr.equals("")) {
                            TTJson = new JSONObject();
                        } else {
                            TTJson = new JSONObject(TTStr);
                        }
                        // get current date
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        String today = dateFormat.format(calendar.getTime());

                        // if the TT is not already notified of if it was not notified today
                        if( !TTJson.has(trendName) || !TTJson.getString(trendName).equals(today) ) {
                            // save the TT
                            TTJson.put(trendName, today);
                            Log.d(Utils.LOG_TAG, "Saving the new TT in SharedPreferences");
                            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                            sharedPreferencesEditor.putString(Utils.SHARED_PREF_TT, TTJson.toString());
                            sharedPreferencesEditor.commit();

                            // notify the user
                            notify(context, trendName, trendURL);
                        }
                        else {
                            Log.d(Utils.LOG_TAG, "TT already notified");
                        }
                        Log.d(Utils.LOG_TAG, "SharedPreferences state: " + TTJson.toString());
                    }
                } catch (TwitterException e) {
                    Log.e(Utils.LOG_TAG, "Network or Twitter services are unavailable", e);
                } catch (JSONException e) {
                    Log.e(Utils.LOG_TAG, "Error parsing JSON data", e);
                }
            }

            private void notify(Context contextNotifiy, String name, String url) {
                // open the browser on the TT url
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                // set up the notification
                NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(contextNotifiy)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setAutoCancel(true) // delete when touch
                        .setSmallIcon(R.drawable.notification_twitter)
                        .setContentTitle(contextNotifiy.getResources().getString(R.string.notification_title))
                        .setContentText(name + " " + contextNotifiy.getResources().getString(R.string.notification_content))
                        .setContentIntent(PendingIntent.getActivity(contextNotifiy, 0, intent, 0)) // browser link
                        .setSound(Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.notif_sound))
                        //.setDefaults(Notification.DEFAULT_SOUND);
                ;
                ((NotificationManager) contextNotifiy.getSystemService(Context.NOTIFICATION_SERVICE))
                        .notify(Utils.NOTIFICATION_ID, nBuilder.build());
            }
        });
        t.start();
    }
}
