package fr.frodriguez.trendingtopic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import fr.frodriguez.trendingtopic.utils.Logger;
import fr.frodriguez.trendingtopic.utils.Settings;
import fr.frodriguez.trendingtopic.utils.Utils;

/**
 * By Florian Rodriguez on 16/01/16.
 */
public class MainActivity extends Activity {

    private final Context _context = this;
    private Logger _log;
    private SharedPreferences _sp;
    static boolean _initialized = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _log = new Logger(Utils.getAppName(this), this);
        _sp = getSharedPreferences(Settings.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        // set app infos
        TextView textView = (TextView) findViewById(R.id.textViewVersion);
        textView.setText(Utils.getAppName(this) + " " + Utils.getAppVersion(this));

        // set switch values
        ((Switch) findViewById(R.id.switchDebug))
                .setChecked(Utils.isDebugEnabled());
        ((Switch) findViewById(R.id.switchEnabled))
                .setChecked(Utils.isServiceRunning(this, TTService.class));
        ((Switch) findViewById(R.id.switchBoot))
                .setChecked(_sp.getBoolean(Settings.SP_BOOT_ENABLED, Settings.SP_BOOT_ENABLED_DEFAULT));

        // set the spinner values and the selected woeid
        int selectedWoied = _sp.getInt(Settings.SP_WOEID, Settings.SP_WOEID_DEFAULT);
        int indexWoied = Arrays.binarySearch(Settings.TWITTER_WOEID_VALUES, selectedWoied);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setSelection(indexWoied, false);
        spinner.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, Settings.TWITTER_WOEID_LABELS));

        // on location/woeid update
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor spe = _sp.edit();
                spe.putInt(Settings.SP_WOEID, Settings.TWITTER_WOEID_VALUES[position]);
                spe.apply();

                // this method is called even on the spinner.setSelection instruction above
                // the service don't have to be started in this case
                if(!_initialized) {
                    _initialized = true;
                    _log.d("Location initialized: " + Settings.TWITTER_WOEID_LABELS[position] + "/" + Settings.TWITTER_WOEID_VALUES[position]);
                } else {
                    _context.startService(new Intent(_context, TTService.class));
                    Toast.makeText(_context, getResources().getString(R.string.location_updated), Toast.LENGTH_SHORT).show();
                    _log.d("Location selected: " + Settings.TWITTER_WOEID_LABELS[position] + "/" + Settings.TWITTER_WOEID_VALUES[position]);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // prevent the service to be started on onCreate method
        _initialized = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // prevent the service to be started on onCreate method
        _initialized = false;
    }

    /** Enable or disable the service */
    public void onClickEnabledSwitch(View view) {
        if(((Switch)view).isChecked()) {
            this.startService(new Intent(this, TTService.class));
            Toast.makeText(this, getResources().getString(R.string.service_enabled), Toast.LENGTH_SHORT).show();
        }
        else {
            this.stopService(new Intent(this, TTService.class));
            Toast.makeText(this, getResources().getString(R.string.service_disabled), Toast.LENGTH_SHORT).show();
        }
    }

    /** Enable or disable the service on device boot */
    public void onClickBootSwitch(View view) {
        boolean enabled = ((Switch)view).isChecked();
        _log.d("Enabled on boot ? " + enabled);

        SharedPreferences.Editor spe = _sp.edit();
        spe.putBoolean(Settings.SP_BOOT_ENABLED, enabled);
        spe.apply();
    }

    /** Clear already notified TT */
    public void onClickClearButton(View view) {
        _log.d("Clear already notified TT");

        SharedPreferences.Editor spe = _sp.edit();
        spe.clear();
        spe.apply();

        Toast.makeText(this, getResources().getString(R.string.ttCleared), Toast.LENGTH_SHORT).show();
    }

    /** Enable or disable the debug mode */
    public void onClickDebugSwitch(View view) {
        boolean enabled = ((Switch)view).isChecked();
        Utils.setDebugEnabled(enabled);
        _log.d("Debug mode " + (enabled ? "enabled" : "disabled"));
    }



    private int easterEggCpt = 0;
    @SuppressWarnings("deprecation")
    public void easterEgg(View view) {
        if(easterEggCpt <= 10) {
            ++easterEggCpt;
        }
        if(easterEggCpt == 10) {

            _log.d("It's dangerous to go alone! Take this.");
            Drawable easterEgg;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                easterEgg = getDrawable(R.drawable.triforce);
            } else {
                easterEgg = getResources().getDrawable(R.drawable.triforce);
            }
            ((TextView) findViewById(R.id.textViewVersion)).setCompoundDrawablesWithIntrinsicBounds(easterEgg, null, easterEgg, null);
            MediaPlayer.create(this, R.raw.secret).start();
        }
    }
}