package com.example.pma_ek;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Run extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView RunningSpeed;
    private TextView RunningDuration;
    private TextView RunningDistance;
    private long startTime = 0;
    private float distance = 0;
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
        RunningDuration = findViewById(R.id.RunningDuration);
        RunningDistance = findViewById(R.id.RunningDistance);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register the accelerometer sensor listener
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        startTime = SystemClock.elapsedRealtime(); // set the start time
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

            // calculate the total acceleration by subtracting the acceleration due to gravity from the acceleration magnitude
            float totalAcceleration = acceleration - SensorManager.GRAVITY_EARTH;

            // calculate the time elapsed since the last update
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - startTime;
            startTime = currentTime;

            // calculate the velocity using the formula v = u + at
            // where u is the initial velocity (0), a is the total acceleration, and t is the time elapsed
            float velocity = totalAcceleration * elapsedTime / 1000;

            // calculate the distance using the formula d = ut + 0.5at^2
            // where d is the distance traveled, u is the initial velocity (0), a is the total acceleration, and t is the time elapsed
            distance += (0.5f * totalAcceleration * elapsedTime * elapsedTime / 1000000) + (velocity * elapsedTime / 1000);

            // update the last values for x, y, and z
            lastX = x;
            lastY = y;
            lastZ = z;

            // update the RunningSpeed TextView with the new acceleration value
            String formattedAcceleration = String.format("%.1f", totalAcceleration);
            RunningSpeed.setText("Speed: " + formattedAcceleration + " m/s");

            // update the RunningDuration TextView with the new duration value
            String formattedDuration = String.format("%.2f", elapsedTime / 1000.0f);
            RunningDuration.setText("Duration: " + formattedDuration + " s");

            // update the RunningDistance TextView with the new distance value
            String formattedDistance = String.format("%.2f", distance);
            RunningDistance.setText("Distance: " + formattedDistance + " m");
        }
    }





    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}
