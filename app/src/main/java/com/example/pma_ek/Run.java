package com.example.pma_ek;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
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

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        startTimeMillis = System.currentTimeMillis();
    }


    @Override
    protected void onPause() {
        super.onPause();
        // unregister the sensor listener
        sensorManager.unregisterListener(this);

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

        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}