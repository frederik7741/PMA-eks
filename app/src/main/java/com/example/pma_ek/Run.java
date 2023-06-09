package com.example.pma_ek;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.Manifest;

public class Run extends AppCompatActivity implements SensorEventListener, LocationListener {

    private TextView scoreTextView;
    private SensorManager sensorManager;
    private TextView RunningSpeed;
    private Sensor accelerometer;
    private final float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];

    private TextView elapsedTimeTextView;

    private LocationManager locationManager;
    private Location previousLocation;

    private TextView distanceTextView;
    private long startTimeMillis = 0;
    private float totalDistance = 0;
    private final int MAX_SPEED_VALUES = 50; // keep track of last 50 speed values
    int fullscore = 0;
    private float[] speedValues = new float[MAX_SPEED_VALUES];

    private int speedValuesIndex = 0; // index of the last speed value added to the array

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;

    private float THRESHOLD_SPEED = 0.5f;

    private int difficultyLevel; // Declare a variable to hold the difficulty level

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        // Retrieve the difficulty level from the intent
        Intent intent = getIntent();
        difficultyLevel = intent.getIntExtra("DIFFICULTY_LEVEL", 0);

        // initialize the sensor manager and accelerometer sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // initialize the RunningSpeed TextView
        RunningSpeed = findViewById(R.id.running_speed_textview);
        elapsedTimeTextView = findViewById(R.id.elapsed_time_textview);

        // initialize the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // initialize the distance TextView
        distanceTextView = findViewById(R.id.distance_textview);

        // initialize the score TextView
        scoreTextView = findViewById(R.id.score_textview);
    }
    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return false;
        }
        // Permission is already granted
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register the sensor listener
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // check if location permission is granted
        if (checkLocationPermission()) {
            // request location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.5f, this);

        }

        startTimeMillis = System.currentTimeMillis();
    }


    @Override
    protected void onPause() {
        super.onPause();

        // calculate the elapsed time
        long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
        long elapsedTimeSeconds = elapsedTimeMillis / 1000;
        String formattedElapsedTime = String.format("%d seconds", elapsedTimeSeconds);
        elapsedTimeTextView.setText("Elapsed time: " + formattedElapsedTime);


    }
    private void updateScore() {
        float averageSpeed = getAverageSpeed();
        float distanceCovered = totalDistance;
        int score = Math.round(averageSpeed * distanceCovered * difficultyLevel);
        fullscore = fullscore + score;
        scoreTextView.setText("Score: " + fullscore);
    }

    private float getAverageSpeed() {
        long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
        int numSpeedValues = Math.min(speedValuesIndex, MAX_SPEED_VALUES);
        float totalSpeed = 0;
        int count = 0;
        for (int i = speedValuesIndex - 1; i >= speedValuesIndex - numSpeedValues; i--) {
            if (i < 0) {
                i += MAX_SPEED_VALUES;
            }
            totalSpeed += speedValues[i];
            count++;
        }

        return totalSpeed / count;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            // calculate the distance traveled
            if (previousLocation == null) {
                previousLocation = location;
            }

            float distance = location.distanceTo(previousLocation);
            totalDistance += distance;
            previousLocation = location;

            // update the distance TextView
            String formattedDistance = String.format("%.2f", totalDistance);
            distanceTextView.setText("Distance: " + formattedDistance + " m");

            // update the score TextView
            updateScore();


            // calculate the average speed for the last 5 seconds.
            long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
            int numSpeedValues = Math.min(speedValuesIndex, MAX_SPEED_VALUES);
            float totalSpeed = 0;
            int count = 0;
            for (int i = speedValuesIndex - 1; i >= speedValuesIndex - numSpeedValues; i--) {
                if (i < 0) {
                    i += MAX_SPEED_VALUES;
                }
                totalSpeed += speedValues[i];
                count++;
            }

            float averageSpeed = totalSpeed / count;
            long timer = System.currentTimeMillis() - startTimeMillis;
            long timer2 = System.currentTimeMillis();
            if (averageSpeed >= THRESHOLD_SPEED * difficultyLevel) {
                String formattedSpeed = String.format("%.1f", averageSpeed);
                RunningSpeed.setText("Average speed (last 5 sec): " + formattedSpeed + " m/s");
            } else {
                RunningSpeed.setText("The zombies are closing in!");
                if (Build.VERSION.SDK_INT >= 26) {
                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(500);
                }
            }


            // reset the start time and speed values index
                timer2 = System.currentTimeMillis();
                speedValuesIndex = 0;
            } else {
                RunningSpeed.setText("Moving too slow to display speed");
            }

            // add current speed to the speed values array
            speedValues[speedValuesIndex] = location.getSpeed();
            speedValuesIndex = (speedValuesIndex + 1) % MAX_SPEED_VALUES;
            }

        @Override
    public void onSensorChanged(SensorEvent event) {


        // check if the sensor type is accelerometer
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // get the acceleration values in x, y, and z directions
            final float alpha = 0.8f;
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];

            // calculate the combined acceleration
            float acceleration = (float) Math.sqrt(linear_acceleration[0] * linear_acceleration[0] +
                    linear_acceleration[1] * linear_acceleration[1] + linear_acceleration[2] * linear_acceleration[2]);


            long elapsedTimeSeconds = (System.currentTimeMillis() - startTimeMillis) / 1000;
            String formattedElapsedTime = String.format("%d seconds", elapsedTimeSeconds);
            elapsedTimeTextView.setText("Elapsed time: " + formattedElapsedTime);


// add the speed value to the array
            speedValues[speedValuesIndex] = acceleration;
            speedValuesIndex = (speedValuesIndex + 1) % MAX_SPEED_VALUES;


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}