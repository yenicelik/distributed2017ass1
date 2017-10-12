package ch.ethz.inf.vs.a1.yedavid.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Switch;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.ethz.inf.vs.a1.yedavid.ble.SensirionSHT31UUIDS.*;

public class ConnectActivity extends AppCompatActivity implements GraphContainer {

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    private Boolean humidity_set = false;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTED = 2;

    private static final String BLUETOOTH_GATT_CALLBACK_TAG = "BluetoothGattCallback";
    private static final String CONNECTING_SERVER_TAG = "Connecting to Server";
    private static final String SERVICE_DISCOVERY_TAG = "Service Discovery";

    private int numValues = 2;
    private List<DataPoint>[] dataPoints = new ArrayList[]{new ArrayList<>(), new ArrayList<>()};

    private GraphView graph;
    private Viewport viewport;
    private GridLabelRenderer renderer;

    private long instant = System.currentTimeMillis();
    private long latest = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        Intent intent = getIntent();
        mBluetoothDeviceAddress = intent.getStringExtra("device");

        // Initializes Bluetooth adapter.
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        bleConnect(mBluetoothDeviceAddress);

        graph = (GraphView) findViewById(R.id.graph);
        viewport = graph.getViewport();
        renderer = graph.getGridLabelRenderer();

        LineGraphSeries series1 = new LineGraphSeries();
        series1.setColor(Color.CYAN);
        graph.addSeries(series1);

        LineGraphSeries series2 = new LineGraphSeries();
        series2.setColor(Color.GREEN);
        graph.addSeries(series2);

        renderer.setLabelFormatter(new LabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                return (isValueX) ? String.valueOf(value / 1000) : null;
            }

            @Override
            public void setViewport(Viewport viewport) {
                return;
            }
        });



    }

    private void bleConnect(String address){

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
    }

    // Various callback methods defined by the BLE API.
    private final BluetoothGattCallback mGattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {

                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        mConnectionState = STATE_CONNECTED;

                        Log.i(CONNECTING_SERVER_TAG, "Connected to GATT server.");
                        Log.i(SERVICE_DISCOVERY_TAG, "Attempting to start service discovery:" +
                                mBluetoothGatt.discoverServices());
                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {

                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        Log.d(BLUETOOTH_GATT_CALLBACK_TAG, "onServicesDiscovered: Found Service");

                        setTemperature(gatt);

                    } else {
                        //Log.d(TAG, "onServicesDiscovered: " + status);
                    }

                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

                    final long time = System.currentTimeMillis() - instant;

                    if (time <= latest){
                        return;
                    }

                    latest = time;

                    final AtomicBoolean done = new AtomicBoolean(false);
                    Runnable runnable = null;
                    if (UUID_TEMPERATURE_CHARACTERISTIC.equals(characteristic.getUuid())) {

                        final float temperature = convertRawValue(characteristic.getValue());
                        final LineGraphSeries lineGraph = (LineGraphSeries) graph.getSeries().get(0);


                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                synchronized (this) {
                                    lineGraph.appendData(new DataPoint(time, (double) temperature), false, 100);
                                    done.set(true);
                                    this.notify();
                                }
                            }
                        };

                    } else if (UUID_HUMIDITY_CHARACTERISTIC.equals(characteristic.getUuid())) {

                        final float humidity = convertRawValue(characteristic.getValue());
                        final LineGraphSeries lineGraph = (LineGraphSeries) graph.getSeries().get(1);

                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                synchronized (this) {
                                    lineGraph.appendData(new DataPoint(time, (double) humidity), false, 100);
                                    done.set(true);
                                    this.notify();
                                }
                            }
                        };
                    }

                    if (runnable != null) {
                        runOnUiThread(runnable);
                        try {
                            synchronized (runnable) {
                                while (!done.get()) {
                                    runnable.wait();
                                }
                            }
                        } catch (Exception e) {

                        }
                    }

                    renderer.setHorizontalAxisTitle(String.format("%02d s", TimeUnit.MILLISECONDS.toSeconds(time)));
                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

                    if (!humidity_set){
                        humidity_set = true;
                        setHumidity(gatt);
                    }
                }

                private void setTemperature(BluetoothGatt gatt) {

                    // Service
                    BluetoothGattService tempService = gatt.getService(UUID_TEMPERATURE_SERVICE);

                    if (tempService != null) {

                        // Characteristic (containing a single value)
                        BluetoothGattCharacteristic tempCharacteristic = new BluetoothGattCharacteristic(
                                UUID_TEMPERATURE_CHARACTERISTIC,
                                BluetoothGattCharacteristic.FORMAT_UINT16,
                                BluetoothGattCharacteristic.PERMISSION_WRITE
                        );

                        tempService.addCharacteristic(tempCharacteristic);
                        gatt.setCharacteristicNotification(tempCharacteristic, true);

                        // Characteristics Descriptors (describing the characteristic’s value)
                        BluetoothGattDescriptor tempDescriptor = new BluetoothGattDescriptor(
                                NOTIFICATION_DESCRIPTOR_UUID,
                                BluetoothGattDescriptor.PERMISSION_WRITE
                        );

                        tempDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        tempCharacteristic.addDescriptor(tempDescriptor);
                        gatt.writeDescriptor(tempDescriptor);
                    }
                }

                private void setHumidity(BluetoothGatt gatt) {

                    BluetoothGattService humidityService = gatt.getService(UUID_HUMIDITY_SERVICE);

                    if (humidityService != null) {

                        BluetoothGattCharacteristic humidityCharacteristic = new BluetoothGattCharacteristic(
                                UUID_HUMIDITY_CHARACTERISTIC,
                                BluetoothGattCharacteristic.FORMAT_UINT16,
                                BluetoothGattCharacteristic.PERMISSION_WRITE
                        );

                        humidityService.addCharacteristic(humidityCharacteristic);
                        gatt.setCharacteristicNotification(humidityCharacteristic, true);

                        BluetoothGattDescriptor humidityDescriptor = new BluetoothGattDescriptor(
                                NOTIFICATION_DESCRIPTOR_UUID,
                                BluetoothGattDescriptor.PERMISSION_WRITE
                        );

                        humidityDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        humidityCharacteristic.addDescriptor(humidityDescriptor);
                        gatt.writeDescriptor(humidityDescriptor);
                    }
                }

            };

    private float convertRawValue(byte[] raw) {
        ByteBuffer wrapper = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);

        return wrapper.getFloat();
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopConnections();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopConnections();
    }

    public void stopConnections() {

        if (mBluetoothGatt == null || mBluetoothAdapter == null) {
            Log.v("ALREADY_NULL", "Adapter or Manager already null");
            return;
        }

        if (mBluetoothAdapter.isEnabled()){

            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }

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