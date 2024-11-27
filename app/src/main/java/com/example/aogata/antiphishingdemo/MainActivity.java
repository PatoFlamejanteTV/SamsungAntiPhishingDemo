package com.example.aogata.antiphishingdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "MainActivity";

    private static final String ITEM1_MAIN="Demo mode";
    private static final String ITEM2_MAIN="Anti-Phishing engine";

    private static final String ENGINE_SELECTION_TITLE="Engine selection";
    private static final String NO_ENGINE="None";
    private static final String GOOGLE_ENGINE="Google SafeBrowsing";
    private static final String MCAFEE_ENGINE="McAfee SiteAdviser";

    private static final String DEMO_SELECTION_TITLE="Demo mode selection";
    private static final String DEMO_MODE_1="Demo1";
    private static final String DEMO_MODE_2="Demo2";
    private static final String DEMO_MODE_3="Demo3";
    private static final String DEMO_MODE_4="Demo4";

    private static final String[] mDemoItems = {
        DEMO_MODE_1,
        DEMO_MODE_2,
        DEMO_MODE_3,
        DEMO_MODE_4
    };

    private static final String[] mEngineItems = {
        NO_ENGINE,
        GOOGLE_ENGINE,
        MCAFEE_ENGINE
    };

    private MySettings mSetting;
    private Button mShowLogsBtn;

    private List<Map<String, Object>> mList;
    private SimpleAdapter mAdapter;
    private ListView mListView;

    private String mItem1Main;
    private String mItem1Sub;
    private String mItem2Main;
    private String mItem2Sub;

    private HttpServer mHttpServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSetting = new MySettings(getApplicationContext());
        mItem1Main = ITEM1_MAIN;
        switch(mSetting.getDemoMode()) {
            case MySettings.DEMO1:
                mItem1Sub = DEMO_MODE_1;
                break;
            case MySettings.DEMO2:
                mItem1Sub = DEMO_MODE_2;
                break;
            case MySettings.DEMO3:
                mItem1Sub = DEMO_MODE_3;
                break;
            default:
                mItem1Sub = DEMO_MODE_4;
                break;
        }

        mItem2Main = ITEM2_MAIN;
        switch(mSetting.getAntiPhishingEngine()) {
            case MySettings.NO_ENGINE:
                mItem2Sub = NO_ENGINE;
                break;
            case MySettings.GOOGLE_SAFE_BROWSING:
                mItem2Sub = GOOGLE_ENGINE;
                break;
            case MySettings.MCAFEE_SITE_ADVISER:
                mItem2Sub = MCAFEE_ENGINE;
                break;
            default:
                mItem2Sub = GOOGLE_ENGINE;
                break;
        }

        mList = new ArrayList<Map<String, Object>>();
        createList();
        mAdapter = new SimpleAdapter(
                this,
                mList,
                R.layout.main_list_item,
                new String[] {"main", "sub"},
                new int[] {R.id.Title, R.id.Selection}
        );

        mListView = (ListView) findViewById(R.id.mainListView);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mShowLogsBtn = (Button) findViewById(R.id.showLogsBtn);
        mShowLogsBtn.setOnClickListener(this);

        mHttpServer = new HttpServer(this);
        mHttpServer.execute();
    }

    private void createList() {
        Map data1 = new HashMap();
        Map data2 = new HashMap();

        data1.put("main", mItem1Main);
        data1.put("sub", mItem1Sub);
        mList.add(data1);

        data2.put("main", mItem2Main);
        data2.put("sub", mItem2Sub);
        mList.add(data2);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        Log.d(TAG, "onItemClick()");
        if(pos == 0) {
            showDemoSelectionDialog();
        } else if(pos == 1) {
            showEngineSelectionDialog();
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick()");

        int id = v.getId();
        switch (id) {
            case R.id.showLogsBtn:
                showLogsBtnClicked();
                break;
            default:
                break;
        }
    }

    private void showLogsBtnClicked() {
        Log.d(TAG, "showLogsBtnClicked()");

        Intent intent = new Intent();
        intent.setClassName("com.example.aogata.antiphishingdemo", "com.example.aogata.antiphishingdemo.DetectionListActivity");
        startActivity(intent);
    }

    private void showDemoSelectionDialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle(DEMO_SELECTION_TITLE);
        dlg.setCancelable(false);
        dlg.setItems(mDemoItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int no) {
                switch (no) {
                    case 0:
                        mSetting.setDemoMode(MySettings.DEMO1);
                        mList.get(0).put("sub", DEMO_MODE_1);
                        break;
                    case 1:
                        mSetting.setDemoMode(MySettings.DEMO2);
                        mList.get(0).put("sub", DEMO_MODE_2);
                        break;
                    case 2:
                        mSetting.setDemoMode(MySettings.DEMO3);
                        mList.get(0).put("sub", DEMO_MODE_3);
                        break;
                    case 3:
                        mSetting.setDemoMode(MySettings.DEMO4);
                        mList.get(0).put("sub", DEMO_MODE_4);
                        break;
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        dlg.show();
    }

    private void showEngineSelectionDialog() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle(ENGINE_SELECTION_TITLE);
        dlg.setCancelable(false);
        dlg.setItems(mEngineItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int no) {
                switch (no) {
                    case 0:
                        mSetting.setAntiPhishingEngine(MySettings.NO_ENGINE);
                        mList.get(1).put("sub", NO_ENGINE);
                        break;
                    case 1:
                        mSetting.setAntiPhishingEngine(MySettings.GOOGLE_SAFE_BROWSING);
                        mList.get(1).put("sub", GOOGLE_ENGINE);
                        break;
                    case 2:
                        mSetting.setAntiPhishingEngine(MySettings.MCAFEE_SITE_ADVISER);
                        mList.get(1).put("sub", MCAFEE_ENGINE);
                        break;
                }
                mAdapter.notifyDataSetChanged();
            }
        });

        dlg.show();
    }
}

