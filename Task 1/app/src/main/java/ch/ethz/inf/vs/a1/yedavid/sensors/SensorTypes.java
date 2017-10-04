package ch.ethz.inf.vs.a1.yedavid.sensors;

public interface SensorTypes {

    /**
     * Returns the number of values for a specific sensor type, e.g. Sensor.ACCELERATION
     * @param sensorType the sensor type id
     * @return the number of vales for the given sensor type
     */
    int getNumberValues(int sensorType);

    /**
     * Returns the unit for a specific sensor type, e.g. "m/s^2" for Sensor.ACCELERATION
     * @param sensorType the sensor type id
     * @return A string containing the unit for the given sensor type
     */
    String getUnitString(int sensorType);
}
