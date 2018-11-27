package com.medella.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ResultsGraphOptions {
    public static void setResultsGraphOption(Context context, int gOption){
        SharedPreferences graphPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = graphPref.edit();
        editor.putInt("graphPref", gOption);
        editor.apply();
    }
    public static int getResultsGraphOption(Context context){
        SharedPreferences graphPref = PreferenceManager.getDefaultSharedPreferences(context);
        return graphPref.getInt("graphPref", 0);
    }
}
