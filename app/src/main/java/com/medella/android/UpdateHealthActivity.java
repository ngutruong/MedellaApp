package com.medella.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.medella.android.options.MedellaOptions;

public class UpdateHealthActivity {
    /**
     * If update process
     */
    public static boolean isUpdate(Context context){
        SharedPreferences updatePref = PreferenceManager.getDefaultSharedPreferences(context);
        return updatePref.getBoolean("isUpdate", false);
    }
    public static void processUpdate(Context context, boolean update){
        SharedPreferences updatePref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = updatePref.edit();
        editor.putBoolean("isUpdate", update);
        editor.apply();
    }

    /**
     * Activity ID
     */
    public static String getActivityId(Context context){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        return healthPref.getString("updateId", null);
    }
    public static void setActivityId(Context context, String activityId){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = healthPref.edit();
        editor.putString("updateId", activityId);
        editor.apply();
    }

    /**
     * Title (update)
     */
    public static void setTitle(Context context, String title){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = healthPref.edit();
        editor.putString("updateTitle", title);
        editor.apply();
    }
    public static String getTitle(Context context){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        return healthPref.getString("updateTitle", null);
    }

    /**
     * Weight (update)
     */
    public static void setWeightLbs(Context context, float weight){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = healthPref.edit();
        editor.putFloat("updateWeightLbs", weight);
        editor.apply();
    }
    public static float getWeightLbs(Context context){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        return healthPref.getFloat("updateWeightLbs", 0);
    }
    public static void setWeightKg(Context context, float weight){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = healthPref.edit();
        editor.putFloat("updateWeightKg", weight);
        editor.apply();
    }
    public static float getWeightKg(Context context){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        return healthPref.getFloat("updateWeightKg", 0);
    }

    /**
     * Weight Unit (update)
     * NOT SURE IF THIS IS NECESSARY
     */
    public static void setWeightUnit(Context context, int unit){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = healthPref.edit();
        editor.putInt("updateWeightUnit", unit);
        editor.apply();
    }
    public static int getWeightUnit(Context context){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isLbs = MedellaOptions.getPreferredWeightUnit(context);
        int defaultUnit;
        if(isLbs)
            defaultUnit = 0;
        else
            defaultUnit = 1;
        return healthPref.getInt("updateWeightUnit", defaultUnit);
    }

    /**
     * Pain Intensity (update)
     */
    public static void setPainIntensity(Context context, float pint){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = healthPref.edit();
        editor.putFloat("updatePint", pint);
        editor.apply();
    }
    public static float getPainIntensity(Context context){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        return healthPref.getFloat("updatePint", 0);
    }

    /**
     * Medication Brand (update)
     */
    public static void setMedName(Context context, String med){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = healthPref.edit();
        editor.putString("updateMedName", med);
        editor.apply();
    }
    public static String getMedName(Context context){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        return healthPref.getString("updateMedName", null);
    }

    /**
     * Medication dosage (update)
     */
    public static void setMedDosage(Context context, String medDosage){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = healthPref.edit();
        editor.putString("updateMedDosage", medDosage);
        editor.apply();
    }
    public static String getMedDosage(Context context){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        return healthPref.getString("updateMedDosage", null);
    }
    /*public static void setMedDosage(Context context, float medDosage){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = healthPref.edit();
        editor.putFloat("updateMedDosage", medDosage);
        editor.apply();
    }
    public static float getMedDosage(Context context){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        return healthPref.getFloat("updateMedDosage", 0);
    }*/

    /**
     * Medication unit (update)
     * NOT SURE IF THIS IS NECESSARY
     */
    public static void setMedUnit(Context context, int medUnit){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = healthPref.edit();
        editor.putInt("updateDoseUnit", medUnit);
        editor.apply();
    }
    public static int getMedUnit(Context context){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        return healthPref.getInt("updateDoseUnit", 0);
    }

    /**
     * Temperature (update)
     */
    public static void setTemperatureCels(Context context, float cels){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = healthPref.edit();
        editor.putFloat("updateTempCels", cels);
        editor.apply();
    }
    public static float getTemperatureCels(Context context){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        return healthPref.getFloat("updateTempCels", 0);
    }
    public static void setTemperatureFahr(Context context, float fahr){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = healthPref.edit();
        editor.putFloat("updateTempFahr", fahr);
        editor.apply();
    }
    public static float getTemperatureFahr(Context context){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        return healthPref.getFloat("updateTempFahr", 0);
    }

    /**
     * Temperature unit (update)
     * NOT SURE IF THIS IS NECESSARY
     */
    public static void setTempUnit(Context context, int tUnit){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = healthPref.edit();
        editor.putInt("updateTempUnit", tUnit);
        editor.apply();
    }
    public static int getTempUnit(Context context){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isCelsius = MedellaOptions.getPreferredBodyTemperatureUnit(context);
        int defaultUnit;
        if(isCelsius)
            defaultUnit = 0;
        else
            defaultUnit = 1;
        return healthPref.getInt("updateTempUnit", defaultUnit);
    }

    /**
     * Blood Pressure (update)
     *
     * Systolic
     * Diastolic
     */
    public static void setSystolic(Context context, float systolic){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = healthPref.edit();
        editor.putFloat("updateSystolic", systolic);
        editor.apply();
    }
    public static float getSystolic(Context context){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        return healthPref.getFloat("updateSystolic", 0);
    }
    public static void setDiastolic(Context context, float diastolic){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = healthPref.edit();
        editor.putFloat("updateDiastolic", diastolic);
        editor.apply();
    }
    public static float getDiastolic(Context context){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        return healthPref.getFloat("updateDiastolic", 0);
    }

    /**
     * Heart Rate (update)
     */
    public static void setHeartRate(Context context, float heartRate){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = healthPref.edit();
        editor.putFloat("updateHeartRate", heartRate);
        editor.apply();
    }
    public static float getHeartRate(Context context){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        return healthPref.getFloat("updateHeartRate", 0);
    }

    /**
     * Location (update)
     */
    public static void setLocation(Context context, String location){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = healthPref.edit();
        editor.putString("updateLocation", location);
        editor.apply();
    }
    public static String getLocation(Context context){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        return healthPref.getString("updateLocation", null);
    }

    /**
     * Description (update)
     */
    public static void setDescription(Context context, String desc){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = healthPref.edit();
        editor.putString("updateDescription", desc);
        editor.apply();
    }
    public static String getDescription(Context context){
        SharedPreferences healthPref = PreferenceManager.getDefaultSharedPreferences(context);
        return healthPref.getString("updateDescription", null);
    }
}
