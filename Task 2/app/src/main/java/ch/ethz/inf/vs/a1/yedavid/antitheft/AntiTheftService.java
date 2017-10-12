package ch.ethz.inf.vs.a1.yedavid.antitheft;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import android.os.Vibrator;

import java.util.concurrent.TimeUnit;
import android.os.Handler;

import static java.lang.reflect.Array.getInt;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AntiTheftService extends IntentService implements AlarmCallback {

    private static boolean alarmActive = false;
    private NotificationManager notificationManager;
    private SensorManager sensorManager;
    private SpikeMovementDetector spikeMovementDetector;
    private Sensor sensor;
    private NotificationCompat.Builder notificationBuilder;
    private Runnable mRunnable = null;
    private Handler handler = new Handler();

    public AntiTheftService() {
        super("AntiTheftService");
    }

    @Override
    public void onDelayStarted() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int delay = Integer.parseInt(sharedPreferences.getString("time_interval", "10"));

        final Context context = this;
        mRunnable = new Runnable() {
            public void run() {

                System.out.println("\n\n\n\n\n\nCalling onDelayStarted \n\n\n\n\n\n");
                notificationBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_info_black_24dp)
                        .setContentTitle("I am a notification")
                        .setContentText("Above is a notification")
                        .setOngoing(true);
                notificationManager.notify(1000, notificationBuilder.build());
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                //Different behavior when the phone is connected vs not
                try {
                    v.vibrate(1000); //TODO: possibly repeat this in a time-interval if it's needed to conitnuously vibrate
                } catch (Exception e) {
                    System.out.println("Brbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbrbr");
                    System.out.println(e);
                }
            }
        };
        handler.postDelayed(mRunnable, delay * 1000);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }

    public void clearNotifications() {
        System.out.println("\n\n\n\n\n\nClearing Notifications \n\n\n\n\n\n");
        notificationManager.cancelAll();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("\n\n\n\n\n\nStarting the command \n\n\n\n\n\n");

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sensorType = sharedPreferences.getString("pref_sensorType", "Linear");
        int sensitivity = Integer.parseInt(sharedPreferences.getString("sensitivity", "1"));

        if (sensorType.equals("Linear")) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        } else {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MOTION_DETECT);
        }

        spikeMovementDetector = new SpikeMovementDetector(this, sensitivity);
        alarmActive = intent.getBooleanExtra("startService", true);

        if (alarmActive) {
            sensorManager.registerListener(spikeMovementDetector, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            clearNotifications();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        System.out.println("\n\n\n\n\n\nDestroying\n\n\n\n\n\n");

        if (mRunnable != null) {
            handler.removeCallbacks(mRunnable);
        }

        clearNotifications();
        sensorManager.unregisterListener(spikeMovementDetector, sensor);
        super.onDestroy();
    }
}
