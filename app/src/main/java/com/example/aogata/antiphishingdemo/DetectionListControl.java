package com.example.aogata.antiphishingdemo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by aogata on 16. 1. 28.
 */
public class DetectionListControl {
    private static final String TAG = "DetectionListControl";

    private DetectionDBOpenHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DetectionListControl(Context context) {
        Log.d(TAG, "DetectionListControl()");
        mDBHelper = new DetectionDBOpenHelper(context);
        mDB = mDBHelper.getReadableDatabase();
    }

    public ArrayList<DetectedURL> getData() {
        Log.d(TAG, "getPagingData()");

        ArrayList<DetectedURL> list = new ArrayList<DetectedURL>();

        Cursor c = mDB.query(
                DetectionDBOpenHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                DetectionDBOpenHelper.COLUMN_DATE + " DESC"
        );

        while(c.moveToNext()) {
            DetectedURL detection = new DetectedURL();

            detection.setId(c.getInt(c.getColumnIndex(DetectionDBOpenHelper.COLUMN_ID)));
            detection.setUrl(c.getString(c.getColumnIndex(DetectionDBOpenHelper.COLUMN_URL)));
            detection.setEngine(c.getString(c.getColumnIndex(DetectionDBOpenHelper.COLUMN_ENGINE)));
            detection.setTime(c.getLong(c.getColumnIndex(DetectionDBOpenHelper.COLUMN_DATE)));

            list.add(detection);
        }

        return list;
    }

    public ArrayList<DetectedURL> getPagingData(int offset, int limit) {
        Log.d(TAG, "getPagingData()");

        ArrayList<DetectedURL> list = new ArrayList<DetectedURL>();

        Cursor c = mDB.query(
                DetectionDBOpenHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                Integer.toString(offset) + "," + Integer.toString(limit)
        );

        while(c.moveToNext()) {
            DetectedURL detection = new DetectedURL();

            detection.setId(c.getInt(c.getColumnIndex(DetectionDBOpenHelper.COLUMN_ID)));
            detection.setUrl(c.getString(c.getColumnIndex(DetectionDBOpenHelper.COLUMN_URL)));
            detection.setEngine(c.getString(c.getColumnIndex(DetectionDBOpenHelper.COLUMN_ENGINE)));
            detection.setTime(c.getInt(c.getColumnIndex(DetectionDBOpenHelper.COLUMN_DATE)));

            list.add(detection);
        }

        return list;
    }

    public int getCount() {
        Log.d(TAG, "getCount()");

        Cursor c = mDB.query(
                DetectionDBOpenHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        return c.getCount();
    }

    public void deleteAll() {
        mDB.delete(DetectionDBOpenHelper.TABLE_NAME, null, null);
    }

}
