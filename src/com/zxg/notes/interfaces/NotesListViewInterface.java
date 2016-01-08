package com.zxg.notes.interfaces;

import java.util.List;
import com.zxg.notes.bean.Notes;

public interface NotesListViewInterface {
    void showNoNotesImage(boolean isShow);

    void toNotesEditActivityForNew();

    void toNotesEditActivityForEdit(long id);

    void fillNotesListView(List<Notes> notesList);

}
