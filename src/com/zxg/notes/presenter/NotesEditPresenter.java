package com.zxg.notes.presenter;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.content.Context;

import com.zxg.notes.bean.Notes;
import com.zxg.notes.database.NotesDAO;
import com.zxg.notes.interfaces.NotesEditViewInterface;

public class NotesEditPresenter {
    final static String TAG = "NotesEditPresenter";
    public static String CONTEXT_IS_EMPTY = "context_is_empty";
    public static int NO_CURRENT_NOTES = -1;
    private NotesEditViewInterface notesEditView;
    private List<NotesListUpdateListener> notesListUpdateListener = new ArrayList<NotesListUpdateListener>();
    private Context mContext;
    private NotesDAO notesDAO;

    private Notes setNotesData(Notes notes) {
        notes.setmContent(notesEditView.getNotesContent());
        notes.setmCreateTime(System.currentTimeMillis());
        notes.setmAlarmTime(notesEditView.getNotesAlarmTime());
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
        if (notesEditView.checkContentIsEmpty()) {
            notesEditView.showErrorToast(CONTEXT_IS_EMPTY);
        } else {
            if (notesEditView.getCurrentNotesId() == NO_CURRENT_NOTES) {
                notes = new Notes();
                notes = setNotesData(notes);
                notesDAO.insertNotes(notes);
            } else {
                notes = notesDAO.findNotesById(notesEditView
                        .getCurrentNotesId());
                notes = setNotesData(notes);
                notesDAO.updateNotes(notes);
            }
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
}
