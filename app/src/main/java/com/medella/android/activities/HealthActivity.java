package com.medella.android.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.medella.android.SaveHealthActivity;
import com.medella.android.UpdateHealthActivity;
import com.medella.android.options.MedellaOptions;
import com.medella.android.R;
import com.medella.android.list.ActivityTable;
import com.medella.android.list.ListActivityAdapter;
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
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.squareup.okhttp.OkHttpClient;

import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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

    private ListView listViewToDo;
    public boolean areAllInputsValid = false;
    
    public boolean bodyTemperatureError, systolicError, diastolicError, heartRateError = true;

    private MobileServiceClient mClient;
    private MobileServiceTable<ActivityTable> mActivityTable;

    private ListActivityAdapter mListActivityAdapter;

    @Nullable
    private EditText etMedicationBrand, etMedicationDosage, etBodyTemperature, etHeartRate, etLocation;
    private EditText etTitle, etWeight, etSystolic, etDiastolic, etDescription;
    private Spinner spWeight, spBodyTemperature, spMedicationDosage, spPainIntensity;
    private ProgressBar mProgressBar;
    private ImageButton ibPickLocation;
    private ImageView imageCameraView;

    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;

    private boolean useLbsUnit, useCelsiusUnit;
    private float useDefaultWeight;

    private String saveTitle, saveMedName, saveLocation, saveDescription;
    private float saveWeight;
    private int saveWeightSpin, savePintSpin, saveDoseSpin, saveTempSpin;
    private float saveDosage, saveTemp, saveSystolic, saveDiastolic, saveHrate;
    private boolean weightUnitChanged, tempUnitChanged;

    private Button addButton, updateButton;

    private Connection con;
    private UpdateActivityTask mUpdateTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        listViewToDo = findViewById(R.id.listViewToDo);

        mProgressBar = findViewById(R.id.loadingProgressBar);
        mProgressBar.setVisibility(ProgressBar.GONE);
        imageCameraView = findViewById(R.id.cameraImage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view_health);
        navigationView.setNavigationItemSelectedListener(this);

        Spinner dosageSpin = findViewById(R.id.doseSpinner);
        dosageSpin.setSelection(15); //Set mg (Milligram) as default item in Dosage spinner

        useLbsUnit = MedellaOptions.getPreferredWeightUnit(getApplicationContext());
        useCelsiusUnit = MedellaOptions.getPreferredBodyTemperatureUnit(getApplicationContext());

        useDefaultWeight = MedellaOptions.getDefaultWeight(getApplicationContext());

        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://medellapp.azurewebsites.net",
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

            etTitle = findViewById(R.id.txtTitle);
            etWeight = findViewById(R.id.txtWeight);
            etMedicationBrand = findViewById(R.id.txtMedName);
            etMedicationDosage = findViewById(R.id.txtDosage);
            etBodyTemperature = findViewById(R.id.txtTemperature);
            etSystolic = findViewById(R.id.txtSystolic);
            etDiastolic = findViewById(R.id.txtDiastolic);
            etHeartRate = findViewById(R.id.txtHrate);
            etLocation = findViewById(R.id.txtLocation);
            etDescription = findViewById(R.id.txtDesc);
            spWeight = findViewById(R.id.weightSpinner);
            spBodyTemperature = findViewById(R.id.tempSpinner);
            spMedicationDosage = findViewById(R.id.doseSpinner);
            spPainIntensity = findViewById(R.id.pintSpinner);
            ibPickLocation = findViewById(R.id.btnLocation);

            addButton = findViewById(R.id.btnHfinish);
            updateButton = findViewById(R.id.btnHupdate);

            if(!UpdateHealthActivity.isUpdate(getApplicationContext())){
                addButton.setVisibility(View.VISIBLE);
                updateButton.setVisibility(View.GONE);
            }
            else{
                addButton.setVisibility(View.GONE);
                updateButton.setVisibility(View.VISIBLE);

                etTitle.setText(UpdateHealthActivity.getTitle(getApplicationContext()));
                if(useLbsUnit)
                    etWeight.setText(String.valueOf(UpdateHealthActivity.getWeightLbs(getApplicationContext())));
                else
                    etWeight.setText(String.valueOf(UpdateHealthActivity.getWeightKg(getApplicationContext())));
                spPainIntensity.setSelection((int)(UpdateHealthActivity.getPainIntensity(getApplicationContext()) - 1));
                etMedicationBrand.setText(UpdateHealthActivity.getMedName(getApplicationContext()));
                // Medication dose amount and unit
                if(useCelsiusUnit)
                    if(UpdateHealthActivity.getTemperatureCels(getApplicationContext()) > 0)
                        etBodyTemperature.setText(String.valueOf(UpdateHealthActivity.getTemperatureCels(getApplicationContext())));
                else
                    if(UpdateHealthActivity.getTemperatureFahr(getApplicationContext()) > 0)
                        etBodyTemperature.setText(String.valueOf(UpdateHealthActivity.getTemperatureFahr(getApplicationContext())));
                if(UpdateHealthActivity.getSystolic(getApplicationContext()) > 0)
                    etSystolic.setText(String.valueOf(UpdateHealthActivity.getSystolic(getApplicationContext())));
                if(UpdateHealthActivity.getDiastolic(getApplicationContext()) > 0)
                    etDiastolic.setText(String.valueOf(UpdateHealthActivity.getDiastolic(getApplicationContext())));
                if(UpdateHealthActivity.getHeartRate(getApplicationContext()) > 0)
                    etHeartRate.setText(String.valueOf(UpdateHealthActivity.getHeartRate(getApplicationContext())));
                etLocation.setText(UpdateHealthActivity.getLocation(getApplicationContext()));
                etDescription.setText(UpdateHealthActivity.getDescription(getApplicationContext()));
            }

            /**
             * SAVEHEALTHACTIVITY
             */
            // MAY NEED TO REMOVE THIS -- GONNA MOVE IT
            /*saveTitle = SaveHealthActivity.getSavedTitle(getApplicationContext());
            saveWeight = SaveHealthActivity.getSavedWeight(getApplicationContext());
            saveWeightSpin = SaveHealthActivity.getSavedWeightUnit(getApplicationContext());
            savePintSpin = SaveHealthActivity.getSavedPainIntensity(getApplicationContext());
            saveMedName = SaveHealthActivity.getSavedMedName(getApplicationContext());
            saveDosage = SaveHealthActivity.getSavedMedDosage(getApplicationContext());
            saveDoseSpin = SaveHealthActivity.getSavedMedUnit(getApplicationContext());
            saveTemp = SaveHealthActivity.getSavedTemperature(getApplicationContext());
            saveTempSpin = SaveHealthActivity.getSavedTempUnit(getApplicationContext());
            saveSystolic = SaveHealthActivity.getSavedSystolic(getApplicationContext());
            saveDiastolic = SaveHealthActivity.getSavedDiastolic(getApplicationContext());
            saveHrate = SaveHealthActivity.getSavedHeartRate(getApplicationContext());
            saveLocation = SaveHealthActivity.getSavedLocation(getApplicationContext());
            saveDescription = SaveHealthActivity.getSavedDescription(getApplicationContext());
            weightUnitChanged = false;
            tempUnitChanged = false;

            spMedicationDosage.setSelection(15);*/

            // Set selections for Weight and Temperature spinners based on user's preferences from Settings
            if(useLbsUnit)
                spWeight.setSelection(0);
            else
                spWeight.setSelection(1);
            if(useCelsiusUnit)
                spBodyTemperature.setSelection(0);
            else
                spBodyTemperature.setSelection(1);

            if(useDefaultWeight > 0)
                etWeight.setText(String.valueOf(useDefaultWeight));

            /**
             * SAVEHEALTHACTIVITY
             */
            //getSavedDetailsAfterIntent();

            // Create an adapter to bind the items with the view
            mListActivityAdapter = new ListActivityAdapter(this, R.layout.activity_card_view);
            //ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
            listViewToDo.setAdapter(mListActivityAdapter);

            // Load the items from the Mobile Service
            refreshItemsFromTable();

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            //createAndShowDialog(e, "Error"); //Hide obsolete error message
            e.printStackTrace();
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

    public void cameraClick(View view) {
        /**
         * SAVEHEALTHACTIVITY
         */
        //saveDetailsBeforeIntent();
        Intent iCamera = new Intent(this, CameraActivity.class);
        startActivityForResult(iCamera,0);
    }

    private void validateInput(){
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
            //errorDialog.setMessage("Please enter a valid input.");
        }
    }

    public void finishClick(View view) {

        // Error dialog when user enters invalid input
        AlertDialog.Builder errorDialog = new AlertDialog.Builder(this);
        errorDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        // Success dialog when user enters valid input
        AlertDialog.Builder successDialog = new AlertDialog.Builder(this);
        successDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent iList = new Intent(HealthActivity.this, ListActivity.class);
                startActivity(iList);
            }
        });
        successDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent iList = new Intent(HealthActivity.this, ListActivity.class);
                startActivity(iList);
            }
        });

        validateInput();

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
                item.setMedicationDosage(dosageAmount+" "+dosageUnit);
            }
            if(etBodyTemperature.getText().toString().isEmpty()) {
                /*item.setBodyTemperatureCelsius(Float.parseFloat(null));
                item.setBodyTemperatureFahrenheit(Float.parseFloat(null));*/
                item.setBodyTemperatureCelsius(-1);
                item.setBodyTemperatureFahrenheit(-1);
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
                item.setSystolic(-1);
            }
            else{
                item.setSystolic(Float.valueOf(etSystolic.getText().toString()));
            }
            if(etDiastolic.getText().toString().isEmpty()){
                item.setDiastolic(-1);
            }
            else{
                item.setDiastolic(Float.valueOf(etDiastolic.getText().toString()));
            }
            if(etHeartRate.getText().toString().matches("")){
                item.setHeartRate(-1);
            }
            else{
                item.setHeartRate(Float.valueOf(etHeartRate.getText().toString()));
            }

            // Height must be gathered from user's data
            // MUST GET FIXED
            float heightInMeters = (float) 1.81;
            item.setBmi(calculateBmi(item.getWeightKg(), heightInMeters));

            item.setDescription(etDescription.getText().toString());
            if(etLocation.getText().toString().matches("")) {
                item.setLocation(null);
            }
            else{
                item.setLocation(etLocation.getText().toString());
            }


            // Insert the new item
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        final ActivityTable entity = addItemInTable(item);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mListActivityAdapter.add(entity);
                            }
                        });
                    } catch (final Exception e) {
                        createAndShowDialogFromTask(e, "Error");
                    }
                    return null;
                }
            };

            runAsyncTask(task);

            successDialog.setTitle("Health Activity Added")
                    .setMessage(etTitle.getText().toString() + " has been successfully added to List.")
                    .show();

            //resetSavedInfoAfterAdd(); //Reset saved activity details after adding successfully
        }
    }

    /*public void getSavedDetailsAfterIntent(String saveTitle, float saveWeight, int savePintSpin,
                                                String saveMedName, float saveDosage, int saveDoseSpin,
                                                float saveTemp, float saveSystolic, float saveDiastolic,
                                                float saveHrate, String saveLocation, String saveDescription)*/
    public void getSavedDetailsAfterIntent()
    {
        if(saveTitle != null)
            etTitle.setText(saveTitle);
        if(saveWeight > 0 || saveWeight != useDefaultWeight)
            etWeight.setText(String.valueOf(saveWeight));
        if(weightUnitChanged)
            spWeight.setSelection(saveWeightSpin);
        spPainIntensity.setSelection(savePintSpin);
        if(saveMedName != null)
            etMedicationBrand.setText(saveMedName);
        if(saveDosage > 0)
            etMedicationDosage.setText(String.valueOf(saveDosage));
        //if(saveDoseSpin != 15)
        spMedicationDosage.setSelection(saveDoseSpin);
        if(saveTemp > 0)
            etBodyTemperature.setText(String.valueOf(saveTemp));
        if(tempUnitChanged)
            spBodyTemperature.setSelection(saveTempSpin);
        if(saveSystolic > 0)
            etSystolic.setText(String.valueOf(saveSystolic));
        if(saveDiastolic > 0)
            etDiastolic.setText(String.valueOf(saveDiastolic));
        if(saveHrate > 0)
            etHeartRate.setText(String.valueOf(saveHrate));
        if(saveLocation != null)
            etLocation.setText(saveLocation);
        if(saveDescription != null)
            etDescription.setText(saveDescription);
    }

    public void saveDetailsBeforeIntent()
    {
        if(!etTitle.getText().toString().trim().isEmpty())
            SaveHealthActivity.saveTitle(getApplicationContext(), etTitle.getText().toString());
        if(!etWeight.getText().toString().trim().isEmpty())
            SaveHealthActivity.saveWeight(getApplicationContext(), Float.parseFloat(etWeight.getText().toString()));
        if((useLbsUnit && spWeight.getSelectedItemId() != 0) || (!useLbsUnit && spWeight.getSelectedItemId() != 1)) {
            SaveHealthActivity.saveWeightUnit(getApplicationContext(), (int)spWeight.getSelectedItemId());
            weightUnitChanged = true;
        }
        SaveHealthActivity.savePainIntensity(getApplicationContext(),(int)spPainIntensity.getSelectedItemId());
        if(!etMedicationBrand.getText().toString().trim().isEmpty())
            SaveHealthActivity.saveMedName(getApplicationContext(),etMedicationBrand.getText().toString());
        if(!etMedicationDosage.getText().toString().trim().isEmpty())
            SaveHealthActivity.saveMedDosage(getApplicationContext(),Float.parseFloat(etMedicationDosage.getText().toString()));
        SaveHealthActivity.saveMedUnit(getApplicationContext(),(int)spMedicationDosage.getSelectedItemId());
        if(!etBodyTemperature.getText().toString().trim().isEmpty())
            SaveHealthActivity.saveTemperature(getApplicationContext(),Float.parseFloat(etBodyTemperature.getText().toString()));
        if((useCelsiusUnit && spBodyTemperature.getSelectedItemId() != 0) || (!useCelsiusUnit && spBodyTemperature.getSelectedItemId() != 1)){
            SaveHealthActivity.saveTempUnit(getApplicationContext(),(int)spBodyTemperature.getSelectedItemId());
            tempUnitChanged = true;
        }
        if(!etSystolic.getText().toString().trim().isEmpty())
            SaveHealthActivity.saveSystolic(getApplicationContext(),Float.parseFloat(etSystolic.getText().toString()));
        if(!etDiastolic.getText().toString().trim().isEmpty())
            SaveHealthActivity.saveDiastolic(getApplicationContext(),Float.parseFloat(etDiastolic.getText().toString()));
        if(!etHeartRate.getText().toString().isEmpty())
        SaveHealthActivity.saveHeartRate(getApplicationContext(),Float.parseFloat(etHeartRate.getText().toString()));
        if(!etLocation.getText().toString().trim().isEmpty())
            SaveHealthActivity.saveLocation(getApplicationContext(),etLocation.getText().toString());
        if(!etDescription.getText().toString().trim().isEmpty())
            SaveHealthActivity.saveDescription(getApplicationContext(),etDescription.getText().toString());
    }

    public void resetSavedInfoAfterAdd(){
        SaveHealthActivity.saveTitle(getApplicationContext(), null);
        SaveHealthActivity.saveWeight(getApplicationContext(), 0);
        SaveHealthActivity.saveWeightUnit(getApplicationContext(), 0);
        SaveHealthActivity.savePainIntensity(getApplicationContext(),0);
        SaveHealthActivity.saveMedName(getApplicationContext(),null);
        SaveHealthActivity.saveMedDosage(getApplicationContext(),0);
        SaveHealthActivity.saveMedUnit(getApplicationContext(),15);
        SaveHealthActivity.saveTemperature(getApplicationContext(),0);
        SaveHealthActivity.saveTempUnit(getApplicationContext(),0);
        SaveHealthActivity.saveSystolic(getApplicationContext(),0);
        SaveHealthActivity.saveDiastolic(getApplicationContext(),0);
        SaveHealthActivity.saveHeartRate(getApplicationContext(),0);
        SaveHealthActivity.saveLocation(getApplicationContext(),null);
        SaveHealthActivity.saveDescription(getApplicationContext(),null);
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
                            mListActivityAdapter.clear();

                            for (ActivityTable item : results) {
                                mListActivityAdapter.add(item);
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

    private AsyncTask<Void, Void, Void> initLocalStore() {

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
        super.onActivityResult(requestCode, resultCode, data);

        // This is for camera image
        // MUST GET FIXED
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        imageCameraView.setImageBitmap(bitmap);

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
                    etLocation.setText(null); // Location field will not be affected if location is missing or empty
                }
                else { // Address strings can apply to business locations including restaurants, banks, etc.
                    String addressString = stBuilder.toString(); // Address that does not initially include DMS coordinates
                    etLocation.setText(addressString); // Location with no DMS coordinates will be automatically added to Location field
                }
            }
        }
    }

    public void updateActivity(View view) {
        // Error dialog when user enters invalid input
        AlertDialog.Builder errorDialog = new AlertDialog.Builder(this);
        errorDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        // Success dialog when user enters valid input
        /*AlertDialog.Builder successDialog = new AlertDialog.Builder(this);
        successDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent iList = new Intent(HealthActivity.this, ListActivity.class);
                startActivity(iList);
            }
        });
        successDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent iList = new Intent(HealthActivity.this, ListActivity.class);
                startActivity(iList);
            }
        });*/

        validateInput();

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

        if(areAllInputsValid){
            String activityId = UpdateHealthActivity.getActivityId(getApplicationContext());
            String activityTitle = etTitle.getText().toString();
            int painIntensity = (int)(spPainIntensity.getSelectedItemId()+1);
            float bTempCels, bTempFahr;
            if(spBodyTemperature.getSelectedItemId() == 0){
                if(etBodyTemperature.getText().toString().isEmpty()){
                    bTempCels = -1;
                    bTempFahr = -1;
                }
                else {
                    bTempCels = Float.parseFloat(etBodyTemperature.getText().toString());
                    bTempFahr = convertCelsiusToFahrenheit(Float.parseFloat(etBodyTemperature.getText().toString()));
                }
            }
            else{
                if(etBodyTemperature.getText().toString().isEmpty()){
                    bTempFahr = -1;
                    bTempCels = -1;
                }
                else {
                    bTempFahr = Float.parseFloat(etBodyTemperature.getText().toString());
                    bTempCels = convertFahrenheitToCelsius(Float.parseFloat(etBodyTemperature.getText().toString()));
                }
            }
            String activityDesc = etDescription.getText().toString();
            float weightLbs, weightKg;
            if(spWeight.getSelectedItemId() == 0){
                weightLbs = Float.parseFloat(etWeight.getText().toString());
                weightKg = convertLbsToKg(Float.parseFloat(etWeight.getText().toString()));
            }
            else{
                weightKg = Float.parseFloat(etWeight.getText().toString());
                weightLbs = convertKgToLbs(Float.parseFloat(etWeight.getText().toString()));
            }
            String medBrand;
            if(etMedicationBrand.getText().toString().isEmpty()){
                medBrand = null;
            }
            else{
                medBrand = etMedicationBrand.getText().toString();
            }
            // Will deal with MEDICATION DOSAGE later
            float systolic, diastolic, heartRate;
            if(etSystolic.getText().toString().isEmpty())
                systolic = -1;
            else
                systolic = Float.parseFloat(etSystolic.getText().toString());
            if(etDiastolic.getText().toString().isEmpty())
                diastolic = -1;
            else
                diastolic = Float.parseFloat(etDiastolic.getText().toString());
            if(etHeartRate.getText().toString().isEmpty())
                heartRate = -1;
            else
                heartRate = Float.parseFloat(etHeartRate.getText().toString());
            String location;
            if(etLocation.getText().toString().isEmpty())
                location = null;
            else
                location = etLocation.getText().toString();
            UpdateActivityTask updateActivityTask = new UpdateActivityTask(activityId,activityTitle,
                    painIntensity, bTempCels, bTempFahr, activityDesc, weightLbs, weightKg, medBrand,
                    systolic, diastolic, heartRate, location);
            updateActivityTask.execute("");

            resetUpdateHealthActivity();
        }
    }

    private void resetUpdateHealthActivity(){
        UpdateHealthActivity.processUpdate(getApplicationContext(), false);

        UpdateHealthActivity.setTitle(getApplicationContext(), null);
        UpdateHealthActivity.setWeightLbs(getApplicationContext(), 0);
        UpdateHealthActivity.setWeightKg(getApplicationContext(), 0);
        UpdateHealthActivity.setPainIntensity(getApplicationContext(), 0);
        UpdateHealthActivity.setMedName(getApplicationContext(), null);
        // Medication dose amount and unit
        UpdateHealthActivity.setTemperatureCels(getApplicationContext(), 0);
        UpdateHealthActivity.setTemperatureFahr(getApplicationContext(), 0);
        UpdateHealthActivity.setSystolic(getApplicationContext(), 0);
        UpdateHealthActivity.setDiastolic(getApplicationContext(), 0);
        UpdateHealthActivity.setHeartRate(getApplicationContext(), 0);
        UpdateHealthActivity.setLocation(getApplicationContext(), null);
        UpdateHealthActivity.setDescription(getApplicationContext(), null);
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

    public class UpdateActivityTask extends AsyncTask<String, String, String> {

        private final String mActivityId, mActivityTitle, mMedBrand, mLocation, mDescription;
        private final int mPainIntensity;
        private final float mTempCels, mTempFahr, mWeightLbs, mWeightKg, mSystolic, mDiastolic, mHeartRate;
        // NEED TO DEAL WITH MEDICATION DOSAGE
        String updateMsg = "";
        Boolean isSuccess = false;

        UpdateActivityTask(String activityId, String activityTitle, int painIntensity, float tempCels,
                           float tempFahr, String desc, float weightLbs, float weightKg, String medBrand,
                           float systolic, float diastolic, float heartRate, String location){
            mActivityId = activityId;
            mActivityTitle = activityTitle;
            mPainIntensity = painIntensity;
            mTempCels = tempCels;
            mTempFahr = tempFahr;
            mDescription = desc;
            mWeightLbs = weightLbs;
            mWeightKg = weightKg;
            mMedBrand = medBrand;
            // NEED TO DEAL WITH MEDICATION DOSAGE
            mSystolic = systolic;
            mDiastolic = diastolic;
            mHeartRate = heartRate;
            mLocation = location;
        }

        protected void onPreExecute(){
        }
        @Override
        protected void onPostExecute(String r){
            if(isSuccess) {
                AlertDialog.Builder successDialog = new AlertDialog.Builder(HealthActivity.this);
                successDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent iList = new Intent(HealthActivity.this, ListActivity.class);
                        startActivity(iList);
                    }
                });
                successDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Intent iList = new Intent(HealthActivity.this, ListActivity.class);
                        startActivity(iList);
                    }
                });
                successDialog.setTitle("Update Successful").setMessage(updateMsg);
                successDialog.show();
            }
            else{
                AlertDialog.Builder errorDialog = new AlertDialog.Builder(HealthActivity.this);
                errorDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                errorDialog.setTitle("Update Error")
                        .setMessage(updateMsg)
                        .show();
            }
        }

        @Override
        protected String doInBackground(String... params){
            try{
                con = connectionClass();
                if(con==null){
                    updateMsg = "Please check your internet connection";
                    isSuccess = false;
                }
                else{
                    String queryUpdate = "update activitytable set ";

                    String queryUpdateTitle = "activity_title=\'" + mActivityTitle + "\',";
                    String queryUpdatePint = "pain_intensity=\'" + mPainIntensity + "\',";
                    String queryUpdateTempCels = "bodytemperature_celsius=\'" + mTempCels + "\',";
                    String queryUpdateTempFahr = "bodytemperature_fahrenheit=\'" + mTempCels + "\',";
                    String queryUpdateDesc = "activity_description=\'" + mDescription + "\',";
                    String queryUpdateWeightLbs = "weight_lbs=\'" + mWeightLbs + "\',";
                    String queryUpdateWeightKg = "weight_kg=\'" + mWeightKg + "\',";
                    String queryUpdateMedBrand;
                    if(mMedBrand!=null)
                        queryUpdateMedBrand = "medication_brand=\'" + mMedBrand + "\',";
                    else
                        queryUpdateMedBrand = "medication_brand=null,";
                    // Will deal with MEDICATION DOSAGE later
                    String queryUpdateSystolic = "systolic=\'" + mSystolic + "\',";
                    String queryUpdateDiastolic = "diastolic=\'" + mDiastolic + "\',";
                    String queryUpdateHeartRate = "heart_rate=\'" + mHeartRate + "\',";
                    String queryUpdateLocation;
                    if(mLocation!=null)
                        queryUpdateLocation = "activity_location=\'" + mLocation + "\'"; // Last entry for UPDATE statement
                    else
                        queryUpdateLocation = "activity_location=null"; // Last entry for UPDATE statement

                    String queryWhereId = " where id=\'" + mActivityId + "\'";

                    String updateActivityQuery = queryUpdate + queryUpdateTitle + queryUpdatePint +
                            queryUpdateTempCels + queryUpdateTempFahr + queryUpdateDesc + queryUpdateWeightLbs +
                            queryUpdateWeightKg + queryUpdateMedBrand + queryUpdateSystolic + queryUpdateDiastolic +
                            queryUpdateHeartRate + queryUpdateLocation + queryWhereId;

                    Statement updateActivity = con.createStatement();
                    updateActivity.executeUpdate(updateActivityQuery);
                    updateMsg = mActivityTitle + " has been successfully updated.";
                    isSuccess = true;
                    con.close();
                }
            } catch (SQLException e) {
                updateMsg = "We hit a snag. Please check back later.";
                isSuccess = false;
                e.printStackTrace();
            }
            return updateMsg;
        }

        /*@Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }*/

        /*@Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }*/
    }

    public Connection connectionClass(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL = null;
        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            //connectionURL = "jdbc:jtds:sqlserver://medellapp.database.windows.net:1433;database=MedellaData;user=MedellaAdmin@medellapp;password=C0ntrolHe@lth;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
            connectionURL = "jdbc:jtds:sqlserver://medellapp.database.windows.net:1433;DatabaseName=MedellaData;user=MedellaAdmin@medellapp;password=C0ntrolHe@lth;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
            connection = DriverManager.getConnection(connectionURL);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            resetUpdateHealthActivity();
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
            resetUpdateHealthActivity();
            Intent iHome = new Intent(this, HomeActivity.class);
            startActivity(iHome);
        } else if (id == R.id.nav_amActivity) {
            //Intent is not needed as the Health Activity button leads to this page
        } else if (id == R.id.nav_amList) {
            resetUpdateHealthActivity();
            Intent iList = new Intent(this, ListActivity.class);
            startActivity(iList);
        } else if (id == R.id.nav_amResults) {
            resetUpdateHealthActivity();
            Intent iResults = new Intent(this, ResultsActivity.class);
            startActivity(iResults);
        }
        else if (id == R.id.nav_amSettings) {
            resetUpdateHealthActivity();
            Intent iSettings = new Intent(this, SettingsActivity.class);
            startActivity(iSettings);
        }
        else if (id == R.id.nav_amLogout) {
            //Logout is not available at this moment
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
