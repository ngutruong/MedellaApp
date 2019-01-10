package com.medella.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.medella.android.AccountStatus;
import com.medella.android.options.MedellaOptions;
import com.medella.android.R;

public class SettingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Spinner weightUnitSpinner;
    private Spinner temperatureUnitSpinner;
    private Switch defaultWeightSwitch;
    private EditText defaultWeightInput;
    private TextView tvDefaultWeightUnit;
    private LinearLayout defaultWeightLayout;

    private boolean isDefaultWeightEnabled;
    private float defaultWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view_settings);
        navigationView.setNavigationItemSelectedListener(this);

        weightUnitSpinner = findViewById(R.id.spnWeightOptions);
        temperatureUnitSpinner = findViewById(R.id.spnTemperatureOptions);
        defaultWeightSwitch = findViewById(R.id.swDefaultWeight);
        defaultWeightInput = findViewById(R.id.etDefaultWeight);
        tvDefaultWeightUnit = findViewById(R.id.txtDefaultWeightUnit);
        defaultWeightLayout = findViewById(R.id.defaultWeightLayout);
        boolean weightUnitPreference = MedellaOptions.getPreferredWeightUnit(getApplicationContext());
        boolean temperatureUnitPreference = MedellaOptions.getPreferredBodyTemperatureUnit(getApplicationContext());

        isDefaultWeightEnabled = MedellaOptions.getDefaultWeightEnabled(getApplicationContext());
        defaultWeight = MedellaOptions.getDefaultWeight(getApplicationContext());

        if(weightUnitPreference){
            weightUnitSpinner.setSelection(0);
            tvDefaultWeightUnit.setText("lbs");
        }
        else{
            weightUnitSpinner.setSelection(1);
            tvDefaultWeightUnit.setText("kg");
        }
        weightUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0: tvDefaultWeightUnit.setText("lbs");
                        break;
                    case 1: tvDefaultWeightUnit.setText("kg");
                        break;
                    default: tvDefaultWeightUnit.setText("lbs");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(temperatureUnitPreference){
            temperatureUnitSpinner.setSelection(0);
        }
        else{
            temperatureUnitSpinner.setSelection(1);
        }

        if(isDefaultWeightEnabled){
            defaultWeightSwitch.setChecked(true);
            defaultWeightInput.setEnabled(true);
            defaultWeightLayout.setVisibility(View.VISIBLE);
            defaultWeightInput.setText(String.valueOf(defaultWeight));
        }
        else{
            defaultWeightSwitch.setChecked(false);
            defaultWeightInput.setText("");
            defaultWeightInput.setEnabled(false);
            defaultWeightLayout.setVisibility(View.GONE);
        }
        defaultWeightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    defaultWeightInput.setEnabled(true);
                    defaultWeightLayout.setVisibility(View.VISIBLE);
                    defaultWeightInput.setText(String.valueOf(defaultWeight));
                }
                else{
                    defaultWeightInput.setText("");
                    defaultWeightInput.setEnabled(false);
                    defaultWeightLayout.setVisibility(View.GONE);
                }
            }
        });
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

        if(defaultWeightSwitch.isChecked() && defaultWeightInput.getText().toString()!="") {
            MedellaOptions.setDefaultWeightEnabled(getApplicationContext(), true);
            MedellaOptions.setDefaultWeight(getApplicationContext(), Float.valueOf(defaultWeightInput.getText().toString()));
        }
        else{
            MedellaOptions.setDefaultWeightEnabled(getApplicationContext(), false);
            MedellaOptions.setDefaultWeight(getApplicationContext(), 0);
        }

        Toast.makeText(getApplicationContext(),"Preferences have been saved.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_health, menu); //Cancel out 3-dot menu in Health Activity page
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //DRAWER NAVIGATION IN HOME PAGE
        if (id == R.id.nav_amHome) {
            Intent iAccountInfo = new Intent(this, AccountInfoActivity.class);
            startActivity(iAccountInfo);
        } else if (id == R.id.nav_amActivity) {
            Intent iHealth = new Intent(this, HealthActivity.class);
            startActivity(iHealth);
        } else if (id == R.id.nav_amList) {
            Intent iList = new Intent(this, ListActivity.class);
            startActivity(iList);
        } else if (id == R.id.nav_amResults) {
            Intent iResults = new Intent(this, ResultsActivity.class);
            startActivity(iResults);
        }
        else if (id == R.id.nav_amSettings) {
            //Intent is not needed as Settings button leads to this page
        }
        else if (id == R.id.nav_amLogout) {
            MedellaOptions.setDefaultWeight(getApplicationContext(), 0);
            MedellaOptions.setPreferredWeightUnit(getApplicationContext(), true);
            MedellaOptions.setPreferredBodyTemperatureUnit(getApplicationContext(), true);
            MedellaOptions.setDefaultWeightEnabled(getApplicationContext(), false);
            AccountStatus.setLogin(getApplicationContext(),false);
            AccountStatus.setProfileName(getApplicationContext(),null);
            AccountStatus.setProfileId(getApplicationContext(),null);
            AccountStatus.setEmail(getApplicationContext(),null);
            AccountStatus.setHeightM(getApplicationContext(),0);
            Intent iLogin = new Intent(this, LoginActivity.class);
            startActivity(iLogin);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
