package com.zxg.notes.presenter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zxg.notes.adapter.NotesAdapter;
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
        Log.i(TAG, "newNotes");
    }

    public void toDeleteNotes() {

    }

    public void initNotesListView() {
        notesList.clear();
        notesList = notesDAO.findAllNotes();
        if (notesList.size() <= 0) {
            notesListView.showNoNotesImage(true);
        } else {
            notesListView.fillNotesListView(notesList);
        }
    }

    @Override
    public void onNotesListUpdate() {
        initNotesListView();
    }

}
