package com.zxg.notes.util;

import com.zxg.notes.activity.AlarmAlertActivity;
import com.zxg.notes.bean.Notes;
import com.zxg.notes.database.NotesDAO;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.Settings.System;
import android.util.Log;

public class AlarmUtil {
    private Context mContext;
    public final static int SET_ALARM = 0;
    public final static int CANCEL_ALARM = 1;
    private PendingIntent mSender;
    private AlarmManager mAlarmManager;
    private NotesDAO notesDAO;
    public AlarmUtil(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        notesDAO = new NotesDAO(mContext);
    }
    public void setAlarmRemind(int flag,long millsec,int noteId){
        if(millsec <0){
            Log.i("zxg", "flag:"+flag+"millsec <0 return");
            return;
        }
        Notes notes = notesDAO.findNotesById((long)noteId);
        Intent intent = new Intent(mContext,AlarmAlertActivity.class);
        intent.setAction("note alarm id is"+Integer.toString(noteId));
        intent.putExtra("alarmid", noteId);
        intent.putExtra("alarmTitle", notes.getmTitle());
        mSender = PendingIntent.getActivity(mContext, 0, intent, 0);
        if(flag == SET_ALARM){
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, millsec, mSender);
            Log.i("zxg", "alarm is started noteId:"+noteId);
        }else if(flag == CANCEL_ALARM){
            mAlarmManager.cancel(mSender);
            Log.i("zxg", "alarm is stop noteId:"+noteId);
        }
        
    }
    
}
