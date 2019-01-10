package com.medella.android.options;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MedellaOptions {
    public static void setPreferredWeightUnit(Context context, boolean isLbs){
        SharedPreferences weightPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = weightPref.edit();
        editor.putBoolean("weightPref", isLbs);
        editor.apply();
    }
    public static boolean getPreferredWeightUnit(Context context){
        SharedPreferences weightPref = PreferenceManager.getDefaultSharedPreferences(context);
        return weightPref.getBoolean("weightPref", true);
    }

    public static void setPreferredBodyTemperatureUnit(Context context, boolean isCelsius){
        SharedPreferences tempPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = tempPref.edit();
        editor.putBoolean("tempPref", isCelsius);
        editor.apply();
    }
    public static boolean getPreferredBodyTemperatureUnit(Context context){
        SharedPreferences tempPref = PreferenceManager.getDefaultSharedPreferences(context);
        return tempPref.getBoolean("tempPref", true);
    }

    public static void setDefaultWeightEnabled(Context context, boolean isEnabled){
        SharedPreferences dwPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = dwPref.edit();
        editor.putBoolean("dwEnabled", isEnabled);
        editor.apply();
    }
    public static boolean getDefaultWeightEnabled(Context context){
        SharedPreferences dwPref = PreferenceManager.getDefaultSharedPreferences(context);
        return dwPref.getBoolean("dwEnabled", false);
    }
    public static void setDefaultWeight(Context context, float dWeight){
        SharedPreferences dwPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = dwPref.edit();
        editor.putFloat("defaultWeight", dWeight);
        editor.apply();
    }
    public static float getDefaultWeight(Context context){
        SharedPreferences dwPref = PreferenceManager.getDefaultSharedPreferences(context);
        return dwPref.getFloat("defaultWeight", 0);
    }
}
