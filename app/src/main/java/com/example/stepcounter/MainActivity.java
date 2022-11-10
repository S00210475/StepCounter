package com.example.stepcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    // experimental values for hi and lo magnitude limits
    private final double HI_STEP = 11.0;     // upper mag limit
    private final double LO_STEP = 8.0;      // lower mag limit
    boolean highLimit = false;      // detect high limit
    boolean stopwatchBool = false, animationStart = false;
    //Formatting
    private static final DecimalFormat df = new DecimalFormat("0.00");
    Run currentRun = new Run();
    List<Run> savedRuns = new ArrayList<>();
    int runCounter = 1;
    double stepsMin = 0;
    final Handler handler = new Handler();

    TextView timeView, stepsView, stepsMinView, countdownView, thisIsShoe, thisIsStop;
    ImageButton startBtn, stopBtn;
    //Time variables
    CountDownTimer countDownTimer;
    int minutes = 0;
    int seconds = 0;
    String time = String.format("%d:%02d", minutes, seconds);

    Animation runAnimationStart, runAnimationEnd;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Starts pop up window
        startActivity(new Intent(MainActivity.this,Poppin_Window.class));

        timeView = findViewById(R.id.timeResult);
        stepsView = findViewById(R.id.stepResult);
        stepsMinView = findViewById(R.id.stepsMin);
        countdownView = findViewById(R.id.countdownView);
        startBtn = findViewById(R.id.startBtn);
        stopBtn = findViewById(R.id.stopBtn);
        // we are going to use the sensor service
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        currentRun.ID = runCounter;
        stopBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ResetRun(v);
                return false;
            }
        });
        //Animation Handling
        runAnimationStart = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.rotate);
        runAnimationEnd = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.rotate_back);

        runAnimationStart.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.i("AnimationTest", "Animation started");
            }
            @Override
            public void onAnimationEnd(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.i("AnimationTest", "Animation repeated");
                startBtn.startAnimation(runAnimationEnd);
            }
        });

        runAnimationEnd.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.i("AnimationTest", "Animation 2 started");
            }
            @Override
            public void onAnimationEnd(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.i("AnimationTest", "Animation 2 repeated");
                startBtn.startAnimation(runAnimationStart);
            }
        });
        //Pop-up window logic


        RunTimer();
    }
    protected void onResume() {
            super.onResume();
            // turn on the sensor
    }
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
        if(currentRun.Seconds > 0) {
            countDownTimer.cancel();
            countdownView.setText("");
        }
        onPause();
        StopAnimation();
    }

    public void StartRun(View view) throws InterruptedException {
        StopAnimation();
        stopwatchBool = false;
        int countdown = 3000;
        countdownView.bringToFront();
        countDownTimer = new CountDownTimer(countdown, 1000) {
            public void onTick(long millisUntilFinished) {
                countdownView.setText(String.valueOf(millisUntilFinished / 1000 + 1));
            }
            public void onFinish() {
                stopwatchBool = true;
                countdownView.setText("Run!!!");
                StartSensor();
                startBtn.startAnimation(runAnimationStart);
                //Holds the run!!! word for 2 seconds
                new CountDownTimer(2000, 1000) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            countdownView.setText(" ");
                        }
                }.start();
            }
        }.start();
        Log.i("AnimationTest", "Animation started");
    }

    public void ResetRun(View view) {
        for (Run run:savedRuns) {
            Log.i("1stTest", String.valueOf(run.ID + " & " + currentRun.ID));
            if (run.ID == currentRun.ID) {
                Log.i("1stTest", "Works");
                savedRuns.remove(currentRun.ID-1);
            }
        }
        savedRuns.add(currentRun);
        currentRun = new Run();
        runCounter++;
        currentRun.ID = runCounter;
        stepsView.setText("0");
        timeView.setText("0");
        stepsMinView.setText("0");
        StopAnimation();
        onPause();
    }
    //Timer Logic
    private void RunTimer()
    {
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handler.post(new Runnable() {
            @Override
            public void run()
            {
                minutes = currentRun.Seconds / 60;
                seconds = currentRun.Seconds % 60;
                stepsMin = (Double.valueOf(currentRun.Steps) / currentRun.Seconds) * 60;
                time = String.format("%d:%02d", minutes, seconds);
                Log.i("TimeTest", String.valueOf(seconds));
                timeView.setText(time);
                if (stopwatchBool) {
                    currentRun.Seconds++;
                }
                String stepsMinTxt = df.format(stepsMin);
                if(Double.isNaN(stepsMin))
                {
                    stepsMinTxt = "0";
                }
                stepsMinView.setText(stepsMinTxt);
                handler.postDelayed(this, 1000);
            }
        });
    }

    public void ShowRun(View view) {
        Intent statsPage = new Intent(view.getContext(), StatsPage.class);
        for (Run run:savedRuns) {
            Log.i("1stTest", String.valueOf(run.ID + " & " + currentRun.ID));
            if (run.ID == currentRun.ID) {
                Log.i("1stTest", "Works");
                savedRuns.remove(currentRun.ID-1);
            }
        }
        savedRuns.add(currentRun);
        statsPage.putExtra("runs", (Serializable) savedRuns);
        startActivity(statsPage);     // start the new page
    }

    public void ResetOneClick(View view) {
        Toast.makeText(this, "Long Press to Reset!", Toast.LENGTH_SHORT).show();
    }
    public void StartSensor()
    {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void StopAnimation()
    {
        runAnimationStart.cancel();
        runAnimationEnd.cancel();
    }
    //Saves data when turned landscape
    /*@Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        //A lot of problems arise from saving objects so I'm saving individual properties
        // Save the state of steps
        outState.putInt("currentRun.Steps", currentRun.Steps);
        outState.putInt("currentRun.Seconds", currentRun.Seconds);
        outState.putDouble("stepsMin", stepsMin);
        for (int i = 0; i < savedRuns.size(); i++){
            outState.putInt("savedRun.Steps", savedRuns.get(i).Steps);
            outState.putInt("savedRun.Seconds", savedRuns.get(i).Seconds);
        }
    }
    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Read the state of item position
        currentRun.Steps = savedInstanceState.getInt("currentRun.Steps");
        currentRun.Seconds = savedInstanceState.getInt("currentRun.Seconds");
        stepsMin = savedInstanceState.getDouble("stepsMin");
        for (Run run: savedRuns) {
            run.Steps = savedInstanceState.getInt("savedRun.Steps");
            run.Seconds = savedInstanceState.getInt("savedRun.Seconds");
        }
        for (int i = 0; i < savedRuns.size(); i++){
            savedRuns.get(i).Steps = savedInstanceState.getInt("savedRun.Steps");
            savedRuns.get(i).Seconds = savedInstanceState.getInt("savedRun.Seconds");
        }
        //Log.i("1stTest", "Saved Runs restored " + savedRuns.get(0).Steps);
    }*/
}