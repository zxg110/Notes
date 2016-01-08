package com.zxg.notes.interfaces;

import java.util.List;

import com.zxg.notes.bean.Notes;

import android.widget.BaseAdapter;

public interface NotesListViewInterface {
    void showNoNotesImage(boolean isShow);
    void toNotesEditActivityForNew();
    void toNotesEditActivityForEdit();
    void fillNotesListView(List<Notes> notesList);

    
}
