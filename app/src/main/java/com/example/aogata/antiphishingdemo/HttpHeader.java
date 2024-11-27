package com.example.aogata.antiphishingdemo;

import android.util.Log;

/**
 * Created by aogata on 16. 4. 8.
 */
public class HttpHeader {
    private static final String TAG = "HttpHeader";
    private StringBuilder mHeaderData = new StringBuilder();
    private String mHeader;

    public String getHttpHeader() {
        return mHeader;
    }

    public void append(String str) {
        if(str == null) {
            Log.d(TAG, "str is null");
        } else {
            Log.d(TAG, str);
            mHeaderData.append(str);
        }
    }

    public void finalize_string() {
        mHeader = mHeaderData.toString();
    }

    public String getRequest() {
        int start_index = mHeader.indexOf("GET");
        int end_index = mHeader.indexOf("\r\n") + 2;
        return mHeader.substring(start_index, end_index);
    }
}
