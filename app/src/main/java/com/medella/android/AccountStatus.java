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

    public static float getHeightM(Context context){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        return loginPref.getFloat("profileHeightM", 0);
    }
    public static void setHeightM(Context context, float m){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = loginPref.edit();
        editor.putFloat("profileHeightM", m);
        editor.apply();
    }

    public static float getHeightCm(Context context){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        return loginPref.getFloat("profileHeightCm", 0);
    }
    public static void setHeightCm(Context context, float cm){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = loginPref.edit();
        editor.putFloat("profileHeightCm", cm);
        editor.apply();
    }

    public static float getHeightIn(Context context){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        return loginPref.getFloat("profileHeightIn", 0);
    }
    public static void setHeightIn(Context context, float in){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = loginPref.edit();
        editor.putFloat("profileHeightIn", in);
        editor.apply();
    }

    public static float getHeight2Ft(Context context){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        return loginPref.getFloat("profileHeight2Ft", 0);
    }
    public static void setHeight2Ft(Context context, float ft){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = loginPref.edit();
        editor.putFloat("profileHeight2Ft", ft);
        editor.apply();
    }

    public static float getHeight2In(Context context){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        return loginPref.getFloat("profileHeight2In", 0);
    }
    public static void setHeight2In(Context context, float in){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = loginPref.edit();
        editor.putFloat("profileHeight2In", in);
        editor.apply();
    }

    public static void setProfileBirthdate(Context context, String birthdate){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = loginPref.edit();
        editor.putString("loginBirthdate", birthdate);
        editor.apply();
    }
    public static String getProfileBirthdate(Context context){
        SharedPreferences loginPref = PreferenceManager.getDefaultSharedPreferences(context);
        return loginPref.getString("loginBirthdate", null);
    }
}
