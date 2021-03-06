package com.medella.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.medella.android.AccountStatus;
import com.medella.android.R;
import com.medella.android.options.MedellaOptions;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button btnLogin, btnCreateProfile, btnCreateActivity, btnList;
    private TextView loginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnLogin = findViewById(R.id.btnLogin);
        btnCreateProfile = findViewById(R.id.btnCreateProfile);
        btnCreateActivity = findViewById(R.id.btnCreateActivity);
        btnList = findViewById(R.id.btnList);
        loginStatus = findViewById(R.id.txtLoginStatus);

        if(AccountStatus.isLoggedIn(getApplicationContext())){
            btnLogin.setVisibility(View.INVISIBLE);
            btnCreateProfile.setVisibility(View.INVISIBLE);
            loginStatus.setText("Welcome, " + AccountStatus.getProfileName(getApplicationContext()) + "!");
            btnCreateActivity.setVisibility(View.VISIBLE);
            btnList.setVisibility(View.VISIBLE);
        }
        else{
            loginStatus.setText("You are NOT logged in.");
            btnLogin.setVisibility(View.VISIBLE);
            btnCreateProfile.setVisibility(View.VISIBLE);
            btnCreateActivity.setVisibility(View.INVISIBLE);
            btnList.setVisibility(View.INVISIBLE);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view_home);
        navigationView.setNavigationItemSelectedListener(this);
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
        //getMenuInflater().inflate(R.menu.home, menu); //Cancel out 3-dot menu in Home page
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
            //Intent is not needed as the Home button leads to this page

        } else if (id == R.id.nav_amActivity) {
            Intent iHealth = new Intent(this, HealthActivity.class);
            startActivity(iHealth);
        }else if (id == R.id.nav_amList) {
            Intent iList = new Intent(this, ListActivity.class);
            startActivity(iList);

        } else if (id == R.id.nav_amResults) {
            Intent iResults = new Intent(this, ResultsActivity.class);
            startActivity(iResults);

        }
        else if (id == R.id.nav_amSettings) {
            Intent iSettings = new Intent(this, SettingsActivity.class);
            startActivity(iSettings);
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

    public final static String EXTRA_VALUE = "com.medella.android.MESSAGE";

    //Once the Login button is clicked, it will send the user to Login page
    public void goLogin(View view){
        Intent iLog = new Intent(this, LoginActivity.class);
        startActivity(iLog);
    }

    //Once the Create a Profile button is clicked, it will send the user to Profile page
    public void goProfile(View view){
        Intent iProf = new Intent(this, ProfileActivity.class);
        startActivity(iProf);
    }

    //It will send user to the Health Activity page to create a new entry
    public void goHealth(View view){
        Intent iHealth = new Intent(this, HealthActivity.class);
        startActivity(iHealth);
    }

    //It will send user to List page
    public void goList(View view){
        Intent iList = new Intent(this, ListActivity.class);
        startActivity(iList);
    }
}
