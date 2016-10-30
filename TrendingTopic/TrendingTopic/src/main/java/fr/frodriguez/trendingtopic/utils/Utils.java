package fr.frodriguez.trendingtopic.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import fr.frodriguez.trendingtopic.TTService;

/**
 * By Florian Rodriguez on 14/07/2016.
 */
public class Utils {

    /**
     * Return whether the debug mode is enabled or not
     * @return true if the debug mode is enabled, false if not
     */
    public static boolean isDebugEnabled() {
        return Settings.DEBUG_ENABLED;
    }

    /**
     * Set the debug mode
     * @param debugEnabled true to enable the debug mode, false to disable it
     */
    public static void setDebugEnabled(boolean debugEnabled) {
        Settings.DEBUG_ENABLED = debugEnabled;
    }

    /**
     * Return whether the given service is running or not
     * @param context context
     * @param serviceClass the service to test
     * @return true if the service is currently running, false if not
     */
    public static boolean isServiceRunning(Context context, Class serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the application version and the application name in a string
     * @param context
     * @return
     */
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

    /**
     * Get the application name
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    /**
     * Create a notification with a browser link
     * @param context
     * @param notificationId id of the notification used to clear it
     * @param icon icon for the notification
     * @param url url to open when clicking on the notification
     * @param title title of the notification
     * @param message content message of the notification
     * @param sound sound played when the notification appears
     */
    public static  void notify(Context context, int notificationId, int icon, String url, String title, String message, Uri sound) {
        // open the browser on the TT url
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        browserIntent.setData(Uri.parse(url));
        PendingIntent browserPendingIntent = PendingIntent.getActivity(context, 0, browserIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // set up the notification
        Notification.Builder nBuilder = new Notification.Builder(context)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true) // delete when touch
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(browserPendingIntent) // browser link
                .setSound(sound);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            nBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(notificationId, nBuilder.build());
    }

}
