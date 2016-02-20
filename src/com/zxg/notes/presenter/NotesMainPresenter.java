package com.zxg.notes.presenter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.telephony.gsm.SmsManager;
import android.util.Log;

import com.zxg.notes.R;
import com.zxg.notes.bean.Notes;
import com.zxg.notes.database.NotesDAO;
import com.zxg.notes.interfaces.NotesListViewInterface;
import com.zxg.notes.presenter.NotesEditPresenter.NotesListUpdateListener;

public class NotesMainPresenter implements NotesListUpdateListener {
    final static String TAG = "NotesMainPresenter";
    //
    public static String MODE = "mode";
    public static String EDIT_MODE = "edit";
    public static String NEW_MODE = "new";
    public static String VISIBLE = "visible";
    public static String PRIVATE = "private";
    public static String PUBLIC = "public";
    private NotesListViewInterface notesListView;
    private List<Notes> notesList = new ArrayList<Notes>();
    private Context mContext;
    private NotesDAO notesDAO;

    public NotesMainPresenter(NotesListViewInterface notesListActivity,
            Context context) {
        notesListView = notesListActivity;
        mContext = context;
        Log.i(TAG, "mContext:" + context.toString());
        notesDAO = new NotesDAO(mContext);
    }

    public void newNotes() {
        notesListView.toNotesEditActivityForNew();
    }

    public void initNotesListView(String visible) {
        notesList.clear();
        notesList = notesDAO.findAllNotes();
        Iterator<Notes> iter = notesList.iterator();
        if (visible.equals(NotesMainPresenter.PRIVATE)) {
            while (iter.hasNext()) {
                Notes notes = iter.next();
                if (notes.getmVisible() == NotesEditPresenter.VISIBLE_PUBLIC) {
                    iter.remove();
                }
            }
        } else if (visible.equals(NotesMainPresenter.PUBLIC)) {
            while (iter.hasNext()) {
                Notes notes = iter.next();
                if (notes.getmVisible() == NotesEditPresenter.VISIBLE_PRIVATE) {
                    iter.remove();
                }
            }
        }
        notesListView.fillNotesListView(notesList);

    }

    public void sendMessage(String phoneNum, int notesId) {
        Log.i("zxg", "phoneNum:" + phoneNum);
        Notes notes = notesDAO.findNotesById((long) notesId);
        String smsContent = mContext.getResources().getString(R.string.title)
                + notes.getmTitle() + "\n"
                + mContext.getResources().getString(R.string.content)
                + notes.getmContent();
        SmsManager smsManager = SmsManager.getDefault();
        if (smsContent.length() > 70) {
            List<String> contents = smsManager.divideMessage(smsContent);
            for (String sms : contents) {
                smsManager.sendTextMessage(phoneNum, null, sms, null, null);
            }
        } else {
            smsManager.sendTextMessage(phoneNum, null, smsContent, null, null);
        }
    }

    @Override
    public void onNotesListUpdate() {
        initNotesListView(NotesMainPresenter.PUBLIC);
    }

    public void editNotes(long id) {
        notesListView.toNotesEditActivityForEdit(id);
    }

}
