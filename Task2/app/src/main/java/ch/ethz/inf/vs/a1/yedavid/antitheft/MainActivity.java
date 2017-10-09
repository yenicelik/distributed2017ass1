package ch.ethz.inf.vs.a1.yedavid.antitheft;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.FloatMath;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity{

    public static boolean alarmIsActive = false;
    private Intent antiTheftServiceIntent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final View button = findViewById(R.id.toggleAlarmButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alarmIsActive = ! alarmIsActive;
                //System.out.println("Alarm state is " + alarmIsActive);
                //System.out.println("Toggle Alarm!");

                if (alarmIsActive) {
                    System.out.println("Starting service AntiTheftService..");
                    antiTheftServiceIntent.putExtra("startService", alarmIsActive);
                    startService(antiTheftServiceIntent);
                } else {
                    System.out.println("Stopping service AntiTheftService..");
                    antiTheftServiceIntent.putExtra("startService", alarmIsActive);
                    startService(antiTheftServiceIntent);
                }

            }
        });

        //Setting up the services
        antiTheftServiceIntent = new Intent(this, AntiTheftService.class);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            System.out.println("(Adjective) Settings selected");

            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
