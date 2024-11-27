package com.example.aogata.antiphishingdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by aogata on 16. 1. 5.
 */
public class MySettings {

    // DEMO Mode definition
    public static final int DEMO1=1;
    public static final int DEMO2=2;
    public static final int DEMO3=3;
    public static final int DEMO4=4;

    // Anti-Phishing Engine definition
    public static final int NO_ENGINE=0;
    public static final int GOOGLE_SAFE_BROWSING=1;
    public static final int MCAFEE_SITE_ADVISER=2;

    private static final String DEMO_MODE_KEY="DEMO_MODE_SELECTION";
    private static final String ANTI_PHISHING_ENGINE_KEY="ANTI_PHISHING_ENGINE";

    Context mContext;

    MySettings(Context context) {
        mContext = context;
    }

    public int getDemoMode() {
        int value;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        value = pref.getInt(DEMO_MODE_KEY, DEMO1);
        return value;
    }

    public void setDemoMode(int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(DEMO_MODE_KEY, value);
        editor.commit();
    }

    public int getAntiPhishingEngine() {
        int value;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        value = pref.getInt(ANTI_PHISHING_ENGINE_KEY, GOOGLE_SAFE_BROWSING);
        return value;
    }

    public void setAntiPhishingEngine(int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(ANTI_PHISHING_ENGINE_KEY, value);
        editor.commit();
    }
}
