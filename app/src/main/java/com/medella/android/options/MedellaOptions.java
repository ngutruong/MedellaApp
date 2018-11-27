package com.medella.android;

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
}
