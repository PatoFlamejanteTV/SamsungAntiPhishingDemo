package com.example.aogata.antiphishingdemo;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by aogata on 16. 4. 6.
 */
public class HttpReq {
    private static final String TAG = "HttpReq";
    private static final String CORRECT_REQ1 = "GET /blocking.html";
    private static final String CORRECT_REQ2 = "GET /scanning.html HTTP/1.1";
    private static final String CORRECT_REQ3 = "GET /processing.gif HTTP/1.1";

    private static int FILETYPE_HTML = 0;
    private static int FILETYPE_GIF = 1;

    private HttpHeader mHeader;
    private int mFileType;

    public HttpReq(InputStream input) throws IOException {
        Log.d(TAG, "HttpReq()");

        mHeader = new HttpHeader();
        BufferedReader in = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        String line = in.readLine();
        if(line == null) {
            return;
        }

        while(line != null && !line.isEmpty()) {
            Log.d(TAG, line);
            mHeader.append(line + "\r\n");
            line = in.readLine();
        }
        mHeader.finalize_string();
    }
    
    public boolean isRequestValid() {
        Log.d(TAG, "isRequestValid()");
        if(mHeader.getHttpHeader().startsWith(CORRECT_REQ1) ||
           mHeader.getHttpHeader().startsWith(CORRECT_REQ2)
           )
        {
            mFileType = FILETYPE_HTML;
            return true;
        } else if(mHeader.getHttpHeader().startsWith(CORRECT_REQ3)) {
            mFileType = FILETYPE_GIF;
            return true;
        }

        return false;
    }

    public String getHeader() {
        return mHeader.getHttpHeader();
    }

    public String getUrlInfo() {
        String temp = mHeader.getRequest();
        int start_index = temp.indexOf("?url=") + 5;
        if(start_index == -1) {
            return null;
        }
        int end_index = temp.indexOf("HTTP") - 1;
        return temp.substring(start_index, end_index);
    }

    public String getFileName() {
        String temp = mHeader.getRequest();
        int start_index = temp.indexOf("GET /") + 5;
        int end_index = -1;
        if(mFileType == FILETYPE_HTML) {
            end_index = temp.indexOf(".html") + 5;
        } else if(mFileType == FILETYPE_GIF) {
            end_index = temp.indexOf(".gif") + 4;
        }

        if(start_index == -1 || end_index == -1) {
            return null;
        }
        return temp.substring(start_index, end_index);
    }
}
