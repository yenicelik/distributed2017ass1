package ch.ethz.inf.vs.a1.yedavid.antitheft;

import ch.ethz.inf.vs.a1.yedavid.antitheft.AlarmCallback;
import android.hardware.SensorEventListener;

public class SpikeMovementDetector extends AbstractMovementDetector implements SensorEventListener{

    public SpikeMovementDetector(AlarmCallback callback, int sensitivity) {
        super(callback, sensitivity);
    }

    @Override
    public boolean doAlarmLogic(float[] values) {
		// TODO, insert your logic here
        return false;
    }


}
