package com.zxg.notes.database;

import java.util.ArrayList;
import java.util.List;

import com.zxg.notes.bean.Notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NotesDAO {
    private final NotesDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private ContentValues values;
    private List<Notes> notesList;

    public NotesDAO(Context context) {
        dbHelper = new NotesDatabaseHelper(context, "Notes.db", null, 1);
        db = dbHelper.getWritableDatabase();
        values = new ContentValues();
        notesList = new ArrayList<Notes>();
    }

    public void insertNotes(Notes notes) {
        values.clear();
        values.put("content", notes.getmContent());
        values.put("alarm_time", notes.getmAlarmTime());
        values.put("create_time", notes.getmCreateTime());
        db.insert(NotesDatabaseHelper.TB_NOTES, null, values);
    }

    public void deleteNotesById(int id) {
        db.delete(NotesDatabaseHelper.TB_NOTES,
                NotesDatabaseHelper.ID + " = ?", new String[] { id + "" });
    }

    public void updateNotes(Notes notes) {
        values.clear();
        values.put("content", notes.getmContent());
        values.put("alarm_time", notes.getmAlarmTime());
        values.put("create_time", notes.getmCreateTime());
        db.update(NotesDatabaseHelper.TB_NOTES, values, NotesDatabaseHelper.ID
                + " = ?", new String[] { notes.getmId() + "" });
    }

    public List<Notes> findAllNotes() {
        notesList.clear();
        Notes n = null;
        Cursor cur = db.query(NotesDatabaseHelper.TB_NOTES, null, null, null,
                null, null, null);
        while (cur.moveToNext()) {
            n = new Notes();
            n.setmId(cur.getInt(cur.getColumnIndex(NotesDatabaseHelper.ID)));
            n.setmContent(cur.getString(cur
                    .getColumnIndex(NotesDatabaseHelper.CONTENT)));
            n.setmCreateTime(cur.getLong(cur
                    .getColumnIndex(NotesDatabaseHelper.CREATE_TIME)));
            n.setmAlarmTime(cur.getLong(cur
                    .getColumnIndex(NotesDatabaseHelper.ALARM_TIME)));
            notesList.add(n);
        }
        cur.close();
        return notesList;
    }
}
