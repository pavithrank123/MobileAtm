package com.proj.mobileAtm.common;

import android.content.SharedPreferences;

public class SharedPreferenceUtils {

    public static String getString(SharedPreferences sharedPreferences, String tag) {
        return sharedPreferences.getString(tag, null);
    }

    public static void putString(SharedPreferences sharedPreferences,String tag, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(tag, value);
        editor.apply();
    }

    public static boolean getBoolean(SharedPreferences sharedPreferences,String tag) {
        return sharedPreferences.getBoolean(tag, false);
    }

    public static void putBoolean(SharedPreferences sharedPreferences,String tag, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(tag, value);
        editor.apply();
    }

    public static int getInt(SharedPreferences sharedPreferences,String tag) {
        return sharedPreferences.getInt(tag, 0);
    }

    public static void putInt(SharedPreferences sharedPreferences,String tag, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(tag, value);
        editor.apply();
    }

}

