package ch.ethz.inf.vs.a1.yedavid.antitheft;
import java.util.stream.*;
import ch.ethz.inf.vs.a1.yedavid.antitheft.AlarmCallback;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;


public class SpikeMovementDetector extends AbstractMovementDetector {

    public static boolean triggered = false;

    public SpikeMovementDetector(AlarmCallback callback, int sensitivity) {
        super(callback, sensitivity);
    }


    @Override
    public boolean doAlarmLogic(float[] values) {
//        float sum = 0;
//        for (float ele : values) {
//            sum += ele;
//        }
//
//        this.triggered = sum < sensitivity;
//        return this.triggered;
        return true;

    }


}
