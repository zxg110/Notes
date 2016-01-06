package com.zxg.notes.test;

import com.zxg.notes.bean.Notes;
import com.zxg.notes.database.NotesDAO;
import com.zxg.notes.database.NotesDatabaseHelper;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

public class NoteDatabaseHelperTest extends AndroidTestCase{
    NotesDatabaseHelper dbHelper;
    SQLiteDatabase db;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
//        dbHelper = new NotesDatabaseHelper(getContext(),"TestNotes.db",null,1);
//        db = dbHelper.getWritableDatabase();
    }
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
//        dbHelper.close();
    }
    public void testCreateDatabase(){
        db = dbHelper.getWritableDatabase();
        Log.i("Test", "db:"+db.toString());
    }
    public void testInsertData(){
        ContentValues values  = new ContentValues();
        values.put("content", "test");
        values.put("alarm_time", 100000);
        values.put("create_time", 100000000);
        db.insert("notes", null, values);
    }
    public void testDAOInsert(){
        NotesDAO notesDAO = new NotesDAO(getContext());
        Notes notes = new Notes();
        notes.setmAlarmTime(2000000);
        notes.setmContent("test111");
        notes.setmCreateTime(300000);
        notesDAO.insertNotes(notes);
    }
    public void testDAODelete(){
        NotesDAO notesDAO = new NotesDAO(getContext());
        notesDAO.deleteNotesById(1);
    }
}
