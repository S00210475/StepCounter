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
import java.util.Date;

public class StatsPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Run[] savedRuns;
    int runCounter = 0;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_page);
        Spinner dropbox = findViewById(R.id.runSpinner);
        TextView dateView = findViewById(R.id.dateView);
        TextView metresRan = findViewById(R.id.metresView);
        TextView calories = findViewById(R.id.caloriesView);
        TextView time = findViewById(R.id.timeView);

        Log.i("1stTest", "Initialization");
        savedRuns = new Run[runCounter+1];
        savedRuns[runCounter] = new Run();
        savedRuns[runCounter] = (Run) getIntent().getSerializableExtra("currentRun");
        Log.i("1stTest", "Run retrieved" + savedRuns[0].ID);
        Log.i("1stTest", "Run saved");
        dropbox.setOnItemSelectedListener(this);
        ArrayAdapter ad
                = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                savedRuns);
        // set simple layout resource file
        // for each item of spinner
        ad.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);

        // Set the ArrayAdapter (ad) data on the
        // Spinner which binds data to spinner
        dropbox.setAdapter(ad);
        Log.i("1stTest", "Arraydapted");

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        dateView.setText(formatter.format(date));
        double metres = savedRuns[runCounter].Steps * 0.8;
        metresRan.setText(df.format(metres));
        double caloriesBurned = savedRuns[runCounter].Steps * 0.04;
        calories.setText(df.format(caloriesBurned));
        time.setText(String.valueOf(savedRuns[runCounter].Seconds));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void Back(View view) {
        finish();
    }
}