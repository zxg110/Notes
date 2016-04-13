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
/**
 * 设置提醒工具类
 * @author zxg
 *
 */
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
    //设置或取消提醒方法(根据参数flag来决定是设置还是取消，millsec是提醒的时间点，noteId用来标记闹铃)
    //level用于标记闹铃级别
    public void setAlarmRemind(int flag,long millsec,int noteId,int level){
        if(millsec <0){
            return;
        }
        Notes notes = notesDAO.findNotesById((long)noteId);
        Intent intent = new Intent(mContext,AlarmAlertActivity.class);
        intent.setAction("note alarm id is"+Integer.toString(noteId));
        intent.putExtra("noteId", noteId);
        intent.putExtra("alarmTitle", notes.getmTitle());
        Log.i("zxg", "level in alarm_util:"+level);
        intent.putExtra("alarm_level", level);
        mSender = PendingIntent.getActivity(mContext, 0, intent, 0);
        if(flag == SET_ALARM){
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, millsec, mSender);
        }else if(flag == CANCEL_ALARM){
            mAlarmManager.cancel(mSender);
        }
        
    }
    
}
