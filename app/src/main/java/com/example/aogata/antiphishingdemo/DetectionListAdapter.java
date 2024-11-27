package com.example.aogata.antiphishingdemo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by aogata on 16. 1. 28.
 */
public class DetectionListAdapter extends ArrayAdapter<DetectedURL> {
    private static final String TAG = "DetectionListAdapter";

    private LayoutInflater layoutInflater;

    public DetectionListAdapter(Context context, int resource, ArrayList<DetectedURL> detections) {
        super(context, resource, detections);

        Log.d(TAG, "DetectionListAdapter()");
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView()");

        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.detection_list_item, null);
        }

        DetectedURL detection = getItem(position);
        TextView url = (TextView)convertView.findViewById(R.id.url);
        TextView date = (TextView)convertView.findViewById(R.id.date);
        url.setText(detection.getUrl());
        date.setText("Date: " + detection.getDate());

        return convertView;
    }

    public void refleshItemList() {

    }
}
