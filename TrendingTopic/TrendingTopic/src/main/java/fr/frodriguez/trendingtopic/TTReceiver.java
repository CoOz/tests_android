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
import fr.frodriguez.trendingtopic.utils.Intents;
import fr.frodriguez.trendingtopic.utils.Settings;
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
            SharedPreferences sp = context.getSharedPreferences(Settings.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            if(sp.getBoolean(Settings.SP_BOOT_ENABLED, Settings.SP_BOOT_ENABLED_DEFAULT)) {
                context.startService(new Intent(context, TTService.class));
            }
        }
        // on alarm received, check the new TTs
        else if(action.equals(Intents.CHECK_TT)) {
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
                        .setOAuthConsumerKey(Settings.OAUTH_CONSUMER_KEY)
                        .setOAuthConsumerSecret(Settings.OAUTH_CONSUMER_SECRET)
                        .setOAuthAccessToken(Settings.OAUTH_ACCESS_TOKEN)
                        .setOAuthAccessTokenSecret(Settings.OAUTH_ACCESS_TOKEN_SECRET);
                Twitter twitter = new TwitterFactory(cb.build()).getInstance();

                try {
                    SharedPreferences sp = context.getSharedPreferences(Settings.SHARED_PREFERENCES, Context.MODE_PRIVATE);

                    // get TTs from woeid
                    int woeid = sp.getInt(Settings.SP_WOEID, Settings.SP_WOEID_DEFAULT);
                    Trend[] trends = twitter.getPlaceTrends(woeid).getTrends();

                    // if the 1st TT is available
                    if (trends[0] != null) {
                        _log.d("TT found for woeid " + woeid);

                        String trendName = trends[0].getName(),
                                trendURL = trends[0].getURL();

                        // get already notified TTs
                        String TTStr = sp.getString(Settings.SP_TT, Settings.SP_TT_DEFAULT);
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
                            SharedPreferences.Editor spe = sp.edit();
                            spe.putString(Settings.SP_TT, TTJson.toString());
                            spe.apply();

                            // notify the user
                            Utils.notify(
                                    context,
                                    Settings.NOTIFICATION_ID,
                                    R.drawable.notification_twitter,
                                    trendURL,
                                    context.getResources().getString(R.string.notification_title),
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
