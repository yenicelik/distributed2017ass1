package ch.ethz.inf.vs.a1.yedavid.sensors;


import android.hardware.Sensor;

public class SensorHelper implements SensorTypes {

    @Override
    public int getNumberValues(int sensorType) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                return 3;
            case Sensor.TYPE_MAGNETIC_FIELD:
                return 3;
            case Sensor.TYPE_GYROSCOPE:
                return 3;
            case Sensor.TYPE_LIGHT:
                return 1;
            case Sensor.TYPE_PRESSURE:
                return 1;
            case Sensor.TYPE_PROXIMITY:
                return 1;
            case Sensor.TYPE_GRAVITY:
                return 3;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return 3;
            case Sensor.TYPE_ROTATION_VECTOR:
                return 5;
            case Sensor.TYPE_ORIENTATION:
                return 3;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return 1;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return 1;
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                return 6;
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                return 5;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                return 6;
        }
        return 0;
    }

    @Override
    public String getUnitString(int sensorType) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                return "m/s^2";
            case Sensor.TYPE_MAGNETIC_FIELD:
                return "microT";
            case Sensor.TYPE_GYROSCOPE:
                return "rad/s";
            case Sensor.TYPE_LIGHT:
                return "lx";
            case Sensor.TYPE_PRESSURE:
                return "hPa";
            case Sensor.TYPE_PROXIMITY:
                return "cm";
            case Sensor.TYPE_GRAVITY:
                return "m/s^2";
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return "m/s^2";
            case Sensor.TYPE_ROTATION_VECTOR:
                return "no unit";
            case Sensor.TYPE_ORIENTATION:
                return "°";
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return "%";
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return "°C";
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                return "µT";
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                return "";
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                return "/s";
        }
        return null;
    }

}
