package com.zxg.notes.presenter;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.content.Context;
import android.util.Log;

import com.zxg.notes.bean.Notes;
import com.zxg.notes.database.NotesDAO;
import com.zxg.notes.interfaces.NotesEditViewInterface;
import com.zxg.notes.util.DateUtil;

public class NotesEditPresenter {
    final static String TAG = "NotesEditPresenter";
    public final static int CONTEXT_IS_EMPTY = 1;
    public static int NO_CURRENT_NOTES = -1;
    private NotesEditViewInterface notesEditView;
    private List<NotesListUpdateListener> notesListUpdateListener = new ArrayList<NotesListUpdateListener>();
    private Context mContext;
    private NotesDAO notesDAO;

    private Notes setNotesData(Notes notes) {
        notes.setmContent(notesEditView.getNotesContent());
        notes.setmCreateTime(System.currentTimeMillis());
        notes.setmAlarmTime(notesEditView.getNotesAlarmTime());
        //add field:title
        notes.setmTitle(notesEditView.getNotesTitle());
        Log.i("111", "after set Title:"+notes.getmTitle());
        return notes;
    }

    public NotesEditPresenter(NotesEditViewInterface notesEditViewInterface,
            Context context) {
        notesEditView = notesEditViewInterface;
        mContext = context;
        notesDAO = new NotesDAO(mContext);
    }

    public void saveNotes() {
        Notes notes;

        if (notesEditView.getCurrentNotesId() == NO_CURRENT_NOTES) {
            notes = new Notes();
            notes = setNotesData(notes);
            notesDAO.insertNotes(notes);
        } else {
            notes = notesDAO.findNotesById(notesEditView.getCurrentNotesId());
            notes = setNotesData(notes);
            notesDAO.updateNotes(notes);
        }
        notifyNotesListUpdateListener();
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
        //add field:title
        notesEditView.setTitleView(currentNotes.getmTitle());
    }
    public void deleteNote(long id){
        notesDAO.deleteNotesById(id);
        notifyNotesListUpdateListener();
        notesEditView.toNotesListView();
    }
}
