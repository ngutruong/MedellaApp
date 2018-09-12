package com.medella.android.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.medella.android.R;
import com.medella.android.list.ActivityTable;
import com.medella.android.list.ActivityTableAdapter;
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

import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

public class HealthActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    public boolean areAllInputsValid = false;
    
    public boolean bodyTemperatureError = true;
    public boolean systolicError = true;
    public boolean diastolicError = true;
    public boolean heartRateError = true;

    private MobileServiceClient mClient;
    private MobileServiceTable<ActivityTable> mActivityTable;

    private ActivityTableAdapter mAdapter;
    
    private EditText etTitle;
    private EditText etWeight;
    private EditText etMedicationBrand;
    private EditText etMedicationDosage;
    private EditText etBodyTemperature;
    private EditText etSystolic;
    private EditText etDiastolic;
    private EditText etHeartRate;
    private EditText etLocation;
    private EditText etDescription;
    private Spinner spWeight;
    private Spinner spBodyTemperature;
    private Spinner spMedicationDosage;
    private Spinner spPainIntensity;
    private ProgressBar mProgressBar;
    private ImageButton ibPickLocation;

    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        mProgressBar = (ProgressBar)findViewById(R.id.loadingProgressBar);
        mProgressBar.setVisibility(ProgressBar.GONE);

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
            
            //Init local storage
            initLocalStore().get();

            etTitle = (EditText)findViewById(R.id.txtTitle);
            etWeight = (EditText)findViewById(R.id.txtWeight);
            etMedicationBrand = (EditText)findViewById(R.id.txtMedName);
            etMedicationDosage = (EditText)findViewById(R.id.txtDosage);
            etBodyTemperature = (EditText)findViewById(R.id.txtTemperature);
            etSystolic = (EditText)findViewById(R.id.txtSystolic);
            etDiastolic = (EditText)findViewById(R.id.txtDiastolic);
            etHeartRate = (EditText)findViewById(R.id.txtHrate);
            etLocation = (EditText)findViewById(R.id.txtLocation);
            etDescription = (EditText)findViewById(R.id.txtDesc);
            spWeight = (Spinner)findViewById(R.id.weightSpinner);
            spBodyTemperature = (Spinner)findViewById(R.id.tempSpinner);
            spMedicationDosage = (Spinner)findViewById(R.id.doseSpinner);
            spPainIntensity = (Spinner)findViewById(R.id.pintSpinner);
            ibPickLocation = (ImageButton)findViewById(R.id.btnLocation);

            // Create an adapter to bind the items with the view
            mAdapter = new ActivityTableAdapter(this, R.layout.activity_card_view);
            ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
            listViewToDo.setAdapter(mAdapter);

            // Load the items from the Mobile Service
            refreshItemsFromTable();

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            createAndShowDialog(e, "Error");
        }

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        ibPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(HealthActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private float convertCelsiusToFahrenheit(float celsiusTemp)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        float calculatedFahrenheit = (celsiusTemp*9/5) + 32;
        return Float.parseFloat(df.format(calculatedFahrenheit));
    }
    private float convertFahrenheitToCelsius(float fahrenheitTemp)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        float calculatedCelsius = ((fahrenheitTemp - 32)*5)/9;
        return Float.parseFloat(df.format(calculatedCelsius));
    }
    private float convertLbsToKg(float weightLbs)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        float calculatedKg = (float) (weightLbs*(1/2.2));
        return Float.parseFloat(df.format(calculatedKg));
    }
    private float convertKgToLbs(float weightKg)
    {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        float calculatedLbs = (float) (weightKg*2.2);
        return Float.parseFloat(df.format(calculatedLbs));
    }
    private float calculateBmi(float weightKg, float heightMeter){
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        float bmi = (float) (weightKg/(Math.pow(heightMeter, 2)));
        return Float.parseFloat(df.format(bmi));
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
            if(etTitle.getText().toString().trim().isEmpty()){
                collectErrors+="- Title is empty.\n";
            }

            //WEIGHT VALIDATION IN HEALTH ACTIVITY PAGE
            if(etWeight.getText().toString().trim().isEmpty()){
                collectErrors+="- Weight is empty.\n";
            }
            else if(spWeight.getSelectedItem().toString().equals("lbs") && Float.valueOf(etWeight.getText().toString()) < 7.7){
                collectErrors+="- Please enter a valid weight.\n";
            }
            else if(spWeight.getSelectedItem().toString().equals("kg") && Float.valueOf(etWeight.getText().toString()) < 3.5){
                collectErrors+="- Please enter a valid weight.\n";
            }

            //HEALTH DESCRIPTION VALIDATION FOR HEALTH ACTIVITY PAGE
            if(etDescription.getText().toString().trim().isEmpty()){
                collectErrors+="- Medical description is empty.\n";
            }

            //BODY TEMPERATURE VALIDATION IN HEALTH ACTIVITY PAGE
            if(Float.valueOf(etBodyTemperature.getText().toString()) == 0){
                bodyTemperatureError = true;
                collectErrors+="- Body temperature must not be 0.\n";
            } else if (Float.valueOf(etBodyTemperature.getText().toString()) < 35 && spBodyTemperature.getSelectedItem().toString().equals("degrees Celsius")) {
                bodyTemperatureError = true;
                collectErrors+="- Please enter a valid body temperature.\n";
            } else if (Float.valueOf(etBodyTemperature.getText().toString()) < 95 && spBodyTemperature.getSelectedItem().toString().equals("degrees Fahrenheit")) {
                bodyTemperatureError = true;
                collectErrors+="- Please enter a valid body temperature.\n";
            } else {
                bodyTemperatureError = false; // no error will trigger if body temperature input is empty or valid
            }

            /*
            VALIDATION IS ERRONEOUS--MESSAGES SHOULD SHOW
             */

            //SYSTOLIC PRESSURE VALIDATION IN HEALTH ACTIVITY PAGE
            if(Float.valueOf(etSystolic.getText().toString()) < 90){
                systolicError = true;
                collectErrors+="- Systolic pressure must not be less than 90.\n";
            }
            else if(Float.valueOf(etSystolic.getText().toString()) > 250){
                systolicError = true;
                collectErrors+="- Systolic pressure must not be greater than 250.\n";
            }
            else if(!etSystolic.getText().toString().trim().isEmpty() && etDiastolic.getText().toString().trim().isEmpty()){
                systolicError = true;
                collectErrors+="- Diastolic pressure must not be empty while systolic pressure is filled.\n";
            }
            else{
                systolicError = false; // no error will trigger if systolic and diastolic inputs are empty or valid
            }

            //DIASTOLIC PRESSURE VALIDATION IN HEALTH ACTIVITY PAGE
            if(Float.valueOf(etDiastolic.getText().toString()) < 60){
                diastolicError = true;
                collectErrors+="- Diastolic pressure must not be less than 60.\n";
            }
            else if(Float.valueOf(etDiastolic.getText().toString()) > 140){
                diastolicError = true;
                collectErrors+="- Diastolic pressure must not be greater than 140.\n";
            }
            else if(etSystolic.getText().toString().trim().isEmpty() && !etDiastolic.getText().toString().trim().isEmpty()){
                diastolicError = true;
                collectErrors+="- Systolic pressure must not be empty while diastolic pressure is filled.\n";
            }
            else {
                diastolicError = false; // no error will trigger if diastolic and systolic inputs are empty or valid
            }

            //HEART RATE VALIDATION IN HEALTH ACTIVITY PAGE
            if(Float.valueOf(etHeartRate.getText().toString()) < 20 && Float.valueOf(etHeartRate.getText().toString()) > 0){
                heartRateError = true;
                collectErrors+="- Heart rate is not valid.\n";

            }
            else if(Float.valueOf(etHeartRate.getText().toString()) == 0 && !etHeartRate.getText().toString().trim().isEmpty()){
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

        if(collectErrors.trim().length() > 0 && heartRateError==true && bodyTemperatureError==true && systolicError==true && diastolicError==true) {
            errorDialog.setTitle("Error")
                    .setMessage("Health activity is not complete due to the following errors:\n\n" + collectErrors)
                    .show();
            collectErrors = ""; //Reset errors
            areAllInputsValid = false; //All inputs will not be saved if one or more inputs are not valid
        }
        else{
            areAllInputsValid = true; //All inputs can be saved if they are valid
        }

        if(areAllInputsValid==true){

            if (mClient == null) {
                return;
            }

            // Create a new item
            final ActivityTable item = new ActivityTable();

            item.setActivityTitle(etTitle.getText().toString());
            if(spPainIntensity.getSelectedItem().toString().contains("Most Painful")){
                item.setPainIntensity(10);
            }
            else{
                String getNumber = spPainIntensity.getSelectedItem().toString().substring(0,1);
                float painNumber = Float.valueOf(getNumber);
                item.setPainIntensity(painNumber);
            }
            if(spWeight.getSelectedItem().toString().equals("lbs")){
                item.setWeightLbs(Float.valueOf(etWeight.getText().toString()));
                float lbsToKg = convertLbsToKg(Float.valueOf(etWeight.getText().toString()));
                item.setWeightKg(lbsToKg);
            }
            else if(spWeight.getSelectedItem().toString().equals("kg")){
                item.setWeightKg(Float.valueOf(etWeight.getText().toString()));
                float kgToLbs = convertKgToLbs(Float.valueOf(etWeight.getText().toString()));
                item.setWeightLbs(kgToLbs);
            }
            if(etMedicationBrand.getText().toString().isEmpty()){
                item.setMedicationBrand(null);
            }
            else{
                item.setMedicationBrand(etMedicationBrand.getText().toString());
            }
            if(etMedicationDosage.getText().toString().isEmpty()){
                item.setMedicationDosage(null);
            }
            else{
                String dosageAmount = etMedicationDosage.getText().toString();
                String dosageUnit = spMedicationDosage.getSelectedItem().toString();
                item.setMedicationDosage(dosageAmount+dosageUnit);
            }
            if(etBodyTemperature.getText().toString().isEmpty()) {
                item.setBodyTemperatureCelsius(Float.parseFloat(null));
                item.setBodyTemperatureFahrenheit(Float.parseFloat(null));
                
            }
            else{
                if (spBodyTemperature.getSelectedItem().toString().contains(("C"))) {
                    item.setBodyTemperatureCelsius(Float.valueOf(etBodyTemperature.getText().toString()));
                    float celToFah = convertCelsiusToFahrenheit(Float.valueOf(etBodyTemperature.getText().toString()));
                    item.setBodyTemperatureFahrenheit(celToFah);
                } else if (spBodyTemperature.getSelectedItem().toString().contains(("F"))) {
                    float fahToCel = convertFahrenheitToCelsius(Float.valueOf(etBodyTemperature.getText().toString()));
                    item.setBodyTemperatureCelsius(fahToCel);
                    item.setBodyTemperatureFahrenheit(Float.valueOf(etBodyTemperature.getText().toString()));
                }
            }
            if(etSystolic.getText().toString().isEmpty()){
                item.setSystolic(Float.parseFloat(null));
            }
            else{
                item.setSystolic(Float.valueOf(etSystolic.getText().toString()));
            }
            if(etDiastolic.getText().toString().isEmpty()){
                item.setDiastolic(Float.parseFloat(null));
            }
            else{
                item.setDiastolic(Float.valueOf(etDiastolic.getText().toString()));
            }
            if(etHeartRate.getText().toString().isEmpty()){
                item.setHeartRate(Float.parseFloat(null));
            }
            else{
                item.setHeartRate(Float.valueOf(etHeartRate.getText().toString()));
            }

            float heightInMeters = (float) 1.81;
            item.setBmi(calculateBmi(item.getWeightKg(), heightInMeters));

            item.setDescription(etDescription.getText().toString());
            item.setLocation(etLocation.getText().toString());


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
                    .setMessage(etTitle.getText().toString() + " has been successfully added to List.")
                    .show();

            etTitle.setText("");
            etWeight.setText("");
            etMedicationBrand.setText("");
            etMedicationDosage.setText("");
            etBodyTemperature.setText("");
            etSystolic.setText("");
            etDiastolic.setText("");
            etHeartRate.setText("");
            etDescription.setText("");
            etLocation.setText("");
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
                    tableDefinition.put("activity_location", ColumnDataType.String);
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

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(ibPickLocation, connectionResult.getErrorMessage() + "", Snackbar.LENGTH_LONG).show();
    }

    /**
     * Add or remove coordinates to Location field
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                final StringBuilder stBuilder = new StringBuilder();
                String placename = String.format("%s", place.getName());
                final String address = String.format("%s", place.getAddress());
                stBuilder.append(placename);
                stBuilder.append(", ");
                stBuilder.append(address);

                // Regular expression for DMS coordinates
                final String coordinatesRegex = "([0-9]{1,2})[:|°]([0-9]{1,2})[:|'|′]?([0-9]{1,2}(?:\\.[0-9]+){0,1})?[\"|″]([N|S])\\s([0-9]{1,3})[:|°]([0-9]{1,2})[:|'|′]?([0-9]{1,2}(?:\\.[0-9]+){0,1})?[\"|″]([E|W]),\\s";

                AlertDialog.Builder coordinatesDialog = new AlertDialog.Builder(this); // Create alert dialog where user has the option to add or remove DMS coordinates
                coordinatesDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() { // Coordinates will not be removed if user selects Yes
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String addressString = stBuilder.toString(); // Address string with coordinates
                        etLocation.setText(addressString); // Address and coordinates will be added to Location field
                    }
                });
                coordinatesDialog.setNegativeButton("No", new DialogInterface.OnClickListener() { // Coordinates will be removed if user selects No
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String addressString = stBuilder.toString(); // Address string with coordinates
                        String addressWithoutCoordinates = addressString.replaceAll(coordinatesRegex, ""); // Remove DMS coordinates
                        etLocation.setText(addressWithoutCoordinates); //Address will only be added to Location field
                    }
                });
                coordinatesDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() { // Location field will not be affected if user selects Cancel
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                // Set title and message for coordinatesDialog
                coordinatesDialog.setTitle("Coordinates detected")
                        .setMessage("Coordinates have been detected. Do you wish to add coordinates next to the selected address?");

                if(Pattern.compile(coordinatesRegex).matcher(stBuilder.toString()).find()){
                    coordinatesDialog.show(); // coordinatesDialog will show if stBuilder contains DMS coordinates
                }
                else if (stBuilder.toString().equals(", ")) {
                    etLocation.setText(""); // Location field will not be affected if location is missing or empty
                }
                else { // Address strings can apply to business locations including restaurants, banks, etc.
                    String addressString = stBuilder.toString(); // Address that does not initially include DMS coordinates
                    etLocation.setText(addressString); // Location with no DMS coordinates will be automatically added to Location field
                }
            }
        }
    }

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

        }else if (id == R.id.nav_amLogout) {
            //Logout is not available at this moment
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
