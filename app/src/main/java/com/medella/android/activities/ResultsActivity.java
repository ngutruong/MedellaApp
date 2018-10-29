package com.medella.android.activities;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.medella.android.R;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private LineChart bmiLineChart;
    private float bmiAverage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

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
        float bmiOne = (float) 20.6;
        float bmiTwo = (float) 28.6;
        float bmiThree = (float) 21.1;
        bmiValues.add(new Entry(0, bmiOne));
        bmiValues.add(new Entry(1, bmiTwo));
        bmiValues.add(new Entry(2, bmiThree));
        bmiAverage+=bmiOne; //Testing
        bmiAverage+=bmiTwo; //Testing
        bmiAverage+=bmiThree; //Testing
        bmiAverage = bmiAverage/3; //Testing

        TextView tvBmiResult = (TextView)findViewById(R.id.txtBmiResult); //Testing
        tvBmiResult.setText(String.valueOf(bmiAverage)); //Testing
        if(bmiAverage > 18 && bmiAverage <= 25){
            tvBmiResult.setTextColor(Color.GREEN); //Testing - see if color for TextView changes
        }
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

        }else if (id == R.id.nav_amLogout) {
            //Logout is not available at this moment
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
