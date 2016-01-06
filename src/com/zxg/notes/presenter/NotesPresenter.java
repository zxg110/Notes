package com.zxg.notes.presenter;

import android.content.Context;
import android.util.Log;

import com.zxg.notes.database.NotesDAO;
import com.zxg.notes.interfaces.NotesListViewInterface;


public class NotesPresenter {
    final static String TAG = "NotesPresenter";
    private NotesListViewInterface notesListView;
    private Context mContext;
    private NotesDAO notesDAO;
    public NotesPresenter(NotesListViewInterface notesListActivity) {
        notesListView = notesListActivity;
        mContext = (Context) notesListActivity;
//        notesDAO = new notesDAO();
    }
    public void newNotes(){
        notesListView.toNotesEditActivity();
        Log.i(TAG, "newNotes");
    }
    public void toDeleteNotes(){
        
    }
    public void init(){
        
    }
    
}
