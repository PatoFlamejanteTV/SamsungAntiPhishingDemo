package com.example.aogata.antiphishingdemo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.samsung.android.lib.seatbelt.url.UrlCheckCallback;
import com.samsung.android.lib.seatbelt.url.UrlCheckResult;
import com.samsung.android.lib.seatbelt.url.UrlEngine;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * Created by aogata on 16. 1. 14.
 */
public class AntiPhishingService extends IntentService {

    private static final String TAG = "AntiPhishingService";
    private static final String DEST_PACKAGE = "dest_package";
    private static final String DEST_CLASS = "dest_class";
    private static final String SCAN_URL = "scan_url";
    private static final String BLOCKING_URL = "http://localhost:50080/blocking.html?url=";
    private static final String SCANNING_URL = "http://localhost:50080/scanning.html";
//    private static final String BLOCKING_URL = "http://www.loukoum.jp/phish.html";
//    private static final String SCANNING_URL = "http://www.loukoum.jp/scan.html";

    // For google safe browsing
    private static final String DEFAULT_API_URL_FORMAT = "https://sb-ssl.google.com/safebrowsing/api/lookup?client=%s&key=%s&appver=%s&pver=3.1&url=";
    private final String client = "OGMyTestApp";
    private final String apiKey = "AIzaSyCt327fjnr8nU3FJyBWzaevKmAilypUbuc";
    private final String appver = "1.0.0";

    private final int REQUEST_CODE = 100;
    private final int NOTIFICATION_ID = 101;

    private final int GOOGLE_ENGINE = 1;
    private final int MCAFEE_ENGINE = 2;

    private final long SCAN_TIMEOUT_VALUE = 3000;

    private MySettings mSetting;
    private Handler mHandler;
    private SQLiteDatabase mDetectionDB;
    private Timer mTimer;
    private String mTmpUrl;
    private String mTmpDestPackage;
    private String mTmpDestClass;


    private static boolean mMFEScanResult;

    public AntiPhishingService(String name) {
        super(name);
    }

    public AntiPhishingService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();

        mSetting = new MySettings(getApplicationContext());
        mHandler = new Handler();

        mDetectionDB = new DetectionDBOpenHelper(getApplicationContext()).getWritableDatabase();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHanldeIntent()");
        if(intent == null) {
            return;
        }

