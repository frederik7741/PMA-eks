package com.example.pma_ek;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.Manifest;
import android.os.Handler;


public class Run extends AppCompatActivity implements SensorEventListener, LocationListener {

    private SensorManager sensorManager;
    private TextView RunningSpeed;
    private Sensor accelerometer;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];

    private TextView elapsedTimeTextView;

    private LocationManager locationManager;
    private Location previousLocation;

    private TextView distanceTextView;
    private long startTimeMillis = 0;
    private float totalDistance = 0;
    private final int MAX_SPEED_VALUES = 50; // keep track of last 50 speed values

    private float[] speedValues = new float[MAX_SPEED_VALUES];

    private int speedValuesIndex = 0; // index of the last speed value added to the array

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;

    private Handler handler;

    private Runnable showZombieRunnable;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        // initialize handler and runnable for showing the zombie fragment
        handler = new Handler();
        showZombieRunnable = new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new Zombie());
                fragmentTransaction.commit();
            }
        };

        // start the timer for the run
        startTimeMillis = System.currentTimeMillis();

        // set a delay of 5 seconds before showing the zombie fragment
        handler.postDelayed(showZombieRunnable, 5000);
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
        // unregister the sensor listener
        sensorManager.unregisterListener(this);
        // Dette bliver brugt til Zombie popup

        // check if location permission is granted
        if (checkLocationPermission()) {
            // remove location updates
            locationManager.removeUpdates(this);

        }

        // calculate the elapsed time
        long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
        long elapsedTimeSeconds = elapsedTimeMillis / 1000;
        String formattedElapsedTime = String.format("%d seconds", elapsedTimeSeconds);
        elapsedTimeTextView.setText("Elapsed time: " + formattedElapsedTime);
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

            // calculate the average speed for the last 5 seconds
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
            if (elapsedTimeMillis >= 5000) {
                String formattedSpeed = String.format("%.1f", averageSpeed);
                RunningSpeed.setText("Average speed (last 5 sec): " + formattedSpeed + " m/s");
            }
        }
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

            // update the TextView with the acceleration value
            String formattedAcceleration = String.format("%.1f", acceleration);
            RunningSpeed.setText("Speed: " + formattedAcceleration + " m/s");

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


