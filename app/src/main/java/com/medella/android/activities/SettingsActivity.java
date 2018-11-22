package com.medella.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.medella.android.MedellaOptions;
import com.medella.android.R;

public class SettingsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Spinner weightUnitSpinner;
    private Spinner temperatureUnitSpinner;

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
            Intent iHome = new Intent(this, HomeActivity.class);
            startActivity(iHome);
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
            //Logout is not available at this moment
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
