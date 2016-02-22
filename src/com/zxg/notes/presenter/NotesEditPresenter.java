package com.zxg.notes.presenter;
/**
 * Presenter:该类封装了NotesEditActivity需要用到的一些方法
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.R;
import android.content.Context;
import android.util.Log;

import com.zxg.notes.bean.Notes;
import com.zxg.notes.database.NotesDAO;
import com.zxg.notes.interfaces.NotesEditViewInterface;
import com.zxg.notes.util.AlarmUtil;
import com.zxg.notes.util.DateUtil;

public class NotesEditPresenter {
    final static String TAG = "NotesEditPresenter";
    public final static int CONTEXT_IS_EMPTY = 1;
    public static int NO_CURRENT_NOTES = -1;
    // visible
    public static int VISIBLE_PRIVATE = 0;
    public static int VISIBLE_PUBLIC = 1;
    private NotesEditViewInterface notesEditView;
    private Context mContext;
    private NotesDAO notesDAO;
    private AlarmUtil alarmUtil;
    //填充notes数据方法
    private Notes setNotesData(Notes notes) {
        notes.setmContent(notesEditView.getNotesContent());
        notes.setmCreateTime(System.currentTimeMillis());
        notes.setmAlarmTime(notesEditView.getNotesAlarmTime());
        // add field:title
        notes.setmTitle(notesEditView.getNotesTitle());
        // add field:visible
        notes.setmVisible(notesEditView.getVisible());
        Log.i("zxg", "setNotesData visible:" + notesEditView.getVisible());
        return notes;
    }

    public NotesEditPresenter(NotesEditViewInterface notesEditViewInterface,
            Context context) {
        notesEditView = notesEditViewInterface;
        mContext = context;
        notesDAO = new NotesDAO(mContext);
        alarmUtil = new AlarmUtil(mContext);
    }
    //保存便签方法
    public void saveNotes() {
        Notes notes;
        if (notesEditView.getCurrentNotesId() == NO_CURRENT_NOTES) {
            notes = new Notes();
            notes = setNotesData(notes);
            notesDAO.insertNotes(notes);
            int currentId = notesDAO.findLastInsertNotesId();
            // set alarm
            alarmUtil.setAlarmRemind(AlarmUtil.SET_ALARM,
                    notes.getmAlarmTime(), currentId);

        } else {
            notes = notesDAO.findNotesById(notesEditView.getCurrentNotesId());
            // 如果提醒时间改变了，首先取消原先的闹铃
            if (notes.getmAlarmTime() != notesEditView.getNotesAlarmTime()) {
                alarmUtil.setAlarmRemind(AlarmUtil.CANCEL_ALARM,
                        notes.getmAlarmTime(), notes.getmId());
            }
            notes = setNotesData(notes);
            // 设置新闹铃
            alarmUtil.setAlarmRemind(AlarmUtil.SET_ALARM,
                    notes.getmAlarmTime(), notes.getmId());

            notesDAO.updateNotes(notes);
        }

        notesEditView.toNotesListView();

    }
    //初始化便签数据方法
    public void initNotesData(long id) {
        Notes currentNotes = notesDAO.findNotesById(id);
        notesEditView.setContentView(currentNotes.getmContent());
        notesEditView.setTimeView(DateUtil.converTime(mContext,
                currentNotes.getmCreateTime()));
        notesEditView.setAlarmTime(currentNotes.getmAlarmTime());
        // add field:title
        notesEditView.setTitleView(currentNotes.getmTitle());

    }
    //删除便签方法
    public void deleteNote(int id) {
        Notes notes = notesDAO.findNotesById(id);
        // 取消闹钟
        alarmUtil.setAlarmRemind(AlarmUtil.CANCEL_ALARM, notes.getmAlarmTime(),
                id);
        notesDAO.deleteNotesById(id);
        notesEditView.toNotesListView();
    }
    //删除闹钟提醒方法
    public void deleteAlarm(int noteId, long alarmTime) {
        alarmUtil.setAlarmRemind(AlarmUtil.CANCEL_ALARM, alarmTime, noteId);
    }
}
