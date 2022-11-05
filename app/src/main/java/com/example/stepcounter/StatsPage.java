package com.example.stepcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatsPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    List<Run> savedRuns = new ArrayList<>();
    Run currentRun = new Run();
    List<String> runTitles = new ArrayList<>();
    int runCounter = 0;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    Spinner dropbox;
    TextView dateView, metresRan, calories, time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_page);
        Log.i("1stTest", "Run Initializing");
        dateView = findViewById(R.id.dateView);
        metresRan = findViewById(R.id.metresView);
        calories = findViewById(R.id.caloriesView);
        time = findViewById(R.id.timeView);
        dropbox = findViewById(R.id.runSpinner);

        Log.i("1stTest", "Initialization");
        savedRuns = (List<Run>) getIntent().getSerializableExtra("currentRun");
        for (Run run: savedRuns) {
            runTitles.add( "Run " + String.valueOf(run.ID));
        }
        Log.i("1stTest", "Titles added");
        dropbox.setOnItemSelectedListener(this);
        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                runTitles);
        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        dropbox.setAdapter(ad);
        RunInitialization();
    }
    public void RunInitialization()
    {
        Log.i("1stTest", "Test");
        currentRun = savedRuns.get(runCounter);
        Log.i("1stTest", "Post current run assignment");

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        dateView.setText(formatter.format(date));
        double metres = currentRun.Steps * 0.8;
        metresRan.setText(df.format(metres));
        double caloriesBurned = currentRun.Steps * 0.04;
        calories.setText(df.format(caloriesBurned));
        time.setText(String.valueOf(currentRun.Seconds));
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        runCounter = dropbox.getSelectedItemPosition();
        Log.i("1stTest", String.valueOf(runCounter));
        RunInitialization();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void Back(View view) {
        finish();
    }
}