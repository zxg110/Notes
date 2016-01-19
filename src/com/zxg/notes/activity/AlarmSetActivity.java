package com.zxg.notes.activity;

import com.zxg.notes.R;

import android.app.Activity;
import android.os.Bundle;

public class AlarmSetActivity extends Activity {
    final static String TAG = "AlarmSetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_time_picker);
    }
}
