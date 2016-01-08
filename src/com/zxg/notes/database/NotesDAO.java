package com.zxg.notes.database;

import java.util.ArrayList;
import java.util.List;

import com.zxg.notes.bean.Notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NotesDAO {
    final static String TAG = "NotesDAO";
    private NotesDatabaseHelper dbHelper = null;
    private SQLiteDatabase db = null;
    private ContentValues values;
    private List<Notes> notesList;

    public NotesDAO(Context context) {
        Log.i(TAG, "NotesDAO Context:" + context.toString());
        dbHelper = new NotesDatabaseHelper(context, "Notes.db", null, 1);
        Log.i(TAG, "dbHelper:" + dbHelper.toString());
        db = dbHelper.getWritableDatabase();
        Log.i(TAG, "db:" + db.toString());
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
        db.delete(NotesDatabaseHelper.TB_NOTES, NotesDatabaseHelper.ID
                + " = ? ", new String[] { id + "" });
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
                null, null, NotesDatabaseHelper.CREATE_TIME + " desc");
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

    public Notes findNotesById(long id) {
        notesList.clear();
        Notes n = null;
        Cursor cur = db.query(NotesDatabaseHelper.TB_NOTES, null,
                NotesDatabaseHelper.ID + " = ?", new String[] { id + "" },
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
        return notesList.get(0);
    }
}
