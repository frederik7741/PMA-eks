package com.example.pma_ek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class Run extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView RunningSpeed;
    private Sensor accelerometer;
    private TextView xTextView, yTextView, zTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        // initialize the sensor manager and accelerometer sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // initialize the RunningSpeed TextView
        RunningSpeed = findViewById(R.id.running_speed_textview);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // register the sensor listener
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister the sensor listener
        sensorManager.unregisterListener(this);
    }

    private static final float GRAVITY = 9.81f; // m/s^2
    private float acceleration;

    @Override
    public void onSensorChanged(SensorEvent event) {
        // check if the sensor type is accelerometer
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // get the acceleration values in x, y, and z directions
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // calculate the combined acceleration
            float netAcceleration = (float) Math.sqrt(x * x + y * y + z * z);

            // subtract gravitational acceleration
            acceleration = netAcceleration - GRAVITY;

            // update the TextView with the acceleration value
            String formattedAcceleration = String.format("%.1f", acceleration);
            RunningSpeed.setText("Speed: " + formattedAcceleration + " m/s");
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}
