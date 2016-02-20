package com.zxg.notes.presenter;

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
    private List<NotesListUpdateListener> notesListUpdateListener = new ArrayList<NotesListUpdateListener>();
    private Context mContext;
    private NotesDAO notesDAO;
    private AlarmUtil alarmUtil;

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

    public void saveNotes() {
        Notes notes;
        if (notesEditView.getCurrentNotesId() == NO_CURRENT_NOTES) {
            notes = new Notes();
            notes = setNotesData(notes);
            notesDAO.insertNotes(notes);
            Date date = new Date(notes.getmAlarmTime());
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
            Log.i("zxg", "format alarm time:" + sdf.format(date));
            int currentId = notesDAO.findLastInsertNotesId();
            Log.i("zxg", "presenter last insert notes id:" + currentId);
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
        notifyNotesListUpdateListener();
        Log.i("zxg", "save notes visible:" + notes.getmVisible());
        notesEditView.toNotesListView();

    }

    interface NotesListUpdateListener {
        void onNotesListUpdate();
    }

    public void addListener(NotesListUpdateListener listener) {
        notesListUpdateListener.add(listener);
    }

    private void notifyNotesListUpdateListener() {
        for (NotesListUpdateListener listener : notesListUpdateListener) {
            listener.onNotesListUpdate();
        }
    }

    public void initNotesData(long id) {
        Notes currentNotes = notesDAO.findNotesById(id);
        notesEditView.setContentView(currentNotes.getmContent());
        notesEditView.setTimeView(DateUtil.converTime(mContext,
                currentNotes.getmCreateTime()));
        notesEditView.setAlarmTime(currentNotes.getmAlarmTime());
        // add field:title
        notesEditView.setTitleView(currentNotes.getmTitle());

    }

    public void deleteNote(int id) {
        Notes notes = notesDAO.findNotesById(id);
        // 取消闹钟
        alarmUtil.setAlarmRemind(AlarmUtil.CANCEL_ALARM, notes.getmAlarmTime(),
                id);
        notesDAO.deleteNotesById(id);
        notifyNotesListUpdateListener();
        notesEditView.toNotesListView();
    }

    public void deleteAlarm(int noteId, long alarmTime) {
        alarmUtil.setAlarmRemind(AlarmUtil.CANCEL_ALARM, alarmTime, noteId);
    }
}
