package ch.ethz.inf.vs.a1.yedavid.antitheft;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity{

    public static boolean alarmIsActive = false;
    private Intent antiTheftServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        antiTheftServiceIntent = new Intent(this, AntiTheftService.class);

        final ToggleButton button = (ToggleButton) findViewById(R.id.toggleAlarmButton);
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
              alarmIsActive = ! alarmIsActive;

              if (alarmIsActive) {
                  System.out.println("Starting service AntiTheftService..");
                  antiTheftServiceIntent.putExtra("startService", alarmIsActive);
                  startService(antiTheftServiceIntent);
              } else {
                  System.out.println("Stopping service AntiTheftService..");
                  antiTheftServiceIntent.putExtra("startService", alarmIsActive);
                  stopService(antiTheftServiceIntent);
              }
          }
      }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void openSettings(Fragment fragment) {

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

            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .addToBackStack(null)
                    .commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
