package ch.ethz.inf.vs.a1.yedavid.sensors;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BaseSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

//import java.time.Instant;
import java.sql.Time;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SensorActivity extends AppCompatActivity implements SensorEventListener, SensorTypes, GraphContainer {

    long instant = System.currentTimeMillis();

    Sensor sensor;
    TextView sensorName;
    TextView sensorValue;

    int numValues;
    String unitString;

    GraphView graph;
    Viewport viewport;
    GridLabelRenderer renderer;

    List<DataPoint>[] dataPoints;

    private final int MAX_DATA_POINTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        sensorName = (TextView) findViewById(R.id.sensor_name);
        sensorValue = (TextView) findViewById(R.id.sensor_value);

        graph = (GraphView) findViewById(R.id.graph);
        viewport = graph.getViewport();
        renderer = graph.getGridLabelRenderer();


        Intent intent = getIntent();
        int sensorType = intent.getIntExtra("sensor_type", 0);

        numValues = getNumberValues(sensorType);
        unitString = getUnitString(sensorType);

        dataPoints = new List[numValues];
        for (int i = 0; i < numValues; i++) {
            dataPoints[i] = new ArrayList<>();
        }

        for (int i = 0; i < numValues; i++) {
            graph.addSeries(new PointsGraphSeries());
        }

        renderer.setHorizontalAxisTitle(unitString);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensorType);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        sensorName.setText(sensor.getName());
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        long time = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - instant);
        String string = "";
        addValues(time, sensorEvent.values);
        for (int i = 0; i < numValues; i++) {
            string = string.concat(String.valueOf(sensorEvent.values[i]) + unitString);
            if (i < numValues) {
                string = string.concat("\n");
            }
        }
        sensorValue.setText(string);
        renderer.setHorizontalAxisTitle(String.format("%02d s", time));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

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
            case Sensor.TYPE_POSE_6DOF:
                return 15;
            case Sensor.TYPE_STATIONARY_DETECT:
                return 1;
            case Sensor.TYPE_MOTION_DETECT:
                return 1;
            case Sensor.TYPE_HEART_BEAT:
                return 1;
            case Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT:
                return 1;
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
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
                return "µT";
            case Sensor.TYPE_GYROSCOPE:
                return "/s";
            case Sensor.TYPE_LIGHT:
                return "lux";
            case Sensor.TYPE_PRESSURE:
                return "hPa";
            case Sensor.TYPE_PROXIMITY:
                return "cm";
            case Sensor.TYPE_GRAVITY:
                return "m/s^2";
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return "m/s^2";
            case Sensor.TYPE_ROTATION_VECTOR:
                return "";
            case Sensor.TYPE_ORIENTATION:
                return "º";
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return "%";
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return "ºC";
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                return "µT";
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                return "";
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                return "/s";
            case Sensor.TYPE_POSE_6DOF:
                return "";
            case Sensor.TYPE_STATIONARY_DETECT:
                return "";
            case Sensor.TYPE_MOTION_DETECT:
                return "";
            case Sensor.TYPE_HEART_BEAT:
                return "";
            case Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT:
                return "";
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                return "m/s^2";
        }
        return null;
    }

    @Override
    public void addValues(double xIndex, float[] values) {
        List<Series> series = graph.getSeries();
        for (int i = 0; i < numValues; i++) {
            PointsGraphSeries lineGraph = (PointsGraphSeries) series.get(i);
            DataPoint dataPoint = new DataPoint(xIndex, values[i]);
            dataPoints[i].add(dataPoint);
            lineGraph.appendData(dataPoint, false, 100);
        }
    }

    @Override
    public float[][] getValues() {
        float[][] result = new float[numValues][MAX_DATA_POINTS];
        int i = 0;
        for (Series series : graph.getSeries()) {
            int j = 0;
            Iterator<DataPointInterface> iter = series.getValues(0, MAX_DATA_POINTS);
            for (; iter.hasNext(); ) {
                result[i][j++] = (float) iter.next().getY();
            }
            i++;
        }
        return result;
    }
}
