package com.zxg.notes.test;

import com.zxg.notes.bean.Notes;
import com.zxg.notes.database.NotesDAO;
import com.zxg.notes.database.NotesDatabaseHelper;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

public class NoteDatabaseHelperTest extends AndroidTestCase {
    NotesDatabaseHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        dbHelper.close();
    }

    public void testDAOInsert() {
        NotesDAO notesDAO = new NotesDAO(getContext());
        Notes notes = new Notes();
        notes.setmAlarmTime(2000000);
        notes.setmContent("test999");
        notes.setmCreateTime(300000);
        notesDAO.insertNotes(notes);
    }

    public void testDAODelete() {
        NotesDAO notesDAO = new NotesDAO(getContext());
        notesDAO.deleteNotesById(1);
    }

    public void testDAOFindById() {
        NotesDAO notesDAO = new NotesDAO(getContext());
        Notes notes = notesDAO.findNotesById(4);
        Log.i("222", "notes Content:" + notes.getmContent());

    }
}
