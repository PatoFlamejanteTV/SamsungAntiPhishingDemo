package com.example.aogata.antiphishingdemo;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by aogata on 16. 2. 4.
 */
public class DetectionListActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DetectionListActivity";

    private DetectionDBOpenHelper mDBHelper;
    private SQLiteDatabase mDB;
    private ArrayList<DetectedURL> mDetectionList;
    private ListView mDetectionListView;
    private DetectionListAdapter mAdapter;
    private DetectionListControl mListControl;


    private Menu mMainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection_list);

        Log.d(TAG, "Checkpoint #1");
        mListControl = new DetectionListControl(this);

        Log.d(TAG, "Checkpoint #2");
        mDetectionList = new ArrayList(mListControl.getData());

        Log.d(TAG, "Checkpoint #3");
        mAdapter = new DetectionListAdapter(this, 0, mDetectionList);

        Log.d(TAG, "Checkpoint #4");
        mDetectionListView = (ListView)findViewById(R.id.detection_list);

        Log.d(TAG, "Checkpoint #5");
        mDetectionListView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "Button is pressed.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu()");
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detection_list_menu, menu);
        mMainMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected()");
        boolean ret = true;
        switch (item.getItemId()) {
            case R.id.clear_all:
                Log.d(TAG, "CLEAR ALL Button is pressed");
                mListControl.deleteAll();
                finish();
                break;
            default:
                break;
        }
        return ret;
    }

}
