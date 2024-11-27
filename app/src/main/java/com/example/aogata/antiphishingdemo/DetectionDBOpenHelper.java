package com.example.aogata.antiphishingdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by aogata on 16. 1. 25.
 */
public class DetectionDBOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "DetectionDBOpenHelper";

    public static final String DB_NAME = "DetectionDB";
    public static final String TABLE_NAME = "detected_url";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_ENGINE = "engine";
    public static final String COLUMN_DATE = "date";


    public DetectionDBOpenHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate()");

        String sql = "";
        sql += "create table " + TABLE_NAME + "(";
        sql += COLUMN_ID + " integer primary key autoincrement, ";
        sql += COLUMN_URL + " text, ";
        sql += COLUMN_ENGINE + " text, ";
        sql += COLUMN_DATE + " integer)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) {
        Log.d(TAG, "onUpgrade()");
    }
}
