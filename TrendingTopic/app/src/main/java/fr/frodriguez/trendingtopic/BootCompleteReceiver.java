package fr.frodriguez.trendingtopic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Florian Rodriguez on 16/01/16.
 */
public final class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DEBUG TT", "BOOT_COMPLETE received");

        // démarrer TTservice
        context.startService(new Intent(context, TTService.class));
    }

}
