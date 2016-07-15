package fr.frodriguez.trendingtopic;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by Florian on 14/07/2016.
 */
public class Utils {
    protected static String LOG_TAG = "FLZ_TrendingTopic";

    protected static String TWITTER_WOEID_LABELS[] = {"World", "France", "US", "UK"};
    protected static int    TWITTER_WOEID_VALUES[] = {1, 23424819, 23424977, 23424975};

    protected static String  SHARED_PREF = "fr.frodriguez.trendingtopic.lastTT";
    protected static String  SHARED_PREF_TT = "last_tt";
    protected static String  SHARED_PREF_BOOT_ENABLED = "boot_enabled";
    protected static boolean SHARED_PREF_BOOT_ENABLED_DEFAULT = true;
    protected static String  SHARED_PREF_WOEID = "woeid";
    protected static int     SHARED_PREF_WOEID_DEFAULT = TWITTER_WOEID_VALUES[0];

    protected static int NOTIFICATION_ID = 0;


    protected static boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TTService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
