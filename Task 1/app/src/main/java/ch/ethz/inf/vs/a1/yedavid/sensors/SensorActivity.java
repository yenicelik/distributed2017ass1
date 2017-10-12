package ch.ethz.inf.vs.a1.yedavid.sensors;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SensorActivity extends AppCompatActivity implements SensorEventListener, GraphContainer {

    boolean test;

    long instant = System.currentTimeMillis();
    long latest = 0;

    SensorTypes sensorHelper = new SensorHelper();

    Sensor sensor;
    TextView sensorName;
    TextView sensorValue;

    int numValues;
    String unitString;

    GraphView graph;
    Viewport viewport;
    GridLabelRenderer renderer;

    List<DataPoint>[] dataPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        sensorName = findViewById(R.id.sensor_name);
        sensorValue = findViewById(R.id.sensor_value);

        graph = findViewById(R.id.graph);
        viewport = graph.getViewport();
        renderer = graph.getGridLabelRenderer();


        Intent intent = getIntent();
        int sensorType = intent.getIntExtra("sensor_type", 1);
        test = intent.getBooleanExtra("is_test", true);

        numValues = sensorHelper.getNumberValues(sensorType);
        unitString = sensorHelper.getUnitString(sensorType);

        dataPoints = new List[numValues];
        for (int i = 0; i < numValues; i++) {
            dataPoints[i] = new ArrayList<>();
        }

        final int[] colors = new int[]{
                Color.RED,
                Color.GREEN,
                Color.BLUE,
                Color.CYAN,
                Color.YELLOW,
                Color.MAGENTA,
                Color.BLACK,
                Color.GRAY,
        };

        for (int i = 0; i < numValues; i++) {
            PointsGraphSeries series = new PointsGraphSeries();
            series.setColor(Integer.MIN_VALUE / 4 + (Integer.MIN_VALUE / 2) / numValues * i);
            series.setColor(colors[i % colors.length]);
            graph.addSeries(series);

        }

        renderer.setVerticalAxisTitle(unitString);
        renderer.setLabelFormatter(new LabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                return (isValueX) ? String.valueOf(value / 1000) : null;
            }

            @Override
            public void setViewport(Viewport viewport) {

            }
        });

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensorType);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        sensorName.setText(sensor.getName());
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (test) {
            return;
        }
        long time = System.currentTimeMillis() - instant;

        if (time <= latest) {
            return;
        }

        latest = time;
        String string = "";
        addValues(time, sensorEvent.values);
        for (int i = 0; i < numValues; i++) {
            string = string.concat(String.valueOf(sensorEvent.values[i]) + unitString);
            if (i < numValues) {
                string = string.concat("\n");
            }
        }
        sensorValue.setText(string);
        renderer.setHorizontalAxisTitle(String.format("%02d s", TimeUnit.MILLISECONDS.toSeconds(time)));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public GraphContainer getGraphContainer() {
        return this;
    }

    @Override
    public void addValues(double xIndex, float[] values) throws IllegalArgumentException {
        List<Series> series = graph.getSeries();
        final Object monitor = new Object();
        final AtomicInteger counter = new AtomicInteger(numValues);
        for (int i = 0; i < numValues; i++) {
            final PointsGraphSeries lineGraph = (PointsGraphSeries) series.get(i);
            final DataPoint dataPoint = new DataPoint(xIndex, values[i]);

            dataPoints[i].add(dataPoint);
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    synchronized (monitor) {
                        lineGraph.appendData(dataPoint, false, 100);
                        counter.decrementAndGet();
                        monitor.notify();
                    }
                }
            };
            runOnUiThread(task);
        }

        try {
            synchronized (monitor) {
                while (counter.get() != 0) {
                    monitor.wait();
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public float[][] getValues() {
        List<Float>[] values = new ArrayList[numValues];
        for (int i = 0; i < values.length; i++) {
            values[i] = new ArrayList<>();
        }

        int i = 0;
        for (Series series : graph.getSeries()) {
            Iterator<DataPointInterface> iter = series.getValues(Double.MIN_VALUE, Double.MAX_VALUE);
            while (iter.hasNext()) {
                values[i].add((float) iter.next().getY());
            }
            i++;
        }
        int size = values[0].size();

        float[][] result = new float[size][numValues];

        for (i = 0; i < size; i++) {
            for (int j = 0; j < numValues; j++) {
                result[i][j] = values[j].get(i);
            }
        }

        return result;
    }

}
