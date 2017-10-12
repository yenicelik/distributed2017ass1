package ch.ethz.inf.vs.a1.yedavid.ble;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner mScanner;

    List<BluetoothDevice> listBluetoothDevice;
    ListAdapter adapterLeScanResult;

    boolean mScanning;
    Handler mHandler;

    private ListView mListView;

    // Stops scanning after 25 seconds.
    private static final long SCAN_PERIOD = 25000;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION }, 1);

        /*
        *  Implement a scan for available devices. You should limit the time of the scan and you should only
            show Sensirion HumiGadget devices (e.g. by using a ScanFilter). Scanning should stop once
            the user selects one of the listed devices, or the scan-time expires.
        * */

        mListView = (ListView) findViewById(R.id.bluetooth_list_view);

        listBluetoothDevice = new ArrayList<>();

        adapterLeScanResult = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listBluetoothDevice);
        mListView.setAdapter(adapterLeScanResult);
        mListView.setOnItemClickListener(scanResultOnItemClickListener);

        mScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mHandler = new Handler();
        scanDevices(true);

    }

    AdapterView.OnItemClickListener scanResultOnItemClickListener =
            new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);

                    Intent myIntent = new Intent(MainActivity.this, ConnectActivity.class);
                    myIntent.putExtra("device", device.getAddress()); //Optional parameters
                    MainActivity.this.startActivity(myIntent);
                }
            };

    // Device scan callback.
    // Add found device to device List
    private ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            addBluetoothDevice(result.getDevice());
        }

        private void addBluetoothDevice(BluetoothDevice device){
            if(!listBluetoothDevice.contains(device)){
                listBluetoothDevice.add(device);

                mListView.invalidateViews();
            }
        }

    };

    private void scanDevices(final boolean enable){

        if (enable) {
            listBluetoothDevice.clear();

            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {

                public void run() {
                    mScanning = false;
                    mScanner.stopScan(scanCallback);
                }
            }, SCAN_PERIOD);

            //scan specified devices only with ScanFilter
            ScanFilter scanFilter = new ScanFilter.Builder().setDeviceName("Smart Humigadget").build();

            List<ScanFilter> scanFilters = new ArrayList<>();
            scanFilters.add(scanFilter);

            ScanSettings scanSettings = new ScanSettings.Builder().build();

            mScanning = true;
            mScanner.startScan(scanFilters, scanSettings, scanCallback);

        } else {
            mScanning = false;
            mScanner.stopScan(scanCallback);
        }

    }

}
