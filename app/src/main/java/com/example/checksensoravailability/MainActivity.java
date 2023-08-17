package com.example.checksensoravailability;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements SensorEventListener {

    private TextView tv_heartRate;
    private TextView tv_pressure;
    private TextView tv_proximity;
    private TextView tv_heartBeat;
    private Button startButton;
    private Button stopButton;
    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor;
    private Sensor mPressureSensor;
    private Sensor mProximitySensor;
    private Sensor mHeartBeatSensor;
    private boolean isRecording = false;
    private static final String TAG = "____Main___";

    private File csvFile;
    private FileOutputStream csvOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_heartRate = findViewById(R.id.text_HEART_RATE);
        tv_pressure = findViewById(R.id.text_Pressure);
        tv_proximity = findViewById(R.id.text_Proximity);
        tv_heartBeat = findViewById(R.id.text_HEART_BEAT);
        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mPressureSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mHeartBeatSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_BEAT);

        checkPermission();
        checkSensorAvailability();

        if (mHeartRateSensor == null) {
            Log.e(TAG, "Heart Rate Sensor is null");
        }

        if (mPressureSensor == null) {
            Log.e(TAG, "Pressure Sensor is null");
        }

        if (mProximitySensor == null) {
            Log.e(TAG, "Proximity Sensor is null");
        }

        if (mHeartBeatSensor == null) {
            Log.e(TAG, "Heart Beat Sensor is null");
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDataCollection();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDataCollection();
            }
        });
    }

    private void checkPermission() {
        if (checkSelfPermission(Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.BODY_SENSORS, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Log.d(TAG, "Permissions granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                Log.d(TAG, "Permissions granted");
                checkSensorAvailability();
            } else {
                Log.e(TAG, "Permissions denied");
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkSensorAvailability() {
        List<Sensor> sensors = new ArrayList<>();
        if (mHeartRateSensor != null) {
            sensors.add(mHeartRateSensor);
        }
        if (mPressureSensor != null) {
            sensors.add(mPressureSensor);
        }
        if (mProximitySensor != null) {
            sensors.add(mProximitySensor);
        }
        if (mHeartBeatSensor != null) {
            sensors.add(mHeartBeatSensor);
        }

        for (Sensor sensor : sensors) {
            Log.d(TAG, sensor.getName() + " is available");
        }

        if (mHeartRateSensor != null) {
            tv_heartRate.setText(tv_heartRate.getText() + " Accessible");
            tv_heartRate.setTextColor(Color.parseColor("#32cd32"));
            mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            tv_heartRate.setText(tv_heartRate.getText() + " Inaccessible");
            tv_heartRate.setTextColor(Color.parseColor("#FF0000"));
        }

        if (mPressureSensor != null) {
            tv_pressure.setText(tv_pressure.getText() + " Accessible");
            tv_pressure.setTextColor(Color.parseColor("#32cd32"));
            mSensorManager.registerListener(this, mPressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            tv_pressure.setText(tv_pressure.getText() + " Inaccessible");
            tv_pressure.setTextColor(Color.parseColor("#454B1B"));
        }

        if (mProximitySensor != null) {
            tv_proximity.setText(tv_proximity.getText() + " Accessible");
            tv_proximity.setTextColor(Color.parseColor("#32cd32"));
            mSensorManager.registerListener(this, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            tv_proximity.setText(tv_proximity.getText() + " Inaccessible");
            tv_proximity.setTextColor(Color.parseColor("#FF0000"));
        }

        if (mHeartBeatSensor != null) {
            tv_heartBeat.setText(tv_heartBeat.getText() + " Accessible");
            tv_heartBeat.setTextColor(Color.parseColor("#32cd32"));
            mSensorManager.registerListener(this, mHeartBeatSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            tv_heartBeat.setText(tv_heartBeat.getText() + " Inaccessible");
            tv_heartBeat.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    private void startDataCollection() {
        if (isRecording) {
            Toast.makeText(this, "Data collection is already in progress.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isExternalStorageWritable()) {
            Toast.makeText(this, "External storage is not writable.", Toast.LENGTH_SHORT).show();
            return;
        }

        csvFile = createCsvFile();
        if (csvFile == null) {
            Toast.makeText(this, "Failed to create CSV file.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            csvOutputStream = new FileOutputStream(csvFile, true);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to open CSV file for writing.", Toast.LENGTH_SHORT).show();
            return;
        }

        isRecording = true;

        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        // mSensorManager.registerListener(this, mPressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        // mSensorManager.registerListener(this, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        // mSensorManager.registerListener(this, mHeartBeatSensor, SensorManager.SENSOR_DELAY_NORMAL);

        Toast.makeText(this, "Data collection started.", Toast.LENGTH_SHORT).show();
    }

    private void stopDataCollection() {
        if (!isRecording) {
            Toast.makeText(this, "Data collection is not in progress.", Toast.LENGTH_SHORT).show();
            return;
        }

        isRecording = false;

        mSensorManager.unregisterListener(this);

        try {
            csvOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "Data collection stopped.", Toast.LENGTH_SHORT).show();
    }

    private File createCsvFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "sensor_data_" + timeStamp + ".csv";

        File dataDir = new File(getFilesDir(), "SensorData");
        System.out.println(dataDir);
        if (!dataDir.exists()) {
            if (!dataDir.mkdirs()) {
                Log.e(TAG, "Failed to create directory for sensor data.");
                return null;
            }
        }

        File csvFile = new File(dataDir, fileName);

        String header = "Timestamp,Heart Rate\n";
        try {
            FileOutputStream headerOutputStream = new FileOutputStream(csvFile);
            headerOutputStream.write(header.getBytes());
            headerOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to write header to CSV file.");
            return null;
        }

        return csvFile;
    }


    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;

        if (isRecording) {
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date());
            String data = timeStamp + ","
                    + (sensor.getType() == Sensor.TYPE_HEART_RATE ? values[0] : "") + "\n";

            try {
                csvOutputStream.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to write data to CSV file.");
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecording) {
            stopDataCollection();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this example
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register sensor listener
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister sensor listener
        mSensorManager.unregisterListener(this);
    }
}
