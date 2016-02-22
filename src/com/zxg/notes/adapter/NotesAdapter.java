package com.zxg.notes.adapter;
/**
 * adapter:用于装配ListView，每一个item显示一个便签
 */
import java.util.ArrayList;
import java.util.List;

import com.zxg.notes.R;
import com.zxg.notes.bean.Notes;
import com.zxg.notes.util.DateUtil;

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
    private Context mContext;
    private LayoutInflater mInfalter = null;

    public NotesAdapter(List<Notes> list, Context context) {
        notesList = list;
        mInfalter = LayoutInflater.from(context);
        mContext = context;

    }

    @Override
    public int getCount() {
        return notesList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return notesList.get(position).getmId();
    }
    //重写该方法，设置item的显示
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //获取该位置需要显示的便签
        Notes notes = notesList.get(position);
        //加载item布局
        convertView = mInfalter.inflate(R.layout.notes_item, null);
        //设置布局
        TextView mNotesTitle = (TextView) convertView
                .findViewById(R.id.title_tv);
        TextView mNotesCreateTime = (TextView) convertView
                .findViewById(R.id.time_tv);
        ImageView mAlarmLaber = (ImageView) convertView
                .findViewById(R.id.alarm_lable);
        mNotesTitle.setText(notes.getmTitle());
        mNotesCreateTime.setText(DateUtil.converTime(mContext,
                notes.getmCreateTime()));
        if (notes.getmAlarmTime() != -1) {
            mAlarmLaber.setVisibility(View.VISIBLE);
        } else {
            mAlarmLaber.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

}
