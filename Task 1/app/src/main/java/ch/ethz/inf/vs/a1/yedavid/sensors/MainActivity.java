package ch.ethz.inf.vs.a1.yedavid.sensors;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Sensor> sensorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorList = ((SensorManager) getSystemService(SENSOR_SERVICE)).getSensorList(Sensor.TYPE_ALL);

        ListView listView = (ListView) findViewById(R.id.list_view);
        List<String> sensorNameList = new ArrayList<>();

        for (Sensor sensor : sensorList) {
            sensorNameList.add(sensor.getName());
        }

        ArrayAdapter stringListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_2, android.R.id.text1, sensorNameList);
        listView.setAdapter(stringListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Sensor sensor = sensorList.get(i);

                Intent intent = new Intent(getApplicationContext(), SensorActivity.class);
                intent.putExtra("sensor_type", sensor.getType());
                intent.putExtra("is_test", false);
                startActivity(intent);
            }
        });

    }

    protected void onItemClick(View v) {
        System.out.println(v);
    }
}
