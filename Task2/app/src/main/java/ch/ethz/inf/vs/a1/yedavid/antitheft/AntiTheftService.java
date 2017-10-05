package ch.ethz.inf.vs.a1.yedavid.antitheft;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AntiTheftService extends IntentService implements AlarmCallback {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "ch.ethz.inf.vs.a1.yedavid.antitheft.action.FOO";
    private static final String ACTION_BAZ = "ch.ethz.inf.vs.a1.yedavid.antitheft.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "ch.ethz.inf.vs.a1.yedavid.antitheft.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "ch.ethz.inf.vs.a1.yedavid.antitheft.extra.PARAM2";

    private static boolean alarmActive = false;

    public AntiTheftService() {
        super("AntiTheftService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method

    @Override
    public void onDelayStarted() {

        System.out.println("\n\nStarting from within nigguh!");


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context
                .NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_info_black_24dp)
                .setContentTitle("I am a notification")
                .setContentText("Above is a notification")
                .setOngoing(true);

        notificationManager.notify(1000, notificationBuilder.build());

    }


    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, AntiTheftService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, AntiTheftService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO: have some function to actually stop what onDelayStarted has started
        boolean alarmIsActive = intent.getBooleanExtra("startService", false); //does this now actually get true or false?
        alarmActive = alarmIsActive;

        if (alarmIsActive) {
            onDelayStarted();
        } else {
            //TODO: Some function to reverse the actions of the other shit
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
