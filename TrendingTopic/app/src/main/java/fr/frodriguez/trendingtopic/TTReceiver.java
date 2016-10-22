package fr.frodriguez.trendingtopic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import fr.frodriguez.trendingtopic.utils.Logger;
import fr.frodriguez.trendingtopic.utils.TTIntents;
import fr.frodriguez.trendingtopic.utils.Utils;
import twitter4j.Trend;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * By Florian Rodriguez on 16/01/16.
 */
public final class TTReceiver extends BroadcastReceiver {

    private Logger _log;


    @Override
    public void onReceive(Context context, Intent intent) {
        _log = new Logger(Utils.getAppName(context), context);

        String action = intent.getAction();
        _log.d("Intent received: " + action);

        // on boot, start the TTService if enabled
        if(action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.SHARED_PREF, Context.MODE_PRIVATE);
            if(sharedPreferences.getBoolean(Utils.SHARED_PREF_BOOT_ENABLED, Utils.SHARED_PREF_BOOT_ENABLED_DEFAULT)) {
                context.startService(new Intent(context, TTService.class));
            }
        }
        // on alarm received, check the new TTs
        else if(action.equals(TTIntents.CHECK_TT)) {
            checkTT(context);
        }
    }


    /**
     * Check the new TTs and create a notification if the last TT was not notified yet
     */
    public void checkTT(final Context context) {
        _log.d("AlarmManager onReceive");

        // thread to prevent NetworkOnMainThreadException Exception
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // authentification to use twitter api
                ConfigurationBuilder cb = new ConfigurationBuilder();
                cb.setDebugEnabled(true)
                        .setOAuthConsumerKey(Utils.OAUTH_CONSUMER_KEY)
                        .setOAuthConsumerSecret(Utils.OAUTH_CONSUMER_SECRET)
                        .setOAuthAccessToken(Utils.OAUTH_ACCESS_TOKEN)
                        .setOAuthAccessTokenSecret(Utils.OAUTH_ACCESS_TOKEN_SECRET);
                Twitter twitter = new TwitterFactory(cb.build()).getInstance();

                try {
                    SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.SHARED_PREF, Context.MODE_PRIVATE);

                    // get TTs from woeid
                    int woeid = sharedPreferences.getInt(Utils.SHARED_PREF_WOEID, Utils.SHARED_PREF_WOEID_DEFAULT);
                    Trend[] trends = twitter.getPlaceTrends(woeid).getTrends();

                    // if the 1st TT is available
                    if (trends[0] != null) {
                        _log.d("TT found for woeid " + woeid);

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
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.FRANCE);
                        String today = dateFormat.format(calendar.getTime());

                        // if the TT is not already notified of if it was not notified today
                        if( !TTJson.has(trendName) || !TTJson.getString(trendName).equals(today) ) {
                            // save the TT
                            TTJson.put(trendName, today);
                            _log.d("Saving the new TT in SharedPreferences");
                            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                            sharedPreferencesEditor.putString(Utils.SHARED_PREF_TT, TTJson.toString());
                            sharedPreferencesEditor.apply();

                            // notify the user
                            Utils.notify(
                                    context,
                                    Utils.NOTIFICATION_ID,
                                    R.drawable.notification_twitter,
                                    context.getResources().getString(R.string.notification_title),
                                    trendURL,
                                    trendName + " " + context.getResources().getString(R.string.notification_content),
                                    Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.notif_sound)
                            );
                        }
                        else {
                            _log.d("TT already notified");
                        }
                        _log.d("SharedPreferences state: " + TTJson.toString());
                    }
                } catch (TwitterException e) {
                    _log.e("Network or Twitter services are unavailable", e);
                } catch (JSONException e) {
                    _log.e("Error parsing JSON data", e);
                }
            }

        });
        t.start();
    }


}
