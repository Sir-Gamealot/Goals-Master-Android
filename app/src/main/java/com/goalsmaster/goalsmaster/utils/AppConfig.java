package com.goalsmaster.goalsmaster.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Tudor Tihan on 4/15/2017.
 */

public class AppConfig {

    /**
     *  Returns the value of the boolean found in the store, or the provided value, if the target object is not found.
     */
    public static boolean getBoolean(Context context, String key, boolean value) {
        return getPrefs(context).getBoolean(key, value);
    }

    private static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        getEdit(context).putBoolean(key, value).apply();
    }

    private static SharedPreferences.Editor getEdit(Context context) {
        return getPrefs(context).edit();
    }
}
