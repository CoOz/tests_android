package fr.frodriguez.trendingtopic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import fr.frodriguez.trendingtopic.utils.Logger;
import fr.frodriguez.trendingtopic.utils.Utils;

/**
 * By Florian Rodriguez on 16/01/16.
 */
public class MainActivity extends AppCompatActivity {

    private final Context _context = this;
    private Logger _log;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _log = new Logger(Utils.getAppName(this), this);

        // set app infos
        TextView textView = (TextView) findViewById(R.id.textViewVersion);
        textView.setText(Utils.getAppName(this) + " " + Utils.getAppVersion(this));

        // set switch values
        final SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREF, Context.MODE_PRIVATE);

        ((Switch) findViewById(R.id.switchEnabledButton)).setChecked(Utils.isServiceRunning(this));
        ((Switch) findViewById(R.id.switchBootButton)).setChecked(sharedPreferences.getBoolean(Utils.SHARED_PREF_BOOT_ENABLED, Utils.SHARED_PREF_BOOT_ENABLED_DEFAULT));

        // set the spinner values and the selected woeid
        int selectedWoied = sharedPreferences.getInt(Utils.SHARED_PREF_WOEID, Utils.SHARED_PREF_WOEID_DEFAULT);
        int indexWoied = Arrays.binarySearch(Utils.TWITTER_WOEID_VALUES, selectedWoied);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, Utils.TWITTER_WOEID_LABELS));
        spinner.setSelection(indexWoied);
        // set the woeid
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _log.d("Location selected: " + Utils.TWITTER_WOEID_LABELS[position] + "/" + Utils.TWITTER_WOEID_VALUES[position]);

                SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putInt(Utils.SHARED_PREF_WOEID, Utils.TWITTER_WOEID_VALUES[position]);
                sharedPreferencesEditor.apply();

                _context.startService(new Intent(_context, TTService.class));
                Toast.makeText(_context, getResources().getString(R.string.location_updated), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
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

        SharedPreferences sharedPreferences = _context.getSharedPreferences(Utils.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(Utils.SHARED_PREF_BOOT_ENABLED, enabled);
        sharedPreferencesEditor.apply();
    }


    private int easterEggCpt = 0;
    @SuppressWarnings("deprecation")
    public void easterEgg(View view) {
        if(easterEggCpt < 10) {
            ++easterEggCpt;
        } else {
            Drawable easterEgg;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                easterEgg = getDrawable(R.drawable.triforce);
            } else {
                easterEgg = getResources().getDrawable(R.drawable.triforce);
            }
            ((TextView) findViewById(R.id.textViewVersion)).setCompoundDrawablesWithIntrinsicBounds(easterEgg, null, easterEgg, null);
        }
    }
}