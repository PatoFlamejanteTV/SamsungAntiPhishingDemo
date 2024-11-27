package com.example.aogata.antiphishingdemo;

import android.text.format.DateFormat;
import android.util.Log;

/**
 * Created by aogata on 16. 1. 27.
 */
public class DetectedURL {
    private static final String TAG = "DetectedURL";

    protected int id;
    protected String url;
    protected String engine;
    protected long time;

    public void setId(int id) {
        Log.d(TAG, "setId()");
        this.id = id;
    }

    public void setUrl(String url) {
        Log.d(TAG, "setUrl()");
        this.url = url;
    }

    public void setEngine(String engine) {
        Log.d(TAG, "setEngine()");
        this.engine = engine;
    }

    public void setTime(long time) {
        Log.d(TAG, "setTime()");
        this.time = time;
    }

    public int getId() {
        Log.d(TAG, "getId()");
        return id;
    }

    public String getUrl() {
        Log.d(TAG, "getUrl()");
        return url;
    }

    public String getEngine() {
        Log.d(TAG, "getEngine()");
        return engine;
    }

    public long getTime() {
        Log.d(TAG, "getTime()");
        return time;
    }

    public String getDate() {
        Log.d(TAG, "getDate()");
        String date = DateFormat.format("yyyy/MM/dd kk:mm:ss", time).toString();
        date += "(" + engine + ")";

        return date;
    }
}
