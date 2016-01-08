package com.zxg.notes.interfaces;

public interface NotesEditViewInterface {
    boolean checkContentIsEmpty();

    void showErrorToast(int msg);

    String getNotesContent();

    long getNotesAlarmTime();

    int getCurrentNotesId();

    void toNotesListView();

    void setContentView(String content);

    void setTimeView(String time);

}