        int demo_mode = mSetting.getDemoMode();
        switch (demo_mode) {
            case MySettings.DEMO1:
                demo1(intent);
                break;
            case MySettings.DEMO2:
                demo2(intent);
                break;
            case MySettings.DEMO3:
                demo3(intent);
                break;
            case MySettings.DEMO4:
                demo4(intent);
                break;
        }
    }

    private void demo1(Intent intent) {
        Log.d(TAG, "Start demo1()");
        String dest_package = intent.getStringExtra(DEST_PACKAGE);
        String dest_class = intent.getStringExtra(DEST_CLASS);
        String scan_url = intent.getStringExtra(SCAN_URL);
        Log.d(TAG, "dest_package: " + dest_package);
        Log.d(TAG, "dest_class: " + dest_class);
        Log.d(TAG, "scan_url: " + scan_url);

        // Pre-process: Send scan url to browser
        Intent send_intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(scan_url));
        send_intent1.setClassName(dest_package, dest_class);
        send_intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(send_intent1);

        // Scan-process
        int engine = mSetting.getAntiPhishingEngine();
        boolean result = true;
        String engine_name = "";
        if(engine == MySettings.NO_ENGINE) {
            engine_name = "Non";
            result = true;
        } else if(engine == MySettings.GOOGLE_SAFE_BROWSING) {
            engine_name = "Google";
            result = scanURL(intent, GOOGLE_ENGINE);
        } else if(engine == MySettings.MCAFEE_SITE_ADVISER) {
            engine_name = "McAfee";
            result = scanURL(intent, MCAFEE_ENGINE);
        }

        // Post-process
        if(result) {
            // Do nothing
        } else {
            sendNotification(scan_url);
            showToastMessage();
            logDetectionInfo(scan_url, engine_name);

            Intent send_intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(BLOCKING_URL + scan_url));
            send_intent2.setClassName(dest_package, dest_class);
            send_intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(send_intent2);
        }
    }

    private void demo2(Intent intent) {
        Log.d(TAG, "Start demo2()");
        String dest_package = intent.getStringExtra(DEST_PACKAGE);
        String dest_class = intent.getStringExtra(DEST_CLASS);
        String scan_url = intent.getStringExtra(SCAN_URL);
        Log.d(TAG, "dest_package: " + dest_package);
        Log.d(TAG, "dest_class: " + dest_class);
        Log.d(TAG, "scan_url: " + scan_url);

        // Pre-process: Do nothing

        // Scan-process
        int engine = mSetting.getAntiPhishingEngine();
        boolean result = true;
        String engine_name = "";
        if(engine == MySettings.NO_ENGINE) {
            engine_name = "Non";
            result = true;
        } else if(engine == MySettings.GOOGLE_SAFE_BROWSING) {
            engine_name = "Google";
            result = scanURL(intent, GOOGLE_ENGINE);
        } else if(engine == MySettings.MCAFEE_SITE_ADVISER) {
            engine_name = "McAfee";
            result = scanURL(intent, MCAFEE_ENGINE);
        }

        // Post-process
        if(result) {
            Intent send_intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(scan_url));
            send_intent1.setClassName(dest_package, dest_class);
            send_intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(send_intent1);
        } else {
            sendNotification(scan_url);
            showToastMessage();
            logDetectionInfo(scan_url, engine_name);

            Intent send_intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(BLOCKING_URL + scan_url));
            send_intent2.setClassName(dest_package, dest_class);
            send_intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(send_intent2);
        }
    }

    private void demo3(Intent intent) {
        Log.d(TAG, "Start demo3()");
        String dest_package = intent.getStringExtra(DEST_PACKAGE);
        String dest_class = intent.getStringExtra(DEST_CLASS);
        String scan_url = intent.getStringExtra(SCAN_URL);
        Log.d(TAG, "dest_package: " + dest_package);
        Log.d(TAG, "dest_class: " + dest_class);
        Log.d(TAG, "scan_url: " + scan_url);

        // Pre-process: Send scanning url to browser
        Intent send_intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(SCANNING_URL));
        send_intent1.setClassName(dest_package, dest_class);
        send_intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(send_intent1);

        // Scan-process
        int engine = mSetting.getAntiPhishingEngine();
        boolean result = true;
        String engine_name = "";
        if(engine == MySettings.NO_ENGINE) {
            engine_name = "Non";
            result = true;
        } else if(engine == MySettings.GOOGLE_SAFE_BROWSING) {
            engine_name = "Google";
            result = scanURL(intent, GOOGLE_ENGINE);
        } else if(engine == MySettings.MCAFEE_SITE_ADVISER) {
            engine_name = "McAfee";
            result = scanURL(intent, MCAFEE_ENGINE);
        }

        // Post-process
        if(result) {
            Intent send_intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(scan_url));
            send_intent2.setClassName(dest_package, dest_class);
            send_intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(send_intent2);
        } else {
            sendNotification(scan_url);
            showToastMessage();
            logDetectionInfo(scan_url, engine_name);

            Intent send_intent3 = new Intent(Intent.ACTION_VIEW, Uri.parse(BLOCKING_URL + scan_url));
            send_intent3.setClassName(dest_package, dest_class);
            send_intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(send_intent3);
        }
    }

    private void demo4(Intent intent) {
        Log.d(TAG, "Start demo4()");
        String dest_package = intent.getStringExtra(DEST_PACKAGE);
        String dest_class = intent.getStringExtra(DEST_CLASS);
        String scan_url = intent.getStringExtra(SCAN_URL);
        Log.d(TAG, "dest_package: " + dest_package);
        Log.d(TAG, "dest_class: " + dest_class);
        Log.d(TAG, "scan_url: " + scan_url);

        // Pre-process: Send scanning url to browser & Set timer
        Intent send_intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(SCANNING_URL));
        send_intent1.setClassName(dest_package, dest_class);
        send_intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(send_intent1);

        mTmpUrl = scan_url;
        mTmpDestPackage = dest_package;
        mTmpDestClass = dest_class;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent send_intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(mTmpUrl));
                send_intent2.setClassName(mTmpDestPackage, mTmpDestClass);
                send_intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(send_intent2);
            }
        }, SCAN_TIMEOUT_VALUE);

        // Scan-process
        int engine = mSetting.getAntiPhishingEngine();
        boolean result = true;
        String engine_name = "";
        if(engine == MySettings.NO_ENGINE) {
            engine_name = "Non";
            result = true;
        } else if(engine == MySettings.GOOGLE_SAFE_BROWSING) {
            engine_name = "Google";
            result = scanURL(intent, GOOGLE_ENGINE);
        } else if(engine == MySettings.MCAFEE_SITE_ADVISER) {
            engine_name = "McAfee";
            result = scanURL(intent, MCAFEE_ENGINE);
        }

        // Post-process
        if(mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if(result) {
            Intent send_intent3 = new Intent(Intent.ACTION_VIEW, Uri.parse(scan_url));
            send_intent3.setClassName(dest_package, dest_class);
            send_intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(send_intent3);
        } else {
            sendNotification(scan_url);
            showToastMessage();
            logDetectionInfo(scan_url, engine_name);

            Intent send_intent4 = new Intent(Intent.ACTION_VIEW, Uri.parse(BLOCKING_URL + scan_url));
            send_intent4.setClassName(dest_package, dest_class);
            send_intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(send_intent4);
        }
    }

    private boolean scanURL(Intent intent, int scan_engine) {
        Log.d(TAG, "scanURL()");

        long start_time, end_time;
        boolean result = false;
        String scan_url = intent.getStringExtra(SCAN_URL);

        switch(scan_engine) {
            case GOOGLE_ENGINE:
                try{
                    start_time = System.currentTimeMillis();
                    result = scanURLGoogle(scan_url);
                    end_time = System.currentTimeMillis();
                    Log.d(TAG, String.format("Scan time (Google) = %1$dms", (end_time - start_time)));
                } catch (Exception ex) {
                    Log.d(TAG, "EXCEPTION: Exception");
                }
                break;

            case MCAFEE_ENGINE:
                try{
                    start_time = System.currentTimeMillis();
                    result = scanURLMcAfee(scan_url);
                    end_time = System.currentTimeMillis();
                    Log.d(TAG, String.format("Scan time (McAfee) = %1$dms", (end_time - start_time)));
                } catch (Exception ex) {
                    Log.d(TAG, "EXCEPTION: Exception");
                }
                break;

            default:
                break;
        }

        return result;
    }

    private boolean scanURLGoogle(String url_in) throws IOException {
        Log.d(TAG, "scanURLGoogle()");

        String encoded_url = URLEncoder.encode(url_in, "utf-8");
        String urlFormat;
        boolean trusted;

        try {
            urlFormat = String.format(DEFAULT_API_URL_FORMAT,
                    URLEncoder.encode(client, "utf-8"),
                    URLEncoder.encode(apiKey, "utf-8"),
                    URLEncoder.encode(appver, "utf-8"), "%s");
        } catch (UnsupportedEncodingException ex) {
            trusted = true;
            Log.d(TAG, "EXCEPTION: UnsupportedEncodingException");
            return trusted;
        }

        URL url = new URL(urlFormat + encoded_url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setUseCaches(false);
        conn.connect();
        int code = conn.getResponseCode();
        switch (code) {
            case 200:
                trusted = false;
                Log.d(TAG, "OK(200): NOT TRUSTED " + url_in);
                break;
            case 204:
                trusted = true;
                Log.d(TAG, "OK(204): TRUSTED " + url_in);
                break;
            case 400:
                trusted = true;
                Log.d(TAG, "ERROR(400) HTTP req was not correctly formed");
                break;
            case 401:
                trusted = true;
                Log.d(TAG, "ERROR(401) API Key is not authorized");
                break;
            case 503:
                trusted = true;
                Log.d(TAG, "ERROR(503) Server cannot handle request");
                break;
            default:
                trusted = true;
                Log.d(TAG, "ERROR(xxx) Invalid reply from server");
                break;
        }
        conn.disconnect();
        return trusted;
    }

    private boolean scanURLMcAfee(String url_in) throws IOException {
        Log.d(TAG, "scanURLMcAfee()");

        CountDownLatch latch = new CountDownLatch(1);
        myCallBack cb = new myCallBack(latch);
        UrlEngine eng = new UrlEngine();
        eng.open(getApplicationContext());
        try {
            mMFEScanResult = true;
            eng.check(url_in, cb);
        } catch(Exception e) {
            Log.d(TAG, "McAfee Scan Exception");
        }

        try {
            latch.await();
        } catch (Exception e) {
            Log.d(TAG, "CountDownLatch.await() exception");
        }
        eng.close();

        return mMFEScanResult;
    }

    private void sendNotification(String scan_url) {
        Log.d(TAG, "sendNotification()");
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentIntent(contentIntent);
        builder.setTicker("Block phishing site");
        builder.setSmallIcon(R.drawable.ic_small);
        Bitmap largeicon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_big);
        builder.setLargeIcon(largeicon);
        builder.setContentTitle("Anti-Phishing");
        builder.setContentText(scan_url + " is detected as Phishing Site!");
        builder.setWhen(System.currentTimeMillis());
        builder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        builder.setAutoCancel(true);

        NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    private void showToastMessage() {
        Log.d(TAG, "showToastMessage()");

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Phishing site is blocked", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void logDetectionInfo(String scan_url, String engine_name) {
        ContentValues values = new ContentValues();
        values.put(DetectionDBOpenHelper.COLUMN_URL, scan_url);
        values.put(DetectionDBOpenHelper.COLUMN_ENGINE, engine_name);
        values.put(DetectionDBOpenHelper.COLUMN_DATE, System.currentTimeMillis());
        mDetectionDB.insert(DetectionDBOpenHelper.TABLE_NAME, null, values);
    }

    public class myCallBack implements UrlCheckCallback {
        private CountDownLatch mLatch;

        myCallBack(CountDownLatch latch) {
            mLatch = latch;
        }

        @Override
        public void onCanceled() {
            Log.d(TAG, "UrlCheckCallback.onCanceled()");
        }

        @Override
        public void onCompleted() {
            Log.d(TAG, "UrlCheckCallback.onCompleted()");

            mLatch.countDown();
        }

        @Override
        public void onDetected(UrlCheckResult result) {
            Log.d(TAG, "UrlCheckCallback.onDetected()");
            Log.d(TAG, String.format("UrlCheckResult: rep(%d) risk(%d) url(%s)",
                    result.getRep(), result.getRisk(), result.getUrl()));

            if(result.getRisk() == UrlCheckResult.RISK_HIGH) {
                Log.d(TAG, "URL is detected as high risk.");
                mMFEScanResult = false;
            }
        }

        @Override
        public void onError(int errorCode) {
            Log.d(TAG, "UrlCheckCallback.onError()" + errorCode);

        }
    };

}
