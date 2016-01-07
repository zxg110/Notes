package com.zxg.notes.interfaces;

import android.widget.BaseAdapter;

public interface NotesListViewInterface {
    void showNoNotesImage(boolean isShow);
    void toNotesEditActivityForNew();
    void toNotesEditActivityForEdit();
    void fillNotesList(BaseAdapter adapter);
    
}
