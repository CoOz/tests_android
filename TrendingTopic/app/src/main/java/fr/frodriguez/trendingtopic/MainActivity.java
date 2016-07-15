package fr.frodriguez.trendingtopic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    protected final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.SHARED_PREF, Context.MODE_PRIVATE);

        Switch switchEnabledButton = (Switch) findViewById(R.id.switchEnabledButton);
        switchEnabledButton.setChecked(Utils.isServiceRunning(this));

        Switch switchBootButton = (Switch) findViewById(R.id.switchBootButton);
        switchBootButton.setChecked(sharedPreferences.getBoolean(Utils.SHARED_PREF_BOOT_ENABLED, Utils.SHARED_PREF_BOOT_ENABLED_DEFAULT));

        // get the selected woeid to set the spinner selection
        int selectedWoied = sharedPreferences.getInt(Utils.SHARED_PREF_WOEID, Utils.SHARED_PREF_WOEID_DEFAULT);
        int indexWoied = Arrays.binarySearch(Utils.TWITTER_WOEID_VALUES, selectedWoied);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, Utils.TWITTER_WOEID_LABELS));
        spinner.setSelection(indexWoied);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(Utils.LOG_TAG, "Location selected: " + Utils.TWITTER_WOEID_LABELS[position] + "/" + Utils.TWITTER_WOEID_VALUES[position]);

                SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putInt(Utils.SHARED_PREF_WOEID, Utils.TWITTER_WOEID_VALUES[position]);
                sharedPreferencesEditor.commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /** Enable or disable the service */
    public void onClickEnabledSwitch(View view) {
        if(((Switch)view).isChecked()) {
            this.startService(new Intent(this, TTService.class));
            Toast.makeText(this, getResources().getString(R.string.text_start), Toast.LENGTH_SHORT).show();
        }
        else {
            this.stopService(new Intent(this, TTService.class));
            Toast.makeText(this, getResources().getString(R.string.text_stop), Toast.LENGTH_SHORT).show();
        }
    }

    /** Enable or disable the service on device boot */
    public void onClickBootSwitch(View view) {
        boolean enabled = ((Switch)view).isChecked();
        Log.d(Utils.LOG_TAG, "Enabled on boot ? " + enabled);

        SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(Utils.SHARED_PREF_BOOT_ENABLED, enabled);
        sharedPreferencesEditor.commit();
    }
}