package com.example.aogata.antiphishingdemo;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by aogata on 16. 4. 6.
 */
public class HttpRes {
    private static final String TAG = "HttpRes";
    private static final String STATUS_LINE = "HTTP/1.1 200 OK\r\n";
    private static final String CONTENT_TYPE_TEXT = "Content-Type: text/html\r\n\r\n";
    private static final String CONTENT_TYPE_IMAGE = "Content-Type: image/gif\r\n\r\n";

    private MainActivity mMainActivity;
    private String mStatusLine;
    private String mResHeader;
    private String mBodyFile;

    public HttpRes(MainActivity activity, String file, String url) {
        Log.d(TAG, "HttpRes()");
        mMainActivity = activity;
        mStatusLine = STATUS_LINE;
        mBodyFile = file;
        if(mBodyFile.equals("blocking.html")) {
            mResHeader = CONTENT_TYPE_TEXT;
            createBodyFile(url);
        } else if(mBodyFile.equals("scanning.html")) {
            mResHeader = CONTENT_TYPE_TEXT;
        } else if(mBodyFile.equals("processing.gif")) {
            mResHeader = CONTENT_TYPE_IMAGE;
        }
    }

    public void writeTo(OutputStream out) throws IOException {
        Log.d(TAG, "writeTo()");
        sendStatusLine(out);
        sendResHeader(out);
        sendBodyFile(out);
    }

    private void sendStatusLine(OutputStream out) {
        Log.d(TAG, "sendStatusLine()");
        try {
            out.write(mStatusLine.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void sendResHeader(OutputStream out) {
        Log.d(TAG, "sendResHeader()");
        try {
            out.write(mResHeader.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void sendBodyFile(OutputStream out){
        Log.d(TAG, "sendBodyFile()");
        byte[] buffer = new byte[1024*4];
        int length = 0;

        try {
            FileInputStream in = mMainActivity.openFileInput(mBodyFile);
            while ((length = in.read(buffer)) >= 0) {
                out.write(buffer, 0, length);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void createBodyFile(String url) {
        byte[] buffer = new byte[1024*4];
        int length = 0;

        try {
            FileInputStream in1 = mMainActivity.openFileInput(HttpServer.BLOCKING_FILE_PRE);
            FileInputStream in2 = mMainActivity.openFileInput(HttpServer.BLOCKING_FILE_POST);
            FileOutputStream out = mMainActivity.openFileOutput(HttpServer.BLOCKING_FILE, Context.MODE_PRIVATE);

            while ((length = in1.read(buffer)) >= 0) {
                out.write(buffer, 0, length);
            }

            out.write(url.getBytes());

            while ((length = in2.read(buffer)) >= 0) {
                out.write(buffer, 0, length);
            }

            out.close();
            in1.close();
            in2.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
}
