package com.zxg.notes.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class NotesDatabaseHelper extends SQLiteOpenHelper {
    // database name and version
    private static final String DB_NAME = "notesdata.db";
    private static final int DB_VERSION = 1;

    // table name
    public static final String TB_NOTES = "notes";
    // table item name
    public static final String ID = "_id";
    public static final String CONTENT = "content";
    public static final String ALARM_TIME = "alarm_time";
    public static final String CREATE_TIME = "create_time";

    // create table expression
    private static final String CREATE_TB_NOTES = " create table " + TB_NOTES
            + " ( " + ID + " integer primary key autoincrement," + CONTENT
            + " varchar," + ALARM_TIME + " long," + CREATE_TIME + " long )";

    public NotesDatabaseHelper(Context context, String name,
            CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TB_NOTES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists" + TB_NOTES);
        onCreate(db);
    }

}
