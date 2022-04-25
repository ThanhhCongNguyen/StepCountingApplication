package com.example.stepcounting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager = null;
    private Boolean running = false;
    private float totalSteps = 0;
    private float previousTotalSteps = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData();
        resetSteps();

        // Adding a context of SENSOR_SERVICE aas Sensor Manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);


        if (stepSensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show();
        } else {
            // Rate suitable for the user interface
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        TextView tv_stepsTaken = findViewById(R.id.tv_stepsTaken);
        SensorEvent event = null;

        if (running) {
            totalSteps = event.values[0];

            // Current steps are calculated by taking the difference of total steps
            // and previous steps
            float currentSteps = totalSteps - previousTotalSteps;

            // It will show the current steps to the user
            tv_stepsTaken.setText(String.valueOf(currentSteps));
        }

    }
    private void resetSteps(){
        TextView tv_stepsTaken = findViewById(R.id.tv_stepsTaken);
        tv_stepsTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Long tap to reset steps", Toast.LENGTH_SHORT).show();
            }
        });

        tv_stepsTaken.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                previousTotalSteps = totalSteps;
                tv_stepsTaken.setText(String.valueOf(0));
                saveData();
                return true;
            }

        });

    }


    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("key1", previousTotalSteps);
        editor.apply();
    }
    
    private void loadData() {
        // In this function we will retrieve data
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        float savedNumber = sharedPreferences.getFloat("key1", 0f);

        // Log.d is used for debugging purposes
        Log.d("MainActivity", String.valueOf(savedNumber));

        previousTotalSteps = savedNumber;
    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}