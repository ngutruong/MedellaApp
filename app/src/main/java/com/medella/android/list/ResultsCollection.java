package com.medella.android.list;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class ResultsCollection {
    /**
     * BMI
     * 
     * Collect mean of BMI
     * Collect difference between 2 latest BMI results
     */
    public static void setBmiAverage(Context context, float bmiAvg){
        SharedPreferences bmiPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = bmiPref.edit();
        editor.putFloat("bmiResults", bmiAvg);
        editor.apply();
    }
    public static float getBmiAverage(Context context){
        SharedPreferences bmiPref = PreferenceManager.getDefaultSharedPreferences(context);
        return bmiPref.getFloat("bmiResults", 0);
    }
    public static void setBmiDifference(Context context, float bmiDiff){
        SharedPreferences bmiPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = bmiPref.edit();
        editor.putFloat("bmiAverage", bmiDiff);
        editor.apply();
    }
    public static float getBmiDifference(Context context){
        SharedPreferences bmiPref = PreferenceManager.getDefaultSharedPreferences(context);
        return bmiPref.getFloat("bmiAverage", 0);
    }
    private static List<Float> bmiList;
    public static List<Float> getBmiList(){
        return bmiList;
    }
    public static void setBmiList(List<Float> floatList){
        bmiList = floatList;
    }

    /**
     * Pain Intensity
     * 
     * Collect mean of Pain Intensity
     * Collect difference between 2 latest Pain Intensity results
     */
    public static void setPainIntensityAverage(Context context, float pintAvg){
        SharedPreferences pintPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pintPref.edit();
        editor.putFloat("pintResults", pintAvg);
        editor.apply();
    }
    public static float getPainIntensityAverage(Context context){
        SharedPreferences pintPref = PreferenceManager.getDefaultSharedPreferences(context);
        return pintPref.getFloat("pintResults", 0);
    }
    public static void setPainIntensityDifference(Context context, float pintDiff){
        SharedPreferences pintPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pintPref.edit();
        editor.putFloat("pintDiff", pintDiff);
        editor.apply();
    }
    public static float getPainIntensityDifference(Context context){
        SharedPreferences pintPref = PreferenceManager.getDefaultSharedPreferences(context);
        return pintPref.getFloat("pintDiff", 0);
    }
    private static List<Float> pintList;
    public static List<Float> getPintList(){
        return pintList;
    }
    public static void setPintList(List<Float> pList){
        pintList = pList;
    }

    /**
     * Weight (Lbs. and Kg.)
     * 
     * Collect mean of weight
     * Collect difference between 2 latest weights
     */
    public static void setWeightLbsAverage(Context context, float weightLbsAvg){
        SharedPreferences weightPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = weightPref.edit();
        editor.putFloat("weightLbsResults", weightLbsAvg);
        editor.apply();
    }
    public static float getWeightLbsAverage(Context context){
        SharedPreferences weightPref = PreferenceManager.getDefaultSharedPreferences(context);
        return weightPref.getFloat("weightLbsResults", 0);
    }
    public static void setWeightLbsDifference(Context context, float weightLbsDiff){
        SharedPreferences weightPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = weightPref.edit();
        editor.putFloat("weightLbsDiff", weightLbsDiff);
        editor.apply();
    }
    public static float getWeightLbsDifference(Context context){
        SharedPreferences weightPref = PreferenceManager.getDefaultSharedPreferences(context);
        return weightPref.getFloat("weightLbsDiff", 0);
    }
    public static void setWeightKgAverage(Context context, float weightKgAvg){
        SharedPreferences weightPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = weightPref.edit();
        editor.putFloat("weightKgResults", weightKgAvg);
        editor.apply();
    }
    public static float getWeightKgAverage(Context context){
        SharedPreferences weightPref = PreferenceManager.getDefaultSharedPreferences(context);
        return weightPref.getFloat("weightKgResults", 0);
    }
    public static void setWeightKgDifference(Context context, float weightKgDiff){
        SharedPreferences weightPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = weightPref.edit();
        editor.putFloat("weightKgDiff", weightKgDiff);
        editor.apply();
    }
    public static float getWeightKgDifference(Context context){
        SharedPreferences weightPref = PreferenceManager.getDefaultSharedPreferences(context);
        return weightPref.getFloat("weightKgDiff", 0);
    }
    private static List<Float> weightLbsList;
    private static List<Float> weightKgList;
    public static List<Float> getWeightLbsList(){ return weightLbsList; }
    public static void setWeightLbsList(List<Float> lbsList){ weightLbsList = lbsList; }
    public static List<Float> getWeightKgList(){ return weightKgList; }
    public static void setWeightKgList(List<Float> kgList){ weightKgList = kgList; }


    /**
     * Temperature (Celsius and Fahrenheit)
     *
     * Collect mean of temperatures
     * Collect differences between 2 latest temperatures
     */
    public static void setTemperatureCelsiusAverage(Context context, float tempCelsAvg){
        SharedPreferences tempPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = tempPref.edit();
        editor.putFloat("tempCelsResults", tempCelsAvg);
        editor.apply();
    }
    public static float getTemperatureCelsiusAverage(Context context){
        SharedPreferences tempPref = PreferenceManager.getDefaultSharedPreferences(context);
        return tempPref.getFloat("tempCelsResults", 0);
    }
    public static void setTemperatureCelsiusDifference(Context context, float tempCelsDiff){
        SharedPreferences tempPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = tempPref.edit();
        editor.putFloat("tempCelsDiff", tempCelsDiff);
        editor.apply();
    }
    public static float getTemperatureCelsiusDifference(Context context) {
        SharedPreferences tempPref = PreferenceManager.getDefaultSharedPreferences(context);
        return tempPref.getFloat("tempCelsDiff", 0);
    }
    public static void setTemperatureFahrAverage(Context context, float tempFahrAvg){
        SharedPreferences tempPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = tempPref.edit();
        editor.putFloat("tempFahrResults", tempFahrAvg);
        editor.apply();
    }
    public static float getTemperatureFahrAverage(Context context){
        SharedPreferences tempPref = PreferenceManager.getDefaultSharedPreferences(context);
        return tempPref.getFloat("tempFahrResults", 0);
    }
    public static void setTemperatureFahrDifference(Context context, float tempFahrDiff){
        SharedPreferences tempPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = tempPref.edit();
        editor.putFloat("tempFahrDiff", tempFahrDiff);
        editor.apply();
    }
    public static float getTemperatureFahrDifference(Context context) {
        SharedPreferences tempPref = PreferenceManager.getDefaultSharedPreferences(context);
        return tempPref.getFloat("tempFahrDiff", 0);
    }
    private static List<Float> tempCelsiusList;
    private static List<Float> tempFahrenheitList;
    public static List<Float> getTempCelsiusList(){ return tempCelsiusList; }
    public static void setTempCelsiusList(List<Float> celsiusList){ tempCelsiusList = celsiusList; }
    public static List<Float> getTempFahrenheitList(){ return tempFahrenheitList; }
    public static void setTempFahrenheitList(List<Float> fahrenheitList){ tempFahrenheitList = fahrenheitList; }

    /**
     * Blood Pressure: SYSTOLIC
     * 
     * Collect mean of systolic
     * Collect difference between 2 latest systolic results
     */
    public static void setSystolicAverage(Context context, float sysAvg){
        SharedPreferences sysPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sysPref.edit();
        editor.putFloat("sysResults", sysAvg);
        editor.apply();
    }
    public static float getSystolicAverage(Context context){
        SharedPreferences sysPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sysPref.getFloat("sysResults", 0);
    }
    public static void setSystolicDifference(Context context, float sysDiff){
        SharedPreferences sysPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sysPref.edit();
        editor.putFloat("sysDiff", sysDiff);
        editor.apply();
    }
    public static float getSystolicDifference(Context context){
        SharedPreferences sysPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sysPref.getFloat("sysDiff", 0);
    }
    private static List<Float> systolicList;
    public static List<Float> getSystolicList(){ return systolicList; }
    public static void setSystolicList(List<Float> sysList){
        systolicList = sysList;
    }

    /**
     * Blood Pressure: DIASTOLIC
     *
     * Collect mean of diastolic
     * Collect difference between 2 latest diastolic results
     */
    public static void setDiastolicAverage(Context context, float diasAvg){
        SharedPreferences diasPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = diasPref.edit();
        editor.putFloat("diasResults", diasAvg);
        editor.apply();
    }
    public static float getDiastolicAverage(Context context){
        SharedPreferences diasPref = PreferenceManager.getDefaultSharedPreferences(context);
        return diasPref.getFloat("diasResults", 0);
    }
    public static void setDiastolicDifference(Context context, float diasDiff){
        SharedPreferences diasPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = diasPref.edit();
        editor.putFloat("diasDiff", diasDiff);
        editor.apply();
    }
    public static float getDiastolicDifference(Context context){
        SharedPreferences diasPref = PreferenceManager.getDefaultSharedPreferences(context);
        return diasPref.getFloat("diasDiff", 0);
    }
    private static List<Float> diastolicList;
    public static List<Float> getDiastolicList(){ return diastolicList; }
    public static void setDiastolicList(List<Float> diasList){
        if(diasList.size() > 0) {
            diastolicList = diasList;
        }
        else{
            List<Float> tempList = new ArrayList<>();
            tempList.add(Float.valueOf(1));
            diastolicList = tempList;
        }
    }

    /**
     * Heart Rate
     * 
     * Collect mean of heart rates
     * Collect difference between 2 latest heart rates
     */
    public static void setHeartRateAverage(Context context, float hRateAvg){
        SharedPreferences hRatePref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = hRatePref.edit();
        editor.putFloat("hRateResults", hRateAvg);
        editor.apply();
    }
    public static float getHeartRateAverage(Context context){
        SharedPreferences hRatePref = PreferenceManager.getDefaultSharedPreferences(context);
        return hRatePref.getFloat("hRateResults", 0);
    }
    public static void setHeartRateDifference(Context context, float hRateDiff){
        SharedPreferences hRatePref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = hRatePref.edit();
        editor.putFloat("hRateDiff", hRateDiff);
        editor.apply();
    }
    public static float getHeartRateDifference(Context context){
        SharedPreferences hRatePref = PreferenceManager.getDefaultSharedPreferences(context);
        return hRatePref.getFloat("hRateDiff", 0);
    }
    private static List<Float> heartRateList;
    public static List<Float> getHeartRateList(){ return heartRateList; }
    public static void setHeartRateList(List<Float> hRateList){ heartRateList = hRateList; }
}
