package com.ivanandevs.appfoliopdf.analytics;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MovementDetector implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private float[] lastAccelerometerData = new float[3];
    private float[] lastGyroscopeData = new float[3];
    private static final float POSITION_CHANGE_THRESHOLD = 0.01f;
    private static final int NUM_SAMPLES = 5;
    public static boolean isMoving = false;

    public MovementDetector(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void start() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == accelerometer) {
            // Збережіть нові дані акселерометра
            float[] filteredAccelerometerData = applyFilter(event.values, lastAccelerometerData);
            float accelerometerDifference = calculateDifference(filteredAccelerometerData, lastAccelerometerData);
            if (accelerometerDifference > POSITION_CHANGE_THRESHOLD) {
            }
            System.arraycopy(filteredAccelerometerData, 0, lastAccelerometerData, 0, filteredAccelerometerData.length);
        } else if (event.sensor == gyroscope) {
            float[] filteredGyroscopeData = applyFilter(event.values, lastGyroscopeData);
            float gyroscopeDifference = calculateDifference(filteredGyroscopeData, lastGyroscopeData);
            if (gyroscopeDifference > POSITION_CHANGE_THRESHOLD) {
                isMoving = true;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    private float calculateDifference(float[] currentValues, float[] lastValues) {
        float differenceSquaredSum = 0;
        for (int i = 0; i < currentValues.length; i++) {
            float delta = currentValues[i] - lastValues[i];
            differenceSquaredSum += delta * delta;
        }
        return differenceSquaredSum;
    }

    private float[] applyFilter(float[] currentValues, float[] lastValues) {
        for (int i = 0; i < currentValues.length; i++) {
            currentValues[i] = (currentValues[i] + lastValues[i] * (NUM_SAMPLES - 1)) / NUM_SAMPLES;
        }
        return currentValues;
    }
}

