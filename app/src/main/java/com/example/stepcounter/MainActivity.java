package com.example.stepcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    // experimental values for hi and lo magnitude limits
    private final double HI_STEP = 11.0;     // upper mag limit
    private final double LO_STEP = 8.0;      // lower mag limit
    boolean highLimit = false;      // detect high limit
    Timer timer;
    TimerTask timerTask;
    boolean stopwatchBool, startClick = false;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    Run currentRun = new Run();

    TextView timeView, stepsView, stepsMin;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeView = findViewById(R.id.timeResult);
        stepsView = findViewById(R.id.stepResult);
        stepsMin = findViewById(R.id.stepsMin);
        // we are going to use the sensor service
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        stepsMin.setText("0");
        currentRun.ID = 1;
        currentRun.Steps = 0;
        currentRun.StepsMin = 0;
        stopwatchBool = false;
        runTimer();
    }
    protected void onResume() {
            stopwatchBool = true;
            super.onResume();
            // turn on the sensor
            mSensorManager.registerListener(this, mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
    }

    /*
     * App running but not on screen - in the background
     */
    protected void onPause() {
        stopwatchBool = false;
        super.onPause();
        mSensorManager.unregisterListener(this);    // turn off listener to save power
    }
    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // get a magnitude number using Pythagoras's Theorem
        double mag = round(Math.sqrt((x*x) + (y*y) + (z*z)), 2);
        // for me! if msg > 11 and then drops below 9, we have a step
        // you need to do your own mag calculating
        if ((mag > HI_STEP) && (highLimit == false)) {
            highLimit = true;
        }
        if ((mag < LO_STEP) && (highLimit == true)) {
            // we have a step
            currentRun.Steps++;
            stepsView.setText(String.valueOf(currentRun.Steps));
            highLimit = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void StopRun(View view) {
        Toast.makeText(this, "Run Stopped", Toast.LENGTH_SHORT).show();
        onPause();
    }

    public void StartRun(View view) {
        Toast.makeText(this, "Run Resumed", Toast.LENGTH_SHORT).show();
        onResume();
    }

    public void ResetRun(View view) {
        /*seconds = 0;
        stepCounter = 0;*/
        currentRun.Seconds = 0;
        currentRun.Steps = 0;
        currentRun.Seconds = 0;
        stepsView.setText("0");
        timeView.setText("0");
        onPause();
    }
    //Timer Logic
    private void runTimer()
    {
        // Creates a new Handler
        final Handler handler = new Handler();

        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handler.post(new Runnable() {
            @Override
            public void run()
            {
                currentRun.StepsMin = (Double.valueOf(currentRun.Steps)/ currentRun.Seconds)*60;
                String time = String.valueOf(currentRun.Seconds);
                timeView.setText(time);
                Log.i("1stTest", "Bool should be false" + stopwatchBool);
                if (stopwatchBool) {
                    currentRun.Seconds++;
                }
                stepsMin.setText(df.format(currentRun.StepsMin));
                handler.postDelayed(this, 1000);
            }
        });
    }

    public void ShowRun(View view) {
        Intent statsPage = new Intent(view.getContext(), StatsPage.class);

        statsPage.putExtra("currentRun", (Serializable) currentRun);
        startActivity(statsPage);     // start the new page
    }
}