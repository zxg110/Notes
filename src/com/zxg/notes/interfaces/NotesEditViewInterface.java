package com.zxg.notes.interfaces;

public interface NotesEditViewInterface {
       boolean checkContentIsEmpty();
       void showErrorToast(String msg);
       String getNotesContent();
       long getNotesAlarmTime();
       int getCurrentNotesId();
       void toNotesListView();
       
}
