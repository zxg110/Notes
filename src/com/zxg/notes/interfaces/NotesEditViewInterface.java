package com.zxg.notes.interfaces;
/**
 * 编辑界面接口，该接口定义了编辑界面需要实现的方法，NotesEditActivity
 * 实现该接口
 * @author zxg
 *
 */
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

    // add field:visible

    int getVisible();

}
