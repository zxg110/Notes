package com.zxg.notes.interfaces;

public interface NotesEditViewInterface {

    void showErrorToast(int msg);

    String getNotesContent();

    long getNotesAlarmTime();

    long getCurrentNotesId();

    void toNotesListView();

    void setContentView(String content);

    void setTimeView(String time);

    // add field:title
    void setTitleView(String title);

    String getNotesTitle();

    void setAlarmTime(long alarmTime);
}
