package ch.ethz.inf.vs.a1.yedavid.sensors;//com.android_examples.sensorslist_android_examplescom;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView ;
    SensorManager sensorManager ;
    List<Sensor> listsensor;
    List<String> liststring ;
    ArrayAdapter<String> adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.listview1);

        liststring = new ArrayList<String>();

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        listsensor = sensorManager.getSensorList(Sensor.TYPE_ALL);

        for(int i=0; i<listsensor.size(); i++){
            liststring.add(listsensor.get(i).getName());
        }

        adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1, liststring
        );

        listView.setAdapter(adapter);

        // Set an item click listener for ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                String selectedItem = (String) parent.getItemAtPosition(position);

                // Display the selected item text on TextView
                System.out.println("Nigguh" + selectedItem);
            }
        });

    }

}