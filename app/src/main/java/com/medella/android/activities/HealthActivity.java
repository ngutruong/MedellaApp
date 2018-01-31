package com.medella.android.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.medella.android.R;
import com.medella.android.database.ActivityTable;
import com.medella.android.database.ActivityTableAdapter;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class HealthActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String healthActivityTitle = null;

    public boolean areAllInputsValid = false;

    public boolean medicationNameError = true;
    public boolean medicationAmountError = true;
    public boolean bodyTemperatureError = true;
    public boolean systolicError = true;
    public boolean diastolicError = true;
    public boolean heartRateError = true;

    private MobileServiceClient mClient;
    private MobileServiceTable<ActivityTable> mActivityTable;
    //Offline Sync
    //private MobileServiceSyncTable<ActivityTable> mActivityTable;
    private ActivityTableAdapter mAdapter;
    
    private EditText titleInput;
    private EditText weightInput;
    private EditText medName;
    private EditText medDose;
    private EditText temperatureInput;
    private EditText systolicInput;
    private EditText diastolicInput;
    private EditText heartRate;
    private EditText healthDescription;
    private Spinner weightSpin;
    private Spinner temperatureSpin;
    private Spinner doseSpin;
    private Spinner pintSpin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_health);
        navigationView.setNavigationItemSelectedListener(this);

        Spinner dosageSpin = (Spinner)findViewById(R.id.doseSpinner);
        dosageSpin.setSelection(15); //Set mg (Milligram) as default item in Dosage spinner

        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://medellaapp.azurewebsites.net",
                    this).withFilter(new ProgressFilter());

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });

            // Get the Mobile Service Table instance to use

            mActivityTable = mClient.getTable(ActivityTable.class);

            // Offline Sync
            //mActivityTable = mClient.getSyncTable("ActivityTable", ActivityTable.class);

            //Init local storage
            initLocalStore().get();

            titleInput = (EditText)findViewById(R.id.txtTitle);
            weightInput = (EditText)findViewById(R.id.txtWeight);
            medName = (EditText)findViewById(R.id.txtMedName);
            medDose = (EditText)findViewById(R.id.txtDosage);
            temperatureInput = (EditText)findViewById(R.id.txtTemperature);
            systolicInput = (EditText)findViewById(R.id.txtSystolic);
            diastolicInput = (EditText)findViewById(R.id.txtDiastolic);
            heartRate = (EditText)findViewById(R.id.txtHrate);
            healthDescription = (EditText)findViewById(R.id.txtDesc);
            weightSpin = (Spinner)findViewById(R.id.weightSpinner);
            temperatureSpin = (Spinner)findViewById(R.id.tempSpinner);
            doseSpin = (Spinner)findViewById(R.id.doseSpinner);
            pintSpin = (Spinner)findViewById(R.id.pintSpinner);

            // Create an adapter to bind the items with the view
            // MAY APPLY TO LIST
            mAdapter = new ActivityTableAdapter(this, R.layout.row_list_to_do);
            ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
            listViewToDo.setAdapter(mAdapter);

            // Load the items from the Mobile Service
            refreshItemsFromTable();

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            createAndShowDialog(e, "Error");
        }
    }

    private float convertCelsiusToFahrenheit(float celsiusTemp)
    {
        return celsiusTemp*(9/5)+32;
    }
    private float convertFahrenheitToCelsius(float fahrenheitTemp)
    {
        return (fahrenheitTemp-32)*(5/9);
    }
    private float convertLbsToKg(float weightLbs)
    {
        return (float) (weightLbs*(1/2.2));
    }
    private float convertKgToLbs(float weightKg)
    {
        return (float) (weightKg*2.2);
    }
    private float calculateBmi(float weightKg, float heightMeter){
        return (float) (weightKg/(Math.pow(heightMeter, 2)));
    }
    
    private static String collectErrors = "";

    public void finishClick(View view) {

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
            else if(weightSpin.getSelectedItem().toString().equals("lbs") && Float.valueOf(weightInput.getText().toString()) < 7.7){
                collectErrors+="- Please enter a valid weight.\n";
            }
            else if(weightSpin.getSelectedItem().toString().equals("kg") && Float.valueOf(weightInput.getText().toString()) < 3.5){
                collectErrors+="- Please enter a valid weight.\n";
            }
            //else if(Float.valueOf(weightInput.getText().toString()) < 0){
              //  collectErrors+="- Weight must not have negative value.\n";
            //}

            //HEALTH DESCRIPTION VALIDATION FOR HEALTH ACTIVITY PAGE
            if(healthDescription.getText().toString().trim().isEmpty()){
                collectErrors+="- Medical description is empty.\n";
            }

            //BODY TEMPERATURE VALIDATION IN HEALTH ACTIVITY PAGE
            if(Float.valueOf(temperatureInput.getText().toString()) == 0){
                bodyTemperatureError = true;
                collectErrors+="- Body temperature must not be 0.\n";
            } else if (Float.valueOf(temperatureInput.getText().toString()) < 35 && temperatureSpin.getSelectedItem().toString().equals("degrees Celsius")) {
                bodyTemperatureError = true;
                collectErrors+="- Please enter a valid body temperature.\n";
            } else if (Float.valueOf(temperatureInput.getText().toString()) < 95 && temperatureSpin.getSelectedItem().toString().equals("degrees Fahrenheit")) {
                bodyTemperatureError = true;
                collectErrors+="- Please enter a valid body temperature.\n";
            } else {
                bodyTemperatureError = false; // no error will trigger if body temperature input is empty or valid
            }
            //else if(Float.valueOf(temperatureInput.getText().toString()) < 0){
            //    collectErrors+="- Body temperature must not have negative value.\n";
            //}

            /*
            VALIDATION IS ERRONEOUS--MESSAGES SHOULD SHOW
             */

            //SYSTOLIC PRESSURE VALIDATION IN HEALTH ACTIVITY PAGE
            if(Float.valueOf(systolicInput.getText().toString()) < 90){
                systolicError = true;
                collectErrors+="- Systolic pressure must not be less than 90.\n";
            }
            else if(Float.valueOf(systolicInput.getText().toString()) > 250){
                systolicError = true;
                collectErrors+="- Systolic pressure must not be greater than 250.\n";
            }
            else if(!systolicInput.getText().toString().trim().isEmpty() && diastolicInput.getText().toString().trim().isEmpty()){
                systolicError = true;
                collectErrors+="- Diastolic pressure must not be empty while systolic pressure is filled.\n";
            }
            else{
                systolicError = false; // no error will trigger if systolic and diastolic inputs are empty or valid
            }

            //DIASTOLIC PRESSURE VALIDATION IN HEALTH ACTIVITY PAGE
            if(Float.valueOf(diastolicInput.getText().toString()) < 60){
                diastolicError = true;
                collectErrors+="- Diastolic pressure must not be less than 60.\n";
            }
            else if(Float.valueOf(diastolicInput.getText().toString()) > 140){
                diastolicError = true;
                collectErrors+="- Diastolic pressure must not be greater than 140.\n";
            }
            else if(systolicInput.getText().toString().trim().isEmpty() && !diastolicInput.getText().toString().trim().isEmpty()){
                diastolicError = true;
                collectErrors+="- Systolic pressure must not be empty while diastolic pressure is filled.\n";
            }
            else {
                diastolicError = false; // no error will trigger if diastolic and systolic inputs are empty or valid
            }

            //HEART RATE VALIDATION IN HEALTH ACTIVITY PAGE
            if(Float.valueOf(heartRate.getText().toString()) < 20 && Float.valueOf(heartRate.getText().toString()) > 0){
                heartRateError = true;
                collectErrors+="- Heart rate is not valid.\n";

            }
            else if(Float.valueOf(heartRate.getText().toString()) == 0 && !heartRate.getText().toString().trim().isEmpty()){
                heartRateError = true;
                collectErrors+="- Heart rate must not be 0.\n";
            }
            else {
                heartRateError = false; // no error will trigger if heart rate input is empty or valid
            }
        }
        catch (Exception ex){
            areAllInputsValid = false;
            errorDialog.setMessage("Please enter a valid input.");
        }

        if(collectErrors.trim().length() == 0 && heartRateError==false && bodyTemperatureError==false && systolicError==false && diastolicError==false) {
            areAllInputsValid = true; //All inputs can be saved if they are valid
        }
        else{
            errorDialog.setTitle("Error")
                    .setMessage("Health activity is not complete due to the following errors:\n\n" + collectErrors)
                    .show();
            collectErrors = ""; //Reset errors
            areAllInputsValid = false; //All inputs will not be saved if one or more inputs are not valid
        }

        /*
        if(collectErrors.trim().length() > 0 && heartRateError==true && bodyTemperatureError==true && systolicError==true && diastolicError==true) {
            errorDialog.setTitle("Error")
                    .setMessage("Health activity is not complete due to the following errors:\n\n" + collectErrors)
                    .show();
            collectErrors = ""; //Reset errors
            areAllInputsValid = false; //All inputs will not be saved if one or more inputs are not valid
        }
        else{
            areAllInputsValid = true; //All inputs can be saved if they are valid
        }*/

        if(areAllInputsValid==true){
            if (mClient == null) {
                return;
            }

            // Create a new item
            final ActivityTable item = new ActivityTable();

            item.setActivityTitle(titleInput.getText().toString());
            if(pintSpin.getSelectedItem().toString().contains("Most Painful")){
                item.setPainIntensity(10);
            }
            else{
                String getNumber = pintSpin.getSelectedItem().toString().substring(0,1);
                float painNumber = Float.valueOf(getNumber);
                item.setPainIntensity(painNumber);
            }
            if(weightSpin.getSelectedItem().toString().equals("lbs")){
                item.setWeightLbs(Float.valueOf(weightInput.getText().toString()));
            }
            else{
                float kgToLbs = convertKgToLbs(Float.valueOf(weightInput.getText().toString()));
                item.setWeightLbs(kgToLbs);
            }
            if(weightSpin.getSelectedItem().toString().equals("kg")){
                item.setWeightKg(Float.valueOf(weightInput.getText().toString()));
            }
            else{
                float lbsToKg = convertLbsToKg(Float.valueOf(weightInput.getText().toString()));
                item.setWeightLbs(lbsToKg);
            }
            if(!medName.getText().toString().trim().isEmpty()){
                item.setMedicationBrand(medName.getText().toString());
            }
            if(!medDose.getText().toString().trim().isEmpty()){
                String dosageAmount = medDose.getText().toString();
                String dosageUnit = doseSpin.getSelectedItem().toString();
                item.setMedicationDosage(dosageAmount+dosageUnit);
            }
            if(temperatureSpin.getSelectedItem().toString().contains(("C"))){
                item.setBodyTemperatureCelsius(Float.valueOf(temperatureInput.getText().toString()));
            }
            else{
                float fahToCel = convertFahrenheitToCelsius(Float.valueOf(temperatureInput.getText().toString()));
                item.setBodyTemperatureCelsius(fahToCel);
            }
            if(temperatureSpin.getSelectedItem().toString().contains(("F"))){
                item.setBodyTemperatureFahrenheit(Float.valueOf(temperatureInput.getText().toString()));
            }
            else{
                float celToFah = convertCelsiusToFahrenheit(Float.valueOf(temperatureInput.getText().toString()));
                item.setBodyTemperatureCelsius(celToFah);
            }
            if(!systolicInput.getText().toString().trim().isEmpty()){
                item.setSystolic(Float.valueOf(systolicInput.getText().toString()));
            }
            if(!diastolicInput.getText().toString().trim().isEmpty()){
                item.setDiastolic(Float.valueOf(diastolicInput.getText().toString()));
            }
            if(!heartRate.getText().toString().trim().isEmpty()){
                item.setHeartRate(Float.valueOf(heartRate.getText().toString()));
            }
            item.setDescription(healthDescription.getText().toString());

            // Insert the new item
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        final ActivityTable entity = addItemInTable(item);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.add(entity);
                            }
                        });
                    } catch (final Exception e) {
                        createAndShowDialogFromTask(e, "Error");
                    }
                    return null;
                }
            };

            runAsyncTask(task);

            errorDialog.setTitle("Health Activity Added")
                    .setMessage(titleInput.getText().toString() + " has been successfully added to List.")
                    .show();

            titleInput.setText("");
            weightInput.setText("");
            medName.setText("");
            medDose.setText("");
            temperatureInput.setText("");
            systolicInput.setText("");
            diastolicInput.setText("");
            heartRate.setText("");
            healthDescription.setText("");
        }
    }

    public ActivityTable addItemInTable(ActivityTable item) throws ExecutionException, InterruptedException {
        ActivityTable entity = mActivityTable.insert(item).get();
        return entity;
    }

    private void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<ActivityTable> results = refreshItemsFromMobileServiceTable();

                    //Offline Sync
                    //final List<ActivityTable> results = refreshItemsFromMobileServiceTableSyncTable();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();

                            for (ActivityTable item : results) {
                                mAdapter.add(item);
                            }
                        }
                    });
                } catch (final Exception e){
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);
    }

    private List<ActivityTable> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException, MobileServiceException {
        //return mActivityTable.where().field("complete").eq(val(false)).execute().get();
        return mActivityTable.execute().get();
    }

    private AsyncTask<Void, Void, Void> initLocalStore() throws MobileServiceLocalStoreException, ExecutionException, InterruptedException {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    MobileServiceSyncContext syncContext = mClient.getSyncContext();

                    if (syncContext.isInitialized())
                        return null;

                    SQLiteLocalStore localStore = new SQLiteLocalStore(mClient.getContext(), "OfflineStore", null, 1);

                    Map<String, ColumnDataType> tableDefinition = new HashMap<String, ColumnDataType>();
                    tableDefinition.put("id", ColumnDataType.String);
                    tableDefinition.put("activity_title", ColumnDataType.String);
                    tableDefinition.put("pain_intensity", ColumnDataType.Real);
                    tableDefinition.put("bodytemperature_celsius", ColumnDataType.Real);
                    tableDefinition.put("bodytemperature_fahrenheit", ColumnDataType.Real);
                    tableDefinition.put("activity_description", ColumnDataType.String);
                    tableDefinition.put("weight_lbs", ColumnDataType.Real);
                    tableDefinition.put("weight_kg", ColumnDataType.Real);
                    tableDefinition.put("medication_brand", ColumnDataType.String);
                    tableDefinition.put("medication_dosage", ColumnDataType.String);
                    tableDefinition.put("systolic", ColumnDataType.Real);
                    tableDefinition.put("diastolic", ColumnDataType.Real);
                    tableDefinition.put("heart_rate", ColumnDataType.Real);
                    tableDefinition.put("bmi", ColumnDataType.Real);

                    localStore.defineTable("ActivityTable", tableDefinition);

                    SimpleSyncHandler handler = new SimpleSyncHandler();

                    syncContext.initialize(localStore, handler).get();

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }

                return null;
            }
        };

        return runAsyncTask(task);
    }

    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }

    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    private void createAndShowDialog(final String message, final String title) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    /**
     * NEEDS CHANGE?
     * MAY APPLY TO LIST
     */
    private class ProgressFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                        }
                    });

                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
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
            //Intent is not needed as the Health Activity button leads to this page
        } else if (id == R.id.nav_amList) {
            Intent iList = new Intent(this, ListActivity.class);
            startActivity(iList);

        } else if (id == R.id.nav_amResults) {
            Intent iResults = new Intent(this, ResultsActivity.class);
            startActivity(iResults);

        } else if (id == R.id.nav_amLogout) {
            //Logout is not available at this moment
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
