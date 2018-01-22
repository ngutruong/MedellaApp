package com.medella.android.activities;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.medella.android.R;

public class ResultsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_results);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //Allow sharing options when users tap 'Share Your Results' button
    public void shareClick(View view){
        TextView bmiLabel = (TextView)findViewById(R.id.bmiText);
        TextView pintLabel = (TextView)findViewById(R.id.pintText);
        TextView weightLabel = (TextView)findViewById(R.id.weightText);
        TextView btempLabel = (TextView)findViewById(R.id.btempText);
        TextView systolicLabel = (TextView)findViewById(R.id.systolicText);
        TextView diastolicLabel = (TextView)findViewById(R.id.diastolicText);
        TextView hrateLabel = (TextView)findViewById(R.id.hrateText);

        TextView bmiResult = (TextView)findViewById(R.id.txtBmiResult);
        TextView pintResult = (TextView)findViewById(R.id.txtPintResult);
        TextView weightResult = (TextView)findViewById(R.id.txtWeightResult);
        TextView btempResult = (TextView)findViewById(R.id.txtBtempResult);
        TextView systolicResult = (TextView)findViewById(R.id.txtSystolicResult);
        TextView diastolicResult = (TextView)findViewById(R.id.txtDiastolicResult);
        TextView hrateResult = (TextView)findViewById(R.id.txtHrateResult);

        String showBmi = bmiLabel.getText().toString() + bmiResult.getText().toString() + "\n";
        String showPint = pintLabel.getText().toString() + pintResult.getText().toString() + "\n";
        String showWeight = weightLabel.getText().toString() + weightResult.getText().toString() + "\n";
        String showBtemp = btempLabel.getText().toString() + btempResult.getText().toString() + "\n";
        String showSystolic = systolicLabel.getText().toString() + systolicResult.getText().toString() + "\n";
        String showDiastolic = diastolicLabel.getText().toString() + diastolicResult.getText().toString() + "\n";
        String showHrate = hrateLabel.getText().toString() + hrateResult.getText().toString() + "\n";

        Intent iShare = new Intent(Intent.ACTION_SEND);
        iShare.setType("text/plain");

        //String shareBody = showBmi + showPint + showWeight + showBtemp + showSystolic + showDiastolic + showHrate; //Not complete? - subject does not work because of update?
        String shareBody = "Your body here"; //shareBody does not show up - possibly due to update?
        //String shareSub  = "My Overall Health Results from Medella"; //Not complete? - Concatenate user's name
        String shareSub = showBmi + showPint + showWeight + showBtemp + showSystolic + showDiastolic + showHrate; //shareSub acts as a body instead of subject - possibly due to update?

        iShare.putExtra(Intent.EXTRA_TEXT, shareBody);
        iShare.putExtra(Intent.EXTRA_TEXT, shareSub);
        startActivity(Intent.createChooser(iShare, "Share your health results via"));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_results, menu); //Cancel out 3-dot menu in Health Activity page
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
            //Intent is not needed as the Result button leads to this page

        } else if (id == R.id.nav_amLogout) {
            //Logout is not available at this moment
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
