package com.medella.android.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.medella.android.AccountStatus;
import com.medella.android.R;
import com.medella.android.list.ResultsCollection;
import com.medella.android.options.MedellaOptions;
import com.medella.android.options.ResultsGraphOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private LineChart resultsLineChart;
    private float bmiAverage;
    private float bmiDifference;
    private List<Float> bmiList;

    private float pintAverage;
    private float pintDifference;
    private List<Float> pintList;

    private float weightLbsAverage;
    private float weightLbsDifference;
    private float weightKgAverage;
    private float weightKgDifference;
    private boolean useLbsUnit;
    private StringBuilder weightStringBuilder = new StringBuilder();
    private List<Float> weightLbsList;
    private List<Float> weightKgList;

    private float tempCelsAverage;
    private float tempFahrAverage;
    private float tempCelsDifference;
    private float tempFahrDifference;
    private boolean useCelsiusUnit;
    private StringBuilder temperatureStringBuilder = new StringBuilder();
    private List<Float> tempCelsList;
    private List<Float> tempFahrList;

    private float systolicAverage;
    private float diastolicAverage;
    private float systolicDifference;
    private float diastolicDifference;
    private List<Float> systolicList;
    private List<Float> diastolicList;

    private float heartRateAverage;
    private float heartRateDifference;
    private List<Float> heartRateList;

    private DecimalFormat df = new DecimalFormat("###.00");

    private String[] graphOptions;
    private int rgOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        graphOptions = getResources().getStringArray(R.array.graph_options_array);

        //bmiAverage = Float.parseFloat(df.format(bmiAverage/bmiCollection.size())); //Testing
        bmiAverage = Float.parseFloat(df.format(ResultsCollection.getBmiAverage(getApplicationContext())));
        //bmiDifference = bmiCollection.get(bmiCollection.size()-1) - bmiCollection.get(bmiCollection.size()-2); //Testing
        bmiDifference = Float.parseFloat(df.format(ResultsCollection.getBmiDifference(getApplicationContext())));
        bmiList = ResultsCollection.getBmiList();
        float bmiMax = Collections.max(bmiList, null);
        TextView tvBmiResult = findViewById(R.id.txtBmiResult);
        TextView tvBmiDifference = findViewById(R.id.txtBmiDifference);
        tvBmiResult.setText(String.valueOf(bmiAverage));
        if(bmiAverage > 18 && bmiAverage <= 25){
            tvBmiResult.setTextColor(Color.parseColor("#3ebc70"));
        }
        // COLORING BMI AVERAGE - END
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
        // Collect BMI elements for graph
        ArrayList<Entry> bmiValues = new ArrayList<>();
        List<Float> bmiCollection = new ArrayList<>();
        for(float bmiValue : bmiList){
            bmiCollection.add(bmiValue);
        }
        for(int i=0;i<bmiCollection.size();i++) {
            bmiValues.add(new Entry(i, bmiCollection.get(i)));
        }

        pintAverage = Float.parseFloat(df.format(ResultsCollection.getPainIntensityAverage(getApplicationContext())));
        pintDifference = Float.parseFloat(df.format(ResultsCollection.getPainIntensityDifference(getApplicationContext())));
        pintList = ResultsCollection.getPintList();
        float pintMax = Collections.max(pintList, null);
        TextView tvPintResult = findViewById(R.id.txtPintResult);
        tvPintResult.setText(String.valueOf(pintAverage));
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
        // Collect Pain Intensity elements for graph
        ArrayList<Entry> pintValues = new ArrayList<>();
        List<Float> pintCollection = new ArrayList<>();
        for(float pintValue : pintList){
            pintCollection.add(pintValue);
        }
        for(int i=0;i<pintCollection.size();i++){
            pintValues.add(new Entry(i, pintCollection.get(i)));
        }

        weightLbsAverage = Float.parseFloat(df.format(ResultsCollection.getWeightLbsAverage(getApplicationContext())));
        weightLbsDifference = Float.parseFloat(df.format(ResultsCollection.getWeightLbsDifference(getApplicationContext())));
        weightKgAverage = Float.parseFloat(df.format(ResultsCollection.getWeightKgAverage(getApplicationContext())));
        weightKgDifference = Float.parseFloat(df.format(ResultsCollection.getWeightKgDifference(getApplicationContext())));
        useLbsUnit = MedellaOptions.getPreferredWeightUnit(getApplicationContext());
        weightLbsList = ResultsCollection.getWeightLbsList();
        weightKgList = ResultsCollection.getWeightKgList();
        float weightLbsMax = Collections.max(weightLbsList, null);
        float weightKgMax = Collections.max(weightKgList, null);
        TextView tvWeightDifference = findViewById(R.id.txtWeightDifference);
        if(useLbsUnit) {
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
        // Collect weight elements for graph
        ArrayList<Entry> weightLbsValues = new ArrayList<>();
        ArrayList<Entry> weightKgValues = new ArrayList<>();
        List<Float> weightLbsCollection = new ArrayList<>();
        List<Float> weightKgCollection = new ArrayList<>();
        for(float lbs : weightLbsList){
            weightLbsCollection.add(lbs);
        }
        for(int i=0;i<weightLbsCollection.size();i++) {
            weightLbsValues.add(new Entry(i, weightLbsCollection.get(i)));
        }
        for(float kg : weightKgList){
            weightKgCollection.add(kg);
        }
        for(int i=0;i<weightKgCollection.size();i++) {
            weightKgValues.add(new Entry(i, weightKgCollection.get(i)));
        }

        tempCelsAverage = Float.parseFloat(df.format(ResultsCollection.getTemperatureCelsiusAverage(getApplicationContext())));
        tempCelsDifference = Float.parseFloat(df.format(ResultsCollection.getTemperatureCelsiusDifference(getApplicationContext())));
        tempFahrAverage = Float.parseFloat(df.format(ResultsCollection.getTemperatureFahrAverage(getApplicationContext())));
        tempFahrDifference = Float.parseFloat(df.format(ResultsCollection.getTemperatureFahrDifference(getApplicationContext())));
        useCelsiusUnit = MedellaOptions.getPreferredBodyTemperatureUnit(getApplicationContext());
        tempCelsList = ResultsCollection.getTempCelsiusList();
        tempFahrList = ResultsCollection.getTempFahrenheitList();
        float tempCelsMax = Collections.max(tempCelsList, null);
        float tempFahrMax = Collections.max(tempFahrList, null);
        TextView tvTemperatureDifference = findViewById(R.id.txtBtempDifference);
        if(useCelsiusUnit) {
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
            else if(tempFahrDifference < 0){
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
        // Collect temperature elements for graph
        ArrayList<Entry> tempCelsValues = new ArrayList<>();
        ArrayList<Entry> tempFahrValues = new ArrayList<>();
        List<Float> tempCelsCollection = new ArrayList<>();
        List<Float> tempFahrCollection = new ArrayList<>();
        for(float cel : tempCelsList){
            tempCelsCollection.add(cel);
        }
        for(int i=0;i<tempCelsCollection.size();i++) {
            tempCelsValues.add(new Entry(i, tempCelsCollection.get(i)));
        }
        for(float fah : tempFahrList){
            tempFahrCollection.add(fah);
        }
        for(int i=0;i<tempFahrCollection.size();i++) {
            tempFahrValues.add(new Entry(i, tempFahrCollection.get(i)));
        }

        systolicAverage = Float.parseFloat(df.format(ResultsCollection.getSystolicAverage(getApplicationContext())));
        diastolicAverage = Float.parseFloat(df.format(ResultsCollection.getDiastolicAverage(getApplicationContext())));
        systolicDifference = Float.parseFloat(df.format(ResultsCollection.getSystolicDifference(getApplicationContext())));
        diastolicDifference = Float.parseFloat(df.format(ResultsCollection.getDiastolicDifference(getApplicationContext())));
        systolicList = ResultsCollection.getSystolicList();
        diastolicList = ResultsCollection.getDiastolicList();
        float systolicMax = Collections.max(systolicList, null);
        float diastolicMax = Collections.max(diastolicList, null);
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
        // Collect blood pressure elements for graph
        ArrayList<Entry> systolicValues = new ArrayList<>();
        ArrayList<Entry> diastolicValues = new ArrayList<>();
        List<Float> systolicCollection = new ArrayList<>();
        List<Float> diastolicCollection = new ArrayList<>();
        for(float sys : systolicList){
            systolicCollection.add(sys);
        }
        for(int i=0;i<systolicCollection.size();i++) {
            systolicValues.add(new Entry(i, systolicCollection.get(i)));
        }
        for(float dia : diastolicList){
            diastolicCollection.add(dia);
        }
        for(int i=0;i<diastolicCollection.size();i++) {
            diastolicValues.add(new Entry(i, diastolicCollection.get(i)));
        }

        heartRateAverage = Float.parseFloat(df.format(ResultsCollection.getHeartRateAverage(getApplicationContext())));
        heartRateDifference = Float.parseFloat(df.format(ResultsCollection.getHeartRateDifference(getApplicationContext())));
        heartRateList = ResultsCollection.getHeartRateList();
        float heartRateMax = Collections.max(heartRateList, null);
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
        // Collect blood pressure elements for graph
        ArrayList<Entry> heartRateValues = new ArrayList<>();
        List<Float> heartRateCollection = new ArrayList<>();
        for(float hrate : heartRateList){
            heartRateCollection.add(hrate);
        }
        for(int i=0;i<heartRateCollection.size();i++) {
            heartRateValues.add(new Entry(i, heartRateCollection.get(i)));
        }

        resultsLineChart = findViewById(R.id.lineChart);
        rgOption = ResultsGraphOptions.getResultsGraphOption(getApplicationContext());

        // NEW - from Examples
        resultsLineChart.setBackgroundColor(Color.WHITE);
        resultsLineChart.getDescription().setEnabled(false);
        resultsLineChart.setTouchEnabled(true); //May need to switch to false
        resultsLineChart.setDrawGridBackground(false);
        resultsLineChart.setDragEnabled(true);
        resultsLineChart.setScaleEnabled(true);
        resultsLineChart.setPinchZoom(true);
        XAxis xAxis;
        {
            xAxis = resultsLineChart.getXAxis();
            xAxis.enableGridDashedLine(10f,10f,0f);
        }
        YAxis yAxis;
        {
            yAxis = resultsLineChart.getAxisLeft();
            resultsLineChart.getAxisRight().setEnabled(false);
            yAxis.enableGridDashedLine(10f,10f,0f);
            switch(rgOption){
                case 0:     yAxis.setAxisMaximum(bmiMax + 20);
                            break;
                case 1:     yAxis.setAxisMaximum(pintMax + 10);
                            break;
                case 2:     yAxis.setAxisMaximum(weightLbsMax + 100);
                            break;
                case 3:     yAxis.setAxisMaximum(weightKgMax + 50);
                            break;
                case 4:     yAxis.setAxisMaximum(tempCelsMax + 20);
                            break;
                case 5:     yAxis.setAxisMaximum(tempFahrMax + 100);
                            break;
                case 6:     yAxis.setAxisMaximum(systolicMax + 100);
                            break;
                case 7:     yAxis.setAxisMaximum(diastolicMax + 50);
                            break;
                case 8:     yAxis.setAxisMaximum(heartRateMax + 100);
                            break;
                default:    yAxis.setAxisMaximum(bmiMax + 20);
                            break;
            }
            yAxis.setAxisMinimum(0);
        }

        LineDataSet resultsLds;
        if(resultsLineChart.getData() != null && resultsLineChart.getData().getDataSetCount() > 0){
            resultsLds = (LineDataSet) resultsLineChart.getData().getDataSetByIndex(0);
            //bmiLds.setValues(bmiValues);
            switch(rgOption){
                case 0:     resultsLds.setValues(bmiValues);
                            break;
                case 1:     resultsLds.setValues(pintValues);
                            break;
                case 2:     resultsLds.setValues(weightLbsValues);
                            break;
                case 3:     resultsLds.setValues(weightKgValues);
                            break;
                case 4:     resultsLds.setValues(tempCelsValues);
                            break;
                case 5:     resultsLds.setValues(tempFahrValues);
                            break;
                case 6:     resultsLds.setValues(systolicValues);
                            break;
                case 7:     resultsLds.setValues(diastolicValues);
                            break;
                case 8:     resultsLds.setValues(heartRateValues);
                            break;
                default:    resultsLds.setValues(bmiValues);
                            break;
            }
            resultsLds.notifyDataSetChanged();
            resultsLineChart.getData().notifyDataChanged();
            resultsLineChart.notifyDataSetChanged();
        }
        else{
            ArrayList<Entry> valuesList;
            switch(rgOption) {
                case 0:     valuesList = bmiValues;
                            break;
                case 1:     valuesList = pintValues;
                            break;
                case 2:     valuesList = weightLbsValues;
                            break;
                case 3:     valuesList = weightKgValues;
                            break;
                case 4:     valuesList = tempCelsValues;
                            break;
                case 5:     valuesList = tempFahrValues;
                            break;
                case 6:     valuesList = systolicValues;
                            break;
                case 7:     valuesList = diastolicValues;
                            break;
                case 8:     valuesList = heartRateValues;
                            break;
                default:    valuesList = bmiValues;
                            break;
            }
            resultsLds = new LineDataSet(valuesList, graphOptions[rgOption]);
            resultsLds.setDrawIcons(false);
            resultsLds.enableDashedLine(10f,0f,0f); //spaceLength is 0 so lines are not dashed
            resultsLds.setColor(Color.BLACK);
            resultsLds.setCircleColor(Color.BLACK);
            resultsLds.setLineWidth(1f);
            resultsLds.setCircleRadius(2.5f);
            resultsLds.setDrawCircleHole(false);
            resultsLds.setFormLineWidth(1f);
            resultsLds.setFormLineDashEffect(new DashPathEffect(new float[]{10f,5f},0f));
            resultsLds.setFormSize(15f);
            resultsLds.setValueTextSize(9f);
            resultsLds.enableDashedHighlightLine(10f,0f,0f);
            resultsLds.setDrawFilled(true);
            resultsLds.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return resultsLineChart.getAxisLeft().getAxisMinimum();
                }
            });
            if(Utils.getSDKInt() >= 18){
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_orange);
                resultsLds.setFillDrawable(drawable);
            } else {
                resultsLds.setFillColor(Color.BLACK);
            }
            ArrayList<ILineDataSet> resultsDataSets = new ArrayList<>();
            resultsDataSets.add(resultsLds);
            LineData resultsData = new LineData(resultsDataSets);
            resultsLineChart.setData(resultsData);
        }
        resultsLineChart.animateX(1500);
        Legend legend = resultsLineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view_results);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //Allow sharing options when users tap 'Share Your Results' button
    public void shareClick(View view){
        TextView bmiLabel = findViewById(R.id.bmiText);
        TextView pintLabel = findViewById(R.id.pintText);
        TextView weightLabel = findViewById(R.id.weightText);
        TextView btempLabel = findViewById(R.id.btempText);
        TextView systolicLabel = findViewById(R.id.systolicText);
        TextView diastolicLabel = findViewById(R.id.diastolicText);
        TextView hrateLabel = findViewById(R.id.hrateText);

        TextView bmiResult = findViewById(R.id.txtBmiResult);
        TextView pintResult = findViewById(R.id.txtPintResult);
        TextView weightResult = findViewById(R.id.txtWeightResult);
        TextView btempResult = findViewById(R.id.txtBtempResult);
        TextView systolicResult = findViewById(R.id.txtSystolicResult);
        TextView diastolicResult = findViewById(R.id.txtDiastolicResult);
        TextView hrateResult = findViewById(R.id.txtHrateResult);

        String showBmi = bmiLabel.getText().toString() + bmiResult.getText().toString() + "\n";
        String showPint = pintLabel.getText().toString() + pintResult.getText().toString() + "\n";
        String showWeight = weightLabel.getText().toString() + weightResult.getText().toString() + "\n";
        String showBtemp = btempLabel.getText().toString() + btempResult.getText().toString() + "\n";
        String showSystolic = systolicLabel.getText().toString() + systolicResult.getText().toString() + "\n";
        String showDiastolic = diastolicLabel.getText().toString() + diastolicResult.getText().toString() + "\n";
        String showHrate = hrateLabel.getText().toString() + hrateResult.getText().toString() + "\n";

        Intent iShare = new Intent(Intent.ACTION_SEND);
        iShare.setType("text/plain");
        String shareSub  = "My Overall Health Results from Medella";
        String shareBody = showBmi + showPint + showWeight + showBtemp + showSystolic + showDiastolic + showHrate;

        iShare.putExtra(Intent.EXTRA_TEXT, shareBody);
        iShare.putExtra(Intent.EXTRA_SUBJECT, shareSub);
        startActivity(Intent.createChooser(iShare, "Share your health results via"));
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
            Intent iAccountInfo = new Intent(this, AccountInfoActivity.class);
            startActivity(iAccountInfo);
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

    public void switchGraph(View view) {
        final int graphOptionIndex = ResultsGraphOptions.getResultsGraphOption(getApplicationContext());

        AlertDialog.Builder graphDialog = new AlertDialog.Builder(this);
        graphDialog.setTitle("Choose your options:");
        graphDialog.setSingleChoiceItems(R.array.graph_options_array, graphOptionIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        graphDialog.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                if(selectedPosition != graphOptionIndex) {
                    String msg = "Graph will show " + graphOptions[selectedPosition] + " results";
                    ResultsGraphOptions.setResultsGraphOption(getApplicationContext(), selectedPosition);
                    Intent iResults = new Intent(ResultsActivity.this, ResultsActivity.class);
                    startActivity(iResults);
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
                else{
                    String msg = "No changes have been applied";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
        graphDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        graphDialog.show();
    }
}
