package com.example.pma_ek;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class Run extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private long lastUpdateTime = 0;
    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get a reference to the accelerometer sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register the accelerometer sensor listener
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // unregister the accelerometer sensor listener
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // check if the sensor type is accelerometer
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // get the acceleration values in x, y, and z directions
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // calculate the acceleration magnitude
            float acceleration = (float) Math.sqrt(x * x + y * y + z * z);

            // calculate the time elapsed since the last update
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastUpdateTime;
            lastUpdateTime = currentTime;

            // calculate the velocity using the formula v = u + at
            // where u is the initial velocity (0), a is the acceleration, and t is the time elapsed
            float velocity = 0 + acceleration * elapsedTime / 1000;

            // calculate the distance using the formula d = ut + 0.5at^2
            // where d is the distance traveled, u is the initial velocity (0), a is the acceleration, and t is the time elapsed
            float distance = 0 + 0.5f * acceleration * elapsedTime * elapsedTime / 1000 / 1000;

            // log the velocity and distance
            Log.d("MainActivity", "Velocity: " + velocity + " m/s");
            Log.d("MainActivity", "Distance: " + distance + " m");

            // update the last x, y, and z values
            lastX = x;
            lastY = y;
            lastZ = z;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}
