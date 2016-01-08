package com.zxg.notes.adapter;

import java.util.ArrayList;
import java.util.List;

import com.zxg.notes.R;
import com.zxg.notes.bean.Notes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NotesAdapter extends BaseAdapter {
    final static String TAG = "NotesAdapter";
    List<Notes> notesList = new ArrayList<Notes>();
    private LayoutInflater mInfalter = null;

    public NotesAdapter(List<Notes> list, Context context) {
        notesList = list;
        mInfalter = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return notesList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Notes notes = notesList.get(position);
        Log.i(TAG, "Notes:" + notes.toString());
        convertView = mInfalter.inflate(R.layout.notes_item, null);
        Log.i(TAG, "convertView" + convertView.toString());
        TextView mNotesContent = (TextView) convertView
                .findViewById(R.id.title_tv);
        TextView mNotesCreateTime = (TextView) convertView
                .findViewById(R.id.time_tv);
        ImageView mAlarmLaber = (ImageView) convertView
                .findViewById(R.id.alarm_lable);
        mNotesContent.setText(notes.getmContent());
        mNotesCreateTime.setText(notes.getmCreateTime() + "");
        if (notes.getmAlarmTime() != 0) {
            mAlarmLaber.setVisibility(View.VISIBLE);
        } else {
            mAlarmLaber.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

}
