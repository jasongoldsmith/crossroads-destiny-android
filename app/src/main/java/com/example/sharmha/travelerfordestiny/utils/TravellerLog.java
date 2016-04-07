package com.example.sharmha.travelerfordestiny.utils;

import android.util.Log;

import com.example.sharmha.travellerdestiny.BuildConfig;

/**
 * Created by sharmha on 3/3/16.
 */
public class TravellerLog {

    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, message);
        }
    }

    public static void e(String tag, String message) {
        Log.e(tag, message);
    }
}
