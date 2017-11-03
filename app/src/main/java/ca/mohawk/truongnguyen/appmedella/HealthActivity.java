package ca.mohawk.truongnguyen.appmedella;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class HealthActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


        //start NAVIGATION MENU in onCreate()
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //end NAVIGATION MENU in onCreate()
    }

    private static String collectErrors = "";

    public void finishClick(View view) {
        EditText titleInput = (EditText)findViewById(R.id.txtTitle);
        EditText weightInput = (EditText)findViewById(R.id.txtWeight);
        EditText medName = (EditText)findViewById(R.id.txtMedName);
        EditText medDose = (EditText)findViewById(R.id.txtDosage);
        EditText temperatureInput = (EditText)findViewById(R.id.txtTemperature);
        EditText systolicInput = (EditText)findViewById(R.id.txtSystolic);
        EditText diastolicInput = (EditText)findViewById(R.id.txtDiastolic);
        EditText heartRate = (EditText)findViewById(R.id.txtHrate);
        EditText healthDescription = (EditText)findViewById(R.id.txtDesc);
        Spinner weightSpin = (Spinner)findViewById(R.id.weightSpinner);


        AlertDialog.Builder errorDialog = new AlertDialog.Builder(this);
        errorDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        try {
            //TITLE VALIDATION IN HEALTH ACTIVITY PAGE
            if(titleInput.getText().toString().trim().isEmpty()){
                collectErrors+="- Title is empty.\n";
            }

            //WEIGHT VALIDATION IN HEALTH ACTIVITY PAGE
            if(weightInput.getText().toString().trim().isEmpty()){
                collectErrors+="- Weight is empty.\n";
            }
            else if(weightSpin.getSelectedItem().toString().equals("lbs") && Double.parseDouble(weightInput.getText().toString()) < 7.7){
                collectErrors+="- Please enter a valid weight.\n";
            }
            else if(Double.parseDouble(weightInput.getText().toString()) < 0){
                collectErrors+="- Weight must not have negative value.\n";
            }

            //HEALTH DESCRIPTION VALIDATION FOR HEALTH ACTIVITY PAGE
            if(healthDescription.getText().toString().trim().isEmpty()){
                collectErrors+="- Medical description is empty.\n";
            }

            //BODY TEMPERATURE VALIDATION IN HEALTH ACTIVITY PAGE
            if(Double.parseDouble(temperatureInput.getText().toString()) == 0){
                collectErrors+="- Body temperature must not be 0.\n";
            }
            else if(Double.parseDouble(temperatureInput.getText().toString()) < 0){
                collectErrors+="- Body temperature must not have negative value.\n";
            }

            /*
            VALIDATION IS ERRONEOUS--MESSAGES SHOULD SHOW
             */

            //SYSTOLIC PRESSURE VALIDATION IN HEALTH ACTIVITY PAGE
            if(Double.parseDouble(systolicInput.getText().toString()) < 90){
                collectErrors+="- Systolic pressure must not be less than 90.\n";
            }
            else if(Double.parseDouble(systolicInput.getText().toString()) > 250){
                collectErrors+="- Systolic pressure must not be greater than 250.\n";
            }

            //DIASTOLIC PRESSURE VALIDATION IN HEALTH ACTIVITY PAGE
            if(Double.parseDouble(diastolicInput.getText().toString()) < 60){
                collectErrors+="- Diastolic pressure must not be less than 60.\n";
            }
            else if(Double.parseDouble(diastolicInput.getText().toString()) > 140){
                collectErrors+="- Diastolic pressure must not be greater than 140.\n";
            }

            //HEART RATE VALIDATION IN HEALTH ACTIVITY PAGE
            if(Double.parseDouble(heartRate.getText().toString()) < 20 && Double.parseDouble(heartRate.getText().toString()) > 0){
                collectErrors+="- Heart rate is not valid.\n";
            }
            else if(Double.parseDouble(heartRate.getText().toString()) == 0 && !heartRate.getText().toString().trim().isEmpty()){
                collectErrors+="- Heart rate must not be 0.\n";
            }
        }
        catch (Exception ex){
            errorDialog.setMessage("Please enter a valid input.");
        }

        if(collectErrors.trim().length() > 0) {
            errorDialog.setTitle("Error")
                    .setMessage("Health activity is not complete due to the following errors:\n\n" + collectErrors)
                    .show();
            collectErrors = ""; //Reset errors
        }
        else{
            errorDialog.setTitle("Health Activity Added")
                    .setMessage("Thank you.")
                    .show();
        }
    }

    /*
    start NAVIGATION MENU
     */
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
        getMenuInflater().inflate(R.menu.home, menu);
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
            Intent iHealth = new Intent(this, HomeActivity.class);
            startActivity(iHealth);
        } else if (id == R.id.nav_amActivity) {
            //Intent is not needed as the Home button leads to this page
        } else if (id == R.id.nav_amList) {
            //ListActivity.class not available yet

        } else if (id == R.id.nav_amResults) {
            Intent iResults = new Intent(this, ResultsActivity.class);
            startActivity(iResults);

        } else if (id == R.id.nav_amTrash) {
            //TrashActivity.class not available yet

        } else if (id == R.id.nav_amLogout) {
            //Logout is not available at this moment
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /*
    end NAVIGATION MENU
     */
}
