package com.example.pma_ek;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Run extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView RunningSpeed;
    private float acceleration;
    private long lastUpdateTime = 0;
    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        // get a reference to the accelerometer sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        RunningSpeed = findViewById(R.id.RunningSpeed);
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
            acceleration = (float) Math.sqrt(x * x + y * y + z * z);

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
            Log.d("RunActivity", "Velocity: " + velocity + " m/s");
            Log.d("RunActivity", "Distance: " + distance + " m");

            // update the RunningSpeed TextView with the new acceleration value
            TextView RunningSpeed = findViewById(R.id.RunningSpeed);
            String formattedAcceleration = String.format("%.1f", acceleration);
            RunningSpeed.setText("Speed: " + formattedAcceleration + " m/s");

            // update the Distance TextView with the new distance value
            TextView distanceTextView = findViewById(R.id.distanceTextView);
            String formattedDistance = String.format("%.1f", distance);
            distanceTextView.setText("Distance: " + formattedDistance + " m");

            // update the Duration TextView with the new duration value
            TextView durationTextView = findViewById(R.id.durationTextView);
            String formattedDuration = String.format("%.1f", elapsedTime / 1000f);
            durationTextView.setText("Duration: " + formattedDuration + " seconds");
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}
