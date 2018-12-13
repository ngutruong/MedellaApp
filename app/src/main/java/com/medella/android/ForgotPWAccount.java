package com.medella.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ForgotPWAccount {

    public static void setEmail(Context context, String email){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = loginPref.edit();
        editor.putString("fpEmail", email);
        editor.apply();
    }
    public static String getEmail(Context context){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        return loginPref.getString("fpEmail", null);
    }

    public static void setSecQues(Context context, String secQues){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = loginPref.edit();
        editor.putString("fpSecQues", secQues);
        editor.apply();
    }
    public static String getSecQues(Context context){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        return loginPref.getString("fpSecQues", null);
    }

    public static void setSecQuesWhich(Context context, String secQuesWhich){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = loginPref.edit();
        editor.putString("fpSecQuesWhich", secQuesWhich);
        editor.apply();
    }
    public static String getSecQuesWhich(Context context){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        return loginPref.getString("fpSecQuesWhich", null);
    }

    public static void setSecAnsWhich(Context context, String secAnsWhich){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = loginPref.edit();
        editor.putString("fpSecAnsWhich", secAnsWhich);
        editor.apply();
    }
    public static String getSecAnsWhich(Context context) {
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        return loginPref.getString("fpSecAnsWhich", null);
    }
}
