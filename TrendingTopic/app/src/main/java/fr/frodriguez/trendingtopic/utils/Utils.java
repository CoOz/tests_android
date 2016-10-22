package fr.frodriguez.trendingtopic.utils;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import fr.frodriguez.trendingtopic.R;
import fr.frodriguez.trendingtopic.TTService;

/**
 * By Florian Rodriguez on 14/07/2016.
 */
public class Utils {

    public final static String PREFIX = "flz";

    public final static String TWITTER_WOEID_LABELS[] = {"World", "France", "US", "UK"};
    public final static int    TWITTER_WOEID_VALUES[] = {1, 23424819, 23424977, 23424975};

    public final static String  SHARED_PREF                         = "fr.frodriguez.trendingtopic.lastTT";
    public final static String  SHARED_PREF_TT                      = "last_tt";
    public final static String  SHARED_PREF_TT_DEFAULT              = "";
    public final static String  SHARED_PREF_BOOT_ENABLED            = "boot_enabled";
    public final static boolean SHARED_PREF_BOOT_ENABLED_DEFAULT    = true;
    public final static String  SHARED_PREF_WOEID                   = "woeid";
    public final static int     SHARED_PREF_WOEID_DEFAULT           = TWITTER_WOEID_VALUES[0];

    public final static int NOTIFICATION_ID = 0;

    public final static String OAUTH_CONSUMER_KEY        = "Zn4nPDBCy6AQctonRzVGlAsHj";
    public final static String OAUTH_CONSUMER_SECRET     = "nLksHtxGDQFhMVJZRoh0BiOMPkQR2Gpu4OQsFLI6HTSsvhiWGA";
    public final static String OAUTH_ACCESS_TOKEN        = "210685890-T4AN6FbswBPSQdbxIxVa599ojhBVbOxX8AgnBppu";
    public final static String OAUTH_ACCESS_TOKEN_SECRET = "ca51aNstTB33j7NToPtKpXWVq8SkceiwmvKZYuMSxeUtb";


    public static boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(TTService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String buildTag(String appName, String version) {
        return PREFIX + "_" + appName + ">" + version;
    }

    public static String getAppVersion(Context context) {
        String version = "?";
        if(context != null) {
            try {
                version = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
                version += "-" + context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(Utils.class.getName()+".getAppVersion()", "Version name not found");
            }
        }
        return version;
    }

    public static String getAppName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    public static  void notify(Context context, int id, int icon, String url, String title, String message, Uri sound) {
        // open the browser on the TT url
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        // set up the notification
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true) // delete when touch
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, 0)) // browser link
                .setSound(sound)
                ;
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(id, nBuilder.build());
    }

}
