package fr.frodriguez.trendingtopic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Florian Rodriguez on 16/01/16.
 */
public final class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Utils.LOG_TAG, "BOOT_COMPLETE received");

        // start the TTService if enabled
        SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.SHARED_PREF, Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean(Utils.SHARED_PREF_BOOT_ENABLED, Utils.SHARED_PREF_BOOT_ENABLED_DEFAULT)) {
            context.startService(new Intent(context, TTService.class));
        }
    }

}
