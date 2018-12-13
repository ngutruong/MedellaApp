package com.medella.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AccountStatus {
    public static void setLogin(Context context, boolean login){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = loginPref.edit();
        editor.putBoolean("loginStatus", login);
        editor.apply();
    }
    public static boolean isLoggedIn(Context context){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        return loginPref.getBoolean("loginStatus", false);
    }

    public static void setProfileName(Context context, String name){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = loginPref.edit();
        editor.putString("loginName", name);
        editor.apply();
    }
    public static String getProfileName(Context context){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        return loginPref.getString("loginName", null);
    }

    public static void setProfileId(Context context, String id){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = loginPref.edit();
        editor.putString("loginId", id);
        editor.apply();
    }
    public static String getProfileId(Context context){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        return loginPref.getString("loginId", null);
    }

    public static void setEmail(Context context, String email){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = loginPref.edit();
        editor.putString("loginEmail", email);
        editor.apply();
    }
    public static String getEmail(Context context){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        return loginPref.getString("loginEmail", null);
    }

    public static void setHeight(Context context, float m){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = loginPref.edit();
        editor.putFloat("profileHeight", m);
        editor.apply();
    }
    public static float getHeight(Context context){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        return loginPref.getFloat("profileHeight", 0);
    }

}
