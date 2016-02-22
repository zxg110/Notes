package com.zxg.notes.database;
/**
 * 创建数据库工具类
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class NotesDatabaseHelper extends SQLiteOpenHelper {

    // table name
    public static final String TB_NOTES = "notes";
    // table item name
    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String ALARM_TIME = "alarm_time";
    public static final String CREATE_TIME = "create_time";
    public static final String VISIBLE = "visible";

    // create table expression
    //建表语句
    private static final String CREATE_TB_NOTES = " create table " + TB_NOTES
            + " ( " + ID + " integer primary key autoincrement," + TITLE
            + " varchar," + CONTENT + " varchar," + VISIBLE + " integer,"
            + ALARM_TIME + " long," + CREATE_TIME + " long )";

    public NotesDatabaseHelper(Context context, String name,
            CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    //第一次创建数据时执行该方法，该方法中执行建表语句，进行建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TB_NOTES);

    }
    //数据库升级时使用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists" + TB_NOTES);
        onCreate(db);
    }

}
