package com.zxg.notes.interfaces;
/**
 * 便签列表界面接口，该接口定义了列表界面需要实现的方法，NotesMainActivity
 * 实现该接口
 */
import java.util.List;
import com.zxg.notes.bean.Notes;

public interface NotesListViewInterface {

    void toNotesEditActivityForNew();

    void toNotesEditActivityForEdit(long id);

    void fillNotesListView(List<Notes> notesList);

}
