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

    private long startTimeMillis = 0;

    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    private TextView elapsedTimeTextView;

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
    }


    @Override
    protected void onResume() {

        startTimeMillis = System.currentTimeMillis();

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
