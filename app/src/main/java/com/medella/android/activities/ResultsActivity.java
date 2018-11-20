package com.medella.android.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.medella.android.MedellaOptions;
import com.medella.android.R;
import com.medella.android.list.ResultsCollection;
import com.medella.android.list.ActivityTable;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.round;

public class ResultsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private LineChart bmiLineChart;
    private float bmiAverage;
    private float bmiDifference;

    private float pintAverage;
    private float pintDifference;
    private StringBuilder pintStringBuilder = new StringBuilder();

    private float weightLbsAverage;
    private float weightLbsDifference;
    private float weightKgAverage;
    private float weightKgDifference;
    private boolean weightPreference;
    private StringBuilder weightStringBuilder = new StringBuilder();

    private float tempCelsAverage;
    private float tempFahrAverage;
    private float tempCelsDifference;
    private float tempFahrDifference;
    private boolean temperaturePreference;
    private StringBuilder temperatureStringBuilder = new StringBuilder();

    private float systolicAverage;
    private float diastolicAverage;
    private float systolicDifference;
    private float diastolicDifference;

    private float heartRateAverage;
    private float heartRateDifference;

    private DecimalFormat df = new DecimalFormat("###.00");

    private MobileServiceClient mClient;
    private MobileServiceTable<ActivityTable> mActivityTable;
    //Offline Sync
    //private MobileServiceSyncTable<ActivityTable> mActivityTable;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        mProgressBar = (ProgressBar) findViewById(R.id.loadResultsProgressBar);
        mProgressBar.setVisibility(ProgressBar.GONE);

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

            // Offline Sync
            //mActivityTable = mClient.getSyncTable("ActivityTable", ActivityTable.class);

            //Init local storage
            initLocalStore().get();

            // MUST GET FIXED
            //ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
            // MUST GET FIXED
            //listViewToDo.setAdapter(mAdapter);

            // Load the items from the Mobile Service
            refreshItemsFromTable();

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            createAndShowDialog(e, "Error");
        }

        /** ------------!!!!!!!!!!!!!!!!!!!!!!----------------- */
        /** ------------- GRAPH PART - START ------------------ */
        bmiLineChart = (LineChart)findViewById(R.id.lineChart);

        // NEW - from Examples
        bmiLineChart.setBackgroundColor(Color.WHITE);
        bmiLineChart.getDescription().setEnabled(false);
        bmiLineChart.setTouchEnabled(true); //May need to switch to false
        bmiLineChart.setDrawGridBackground(false);
        bmiLineChart.setDragEnabled(true);
        bmiLineChart.setScaleEnabled(true);
        bmiLineChart.setPinchZoom(true);
        XAxis xAxis;
        {
            xAxis = bmiLineChart.getXAxis();
            xAxis.enableGridDashedLine(10f,10f,0f);
        }
        YAxis yAxis;
        {
            yAxis = bmiLineChart.getAxisLeft();
            bmiLineChart.getAxisRight().setEnabled(false);
            yAxis.enableGridDashedLine(10f,10f,0f);
            yAxis.setAxisMaximum(50);
            yAxis.setAxisMinimum(0);
        }
        ArrayList<Entry> bmiValues = new ArrayList<>();
        List<Float> bmiCollection = new ArrayList<>(); //Testing
        float bmiOne = (float) 20.6;
        float bmiTwo = (float) 28.6;
        float bmiThree = (float) 21.1;
        bmiCollection.add(bmiOne); //Testing
        bmiCollection.add(bmiTwo); //Testing
        bmiCollection.add(bmiThree); //Testing
        // JUST TESTING
        for(int i=0;i<bmiCollection.size();i++) {
            bmiValues.add(new Entry(i, bmiCollection.get(i)));
            //bmiAverage += bmiCollection.get(i);
        }

        //bmiAverage = Float.parseFloat(df.format(bmiAverage/bmiCollection.size())); //Testing
        bmiAverage = Float.parseFloat(df.format(ResultsCollection.getBmiAverage(getApplicationContext())));
        //bmiDifference = bmiCollection.get(bmiCollection.size()-1) - bmiCollection.get(bmiCollection.size()-2); //Testing
        bmiDifference = Float.parseFloat(df.format(ResultsCollection.getBmiDifference(getApplicationContext())));
        TextView tvBmiResult = findViewById(R.id.txtBmiResult);
        TextView tvBmiDifference = findViewById(R.id.txtBmiDifference);
        tvBmiResult.setText(String.valueOf(bmiAverage));
        // COLORING FOR BMI AVERAGE NOT FINISHED YET
        if(bmiAverage > 18 && bmiAverage <= 25){
            tvBmiResult.setTextColor(Color.parseColor("#3ebc70"));
        }
        if(bmiDifference > 0) {
            tvBmiDifference.setText("(+" + bmiDifference + ")");
            tvBmiDifference.setTextColor(Color.parseColor("#F28622"));
        }
        else if(bmiDifference < 0){
            tvBmiDifference.setText("("+bmiDifference+")");
            tvBmiDifference.setTextColor(Color.parseColor("#3ebc70"));
        }
        else {
            tvBmiDifference.setText("("+bmiDifference+")");
            tvBmiDifference.setTextColor(Color.GRAY);
        }

        pintAverage = Float.parseFloat(df.format(ResultsCollection.getPainIntensityAverage(getApplicationContext())));
        pintStringBuilder.append(String.valueOf(pintAverage));
        pintDifference = Float.parseFloat(df.format(ResultsCollection.getPainIntensityDifference(getApplicationContext())));
        TextView tvPintResult = findViewById(R.id.txtPintResult);
        tvPintResult.setText(pintStringBuilder);
        TextView tvPintDiff = findViewById(R.id.txtPintDifference);
        if(pintDifference > 0){
            tvPintDiff.setText("(+" + pintDifference + ")");
            tvPintDiff.setTextColor(Color.RED);
        } else if(pintDifference < 0){
            tvPintDiff.setText("("+pintDifference+")");
            tvPintDiff.setTextColor(Color.parseColor("#3ebc70"));
        } else{
            tvPintDiff.setText("("+pintDifference+")");
            tvPintDiff.setTextColor(Color.GRAY);
        }

        weightLbsAverage = Float.parseFloat(df.format(ResultsCollection.getWeightLbsAverage(getApplicationContext())));
        weightLbsDifference = Float.parseFloat(df.format(ResultsCollection.getWeightLbsDifference(getApplicationContext())));
        weightKgAverage = Float.parseFloat(df.format(ResultsCollection.getWeightKgAverage(getApplicationContext())));
        weightKgDifference = Float.parseFloat(df.format(ResultsCollection.getWeightKgDifference(getApplicationContext())));
        weightPreference = MedellaOptions.getPreferredWeightUnit(getApplicationContext());
        TextView tvWeightDifference = findViewById(R.id.txtWeightDifference);
        if(weightPreference) {
            weightStringBuilder.append(String.valueOf(weightLbsAverage));
            weightStringBuilder.append(" lbs");
            if(weightLbsDifference > 0) {
                tvWeightDifference.setText("(+" + weightLbsDifference + ")");
                tvWeightDifference.setTextColor(Color.parseColor("#F28622"));
            }
            else if(weightLbsDifference < 0){
                tvWeightDifference.setText("(" + weightLbsDifference + ")");
                tvWeightDifference.setTextColor(Color.parseColor("#3ebc70"));
            }
            else{
                tvWeightDifference.setText("(" + weightLbsDifference + ")");
                tvWeightDifference.setTextColor(Color.GRAY);
            }
        }
        else {
            weightStringBuilder.append(String.valueOf(weightKgAverage));
            weightStringBuilder.append(" kg");
            if(weightKgDifference > 0) {
                tvWeightDifference.setText("(+" + weightKgDifference + ")");
                tvWeightDifference.setTextColor(Color.parseColor("#F28622"));
            }
            else if(weightKgDifference < 0){
                tvWeightDifference.setText("(" + weightKgDifference+ ")");
                tvWeightDifference.setTextColor(Color.parseColor("#3ebc70"));
            }
            else{
                tvWeightDifference.setText("(" + weightKgDifference + ")");
                tvWeightDifference.setTextColor(Color.GRAY);
            }
        }
        TextView tvWeightResult = findViewById(R.id.txtWeightResult);
        tvWeightResult.setText(weightStringBuilder);

        tempCelsAverage = Float.parseFloat(df.format(ResultsCollection.getTemperatureCelsiusAverage(getApplicationContext())));
        tempCelsDifference = Float.parseFloat(df.format(ResultsCollection.getTemperatureCelsiusDifference(getApplicationContext())));
        tempFahrAverage = Float.parseFloat(df.format(ResultsCollection.getTemperatureFahrAverage(getApplicationContext())));
        tempFahrDifference = Float.parseFloat(df.format(ResultsCollection.getTemperatureFahrDifference(getApplicationContext())));
        temperaturePreference = MedellaOptions.getPreferredBodyTemperatureUnit(getApplicationContext());
        TextView tvTemperatureDifference = findViewById(R.id.txtBtempDifference);
        if(temperaturePreference) {
            temperatureStringBuilder.append(String.valueOf(tempCelsAverage));
            temperatureStringBuilder.append("°C");
            if(tempCelsDifference > 0) {
                tvTemperatureDifference.setText("(+" + tempCelsDifference + ")");
                tvTemperatureDifference.setTextColor(Color.parseColor("#F28622"));
            }
            else if(tempCelsDifference < 0){
                tvTemperatureDifference.setText("(" + tempCelsDifference + ")");
                tvTemperatureDifference.setTextColor(Color.parseColor("#228ef2"));
            }
            else{
                tvTemperatureDifference.setText("(" + tempCelsDifference + ")");
                tvTemperatureDifference.setTextColor(Color.GRAY);
            }
        }
        else {
            temperatureStringBuilder.append(String.valueOf(tempFahrAverage));
            temperatureStringBuilder.append("°F");
            if(tempFahrDifference > 0) {
                tvTemperatureDifference.setText("(+" + tempFahrDifference + ")");
                tvTemperatureDifference.setTextColor(Color.parseColor("#F28622"));
            }
            else if(weightKgDifference < 0){
                tvTemperatureDifference.setText("(" + tempFahrDifference + ")");
                tvTemperatureDifference.setTextColor(Color.parseColor("#228ef2"));
            }
            else{
                tvTemperatureDifference.setText("(" + tempFahrDifference + ")");
                tvTemperatureDifference.setTextColor(Color.GRAY);
            }
        }
        TextView tvTemperatureResult = findViewById(R.id.txtBtempResult);
        tvTemperatureResult.setText(temperatureStringBuilder);

        systolicAverage = Float.parseFloat(df.format(ResultsCollection.getSystolicAverage(getApplicationContext())));
        diastolicAverage = Float.parseFloat(df.format(ResultsCollection.getDiastolicAverage(getApplicationContext())));
        systolicDifference = Float.parseFloat(df.format(ResultsCollection.getSystolicDifference(getApplicationContext())));
        diastolicDifference = Float.parseFloat(df.format(ResultsCollection.getDiastolicDifference(getApplicationContext())));
        TextView tvSystolicResult = findViewById(R.id.txtSystolicResult);
        TextView tvDiastolicResult = findViewById(R.id.txtDiastolicResult);
        TextView tvSystolicDifference = findViewById(R.id.txtSystolicDifference);
        TextView tvDiastolicDifference = findViewById(R.id.txtDiastolicDifference);
        tvSystolicResult.setText(String.valueOf(systolicAverage));
        tvDiastolicResult.setText(String.valueOf(diastolicAverage));
        if(systolicDifference > 0) {
            tvSystolicDifference.setText("(+" + systolicDifference + ")");
        }
        else {
            tvSystolicDifference.setText("(" + systolicDifference + ")");
        }
        tvSystolicDifference.setTextColor(Color.GRAY);
        if(diastolicDifference > 0){
            tvDiastolicDifference.setText("(+" + diastolicDifference + ")");
        }
        else{
            tvDiastolicDifference.setText("(" + diastolicDifference + ")");
        }
        tvDiastolicDifference.setTextColor(Color.GRAY);

        heartRateAverage = Float.parseFloat(df.format(ResultsCollection.getHeartRateAverage(getApplicationContext())));
        heartRateDifference = Float.parseFloat(df.format(ResultsCollection.getHeartRateDifference(getApplicationContext())));
        TextView tvHeartRateResult = findViewById(R.id.txtHrateResult);
        TextView tvHeartRateDifference = findViewById(R.id.txtHrateDifference);
        tvHeartRateResult.setText(String.valueOf(heartRateAverage));
        if(heartRateDifference < 0) {
            tvHeartRateDifference.setText("(" + heartRateDifference + ")");
            tvHeartRateDifference.setTextColor(Color.parseColor("#3ebc70"));
        }
        else if(heartRateDifference > 0){
            tvHeartRateDifference.setText("(+" + heartRateDifference + ")");
            tvHeartRateDifference.setTextColor(Color.parseColor("#F28622"));
        }
        else {
            tvHeartRateDifference.setText("(" + heartRateDifference + ")");
            tvHeartRateDifference.setTextColor(Color.GRAY);
        }

        // DOESN'T WORK
        //collectITEMSFROMTABLE();

        LineDataSet bmiLds;
        if(bmiLineChart.getData() != null && bmiLineChart.getData().getDataSetCount() > 0){
            bmiLds = (LineDataSet) bmiLineChart.getData().getDataSetByIndex(0);
            bmiLds.setValues(bmiValues);
            bmiLds.notifyDataSetChanged();
            bmiLineChart.getData().notifyDataChanged();
            bmiLineChart.notifyDataSetChanged();
        }
        else{
            bmiLds = new LineDataSet(bmiValues, "BMI");
            bmiLds.setDrawIcons(false);
            bmiLds.enableDashedLine(10f,0f,0f); //spaceLength is 0 so lines are not dashed
            bmiLds.setColor(Color.BLACK);
            bmiLds.setCircleColor(Color.BLACK);
            bmiLds.setLineWidth(1f);
            bmiLds.setCircleRadius(2.5f);
            bmiLds.setDrawCircleHole(false);
            bmiLds.setFormLineWidth(1f);
            bmiLds.setFormLineDashEffect(new DashPathEffect(new float[]{10f,5f},0f));
            bmiLds.setFormSize(15f);
            bmiLds.setValueTextSize(9f);
            bmiLds.enableDashedHighlightLine(10f,0f,0f);
            bmiLds.setDrawFilled(true);
            bmiLds.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return bmiLineChart.getAxisLeft().getAxisMinimum();
                }
            });
            if(Utils.getSDKInt() >= 18){
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_orange);
                bmiLds.setFillDrawable(drawable);
            } else {
                bmiLds.setFillColor(Color.BLACK);
            }
            ArrayList<ILineDataSet> bmiDatasets = new ArrayList<>();
            bmiDatasets.add(bmiLds);
            LineData bmiData = new LineData(bmiDatasets);
            bmiLineChart.setData(bmiData);
        }
        bmiLineChart.animateX(1500);
        Legend legend = bmiLineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);

        /*
        ArrayList<String> xAXES = new ArrayList<>();
        ArrayList<Entry> yAXES_1 = new ArrayList<>();
        ArrayList<Entry> yAXES_2 = new ArrayList<>();
        float x = 0;
        float x1 = 0;
        double xAll = 0;
        int numDataPoints = 30;
        for(int i=0;i<numDataPoints;i++){
            if(x%7 != 0){
                x = x + 5;
                x1 = x*2/3;
            }
            else{
                x = x-(x+(i-3));
                x1 = x1 + 5;
            }
            xAll = xAll + 1;
            yAXES_1.add(new Entry(x,i));
            yAXES_2.add(new Entry(x1,i));
            xAXES.add(i, String.valueOf(xAll));
        }
        String[] xaxes = new String[xAXES.size()];
        for(int i=0;i<xAXES.size();i++){
            xaxes[i] = xAXES.get(i).toString();
        }
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
        LineDataSet lineDataSet1 = new LineDataSet(yAXES_1, "lab1");
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setColor(Color.BLUE);
        LineDataSet lineDataSet2 = new LineDataSet(yAXES_2, "lab2");
        lineDataSet2.setDrawCircles(false);
        lineDataSet2.setColor(Color.RED);
        lineDataSets.add(lineDataSet1);
        lineDataSets.add(lineDataSet2);
        bmiLineChart.setData(new LineData(lineDataSets));
        */
        /** ------------!!!!!!!!!!!!!!!!!!!!!!----------------- */
        /** ------------- GRAPH PART - END ------------------ */

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

    /** !!!!!!!!!!!!!!!! AZURE CHUNK -- START !!!!!!!!!!!!!!!!!!!!!!!!! */
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
                            //mAdapter.clear();

                            /*for (ActivityTable item : results) {
                                mAdapter.add(item);
                            }*/
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
        //return mActivityTable.where().field("deleted").eq(val(false)).orderBy("createdAt", QueryOrder.Descending).execute().get();
        return mActivityTable.orderBy("createdAt", QueryOrder.Descending).execute().get();
    }

    // NEW LISTS
    private List<ActivityTable> getBmiFromMobileService() throws ExecutionException, InterruptedException{
        return mActivityTable.where().field("bmi").gt(0).select("bmi").orderBy("createdAt", QueryOrder.Ascending).execute().get();
    }
    private List<ActivityTable> getPainIntensityFromMobileService() throws ExecutionException, InterruptedException{
        return mActivityTable.select("pain_intensity").execute().get();
    }
    private List<ActivityTable> getHeartRatesFromMobileService() throws ExecutionException, InterruptedException{
        return mActivityTable.where().field("heart_rate").gt(0).select("heart_rate").execute().get();
    }

    // DOESN'T WORK
    /*private void collectITEMSFROMTABLE() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final List<ActivityTable> bmiResults = getBmiFromMobileService();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BMI_COLLECTION.clear();

                            for (ActivityTable result : bmiResults) {
                                BMI_COLLECTION.add(result);
                            }
                        }
                    });
                } catch (final Exception e){
                    createAndShowDialog(e, "Error");
                }

                return null;
            }
        };

        runAsyncTask(task);
    }*/

    //Offline Sync
    /**
     * Refresh the list with the items in the Mobile Service Sync Table
     */
    /*private List<ActivityTable> refreshItemsFromMobileServiceTableSyncTable() throws ExecutionException, InterruptedException {
        //sync the data
        sync().get();
        //Query query = QueryOperations.field("complete").
        //        eq(val(false));
        Query query = QueryOperations.field("deleted").eq(val(false)).orderBy("createdAt",QueryOrder.Descending);
        return mActivityTable.read(query).get();
    }*/

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
                    tableDefinition.put("deleted", ColumnDataType.Boolean);
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

    //Offline Sync
    /**
     * Sync the current context and the Mobile Service Sync Table
     * @return
     */
    /*private AsyncTask<Void, Void, Void> sync() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    MobileServiceSyncContext syncContext = mClient.getSyncContext();
                    syncContext.push().get();
                    mActivityTable.pull(null).get();
                } catch (final Exception e) {
                    //createAndShowDialogFromTask(e, "Error"); //Hide error when running offline
                    e.printStackTrace();
                }
                return null;
            }
        };
        return runAsyncTask(task);
    }*/

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
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

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

    public void refreshList(View view) {
        refreshItemsFromTable();
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
    /** !!!!!!!!!!!!!!!!!!!!!!! AZURE CHUNK -- END !!!!!!!!!!!!!!!!!!!!!! */

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

        }
        else if (id == R.id.nav_amSettings) {
            Intent iSettings = new Intent(this, SettingsActivity.class);
            startActivity(iSettings);
        }
        else if (id == R.id.nav_amLogout) {
            //Logout is not available at this moment
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
