package com.medella.android.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.medella.android.MedellaOptions;
import com.medella.android.R;

public class SettingsActivity extends AppCompatActivity {

    private Spinner weightUnitSpinner;
    private Spinner temperatureUnitSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        weightUnitSpinner = findViewById(R.id.spnWeightOptions);
        temperatureUnitSpinner = findViewById(R.id.spnTemperatureOptions);
        boolean weightUnitPreference = MedellaOptions.getPreferredWeightUnit(getApplicationContext());
        boolean temperatureUnitPreference = MedellaOptions.getPreferredBodyTemperatureUnit(getApplicationContext());

        if(weightUnitPreference){
            weightUnitSpinner.setSelection(0);
        }
        else{
            weightUnitSpinner.setSelection(1);
        }

        if(temperatureUnitPreference){
            temperatureUnitSpinner.setSelection(0);
        }
        else{
            temperatureUnitSpinner.setSelection(1);
        }
    }

    public void saveClick(View view){
        if(weightUnitSpinner.getSelectedItem().toString().equals("Pounds (lbs)")){
            MedellaOptions.setPreferredWeightUnit(getApplicationContext(), true);
        }
        else{
            MedellaOptions.setPreferredWeightUnit(getApplicationContext(), false);
        }

        if(temperatureUnitSpinner.getSelectedItem().toString().equals("Celsius (°C)")){
            MedellaOptions.setPreferredBodyTemperatureUnit(getApplicationContext(), true);
        }
        else{
            MedellaOptions.setPreferredBodyTemperatureUnit(getApplicationContext(), false);
        }

        Toast.makeText(getApplicationContext(),"Preferences have been saved.", Toast.LENGTH_SHORT).show();
    }
}
