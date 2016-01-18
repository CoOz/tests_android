package fr.frodriguez.trendingtopic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickStart(View view) {
        this.startService(new Intent(this, TTService.class));

        Toast.makeText(this, getResources().getString(R.string.text_start), Toast.LENGTH_SHORT).show();
    }

    public void onClickStop(View view) {
        this.stopService(new Intent(this, TTService.class));

        Toast.makeText(this, getResources().getString(R.string.text_stop), Toast.LENGTH_SHORT).show();
    }
}