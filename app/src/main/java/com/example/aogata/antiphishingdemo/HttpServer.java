package com.example.aogata.antiphishingdemo;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by aogata on 16. 4. 7.
 */
public class HttpServer extends AsyncTask<String, String, String> {
    public static final String BLOCKING_FILE = "blocking.html";
    public static final String BLOCKING_FILE_PRE = "blocking.pre";
    public static final String BLOCKING_FILE_POST = "blocking.post";
    public static final String SCANNING_FILE = "scanning.html";
    public static final String PROCESSING_FILE = "processing.gif";

    private static final String TAG = "HttpServer";
    private ServerSocket mServerSocket;
    private Socket mSocket;
    private MainActivity mMainActivity;

    public HttpServer(MainActivity main) {
        super();

        Log.d(TAG, "HttpServer()");
        mMainActivity = main;
        try {
            mServerSocket = new ServerSocket(50080);
            copyFiles(BLOCKING_FILE_PRE);
            copyFiles(BLOCKING_FILE_POST);
            copyFiles(SCANNING_FILE);
            copyFiles(PROCESSING_FILE);
        } catch (Exception e) {
            Log.d(TAG, "=== Exception1 in HttpServer()");
            Log.e(TAG, e.toString());
        }
    }

    @Override
    protected String doInBackground(String... values) {
        Log.d(TAG, "doInBackground()");

        while(true) {
            try {
                mSocket = mServerSocket.accept();
                serverProcess();
            } catch (Exception e) {
                Log.d(TAG, "=== Exception1 in doInBackground()");
                Log.e(TAG, e.toString());
            }
        }
    }


    @Override
    protected void onProgressUpdate(String... values) {

    }

    @Override
    protected void onPostExecute(String result) {

    }

    private void serverProcess() throws IOException {
        Log.d(TAG,"serverProcess()");

        try (
                InputStream in = mSocket.getInputStream();
                OutputStream out = mSocket.getOutputStream();
        ) {
            HttpReq req = new HttpReq(in);
            String header = req.getHeader();
            if(header != null) {
                Log.d(TAG, header);
            }

            if (req.isRequestValid()) {
                HttpRes res = new HttpRes(mMainActivity, req.getFileName(), req.getUrlInfo());
                res.writeTo(out);
            }
        } catch (Exception e) {
            Log.d(TAG, "=== Exception1 in serverProcess()");
            Log.e(TAG, e.toString());
        } finally {
            try {
                mSocket.close();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    private void copyFiles(String filename) throws IOException {
        byte[] buffer = new byte[1024*4];
        int length = 0;

        InputStream in = mMainActivity.getAssets().open(filename);
        FileOutputStream out = mMainActivity.openFileOutput(filename, Context.MODE_PRIVATE);

        while ((length = in.read(buffer)) >= 0) {
            out.write(buffer, 0, length);
        }
        out.close();
        in.close();
    }

}


