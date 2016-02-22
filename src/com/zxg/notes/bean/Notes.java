package com.zxg.notes.bean;

/**
 * java bean Notes
 *
 * @author zxg
 *
 */
public class Notes {
    //便签Id
    private int mId;
    //便签内容
    private String mContent;
    //便签标题
    private String mTitle;
    //便签提醒时间
    private long mAlarmTime;
    //便签创建(修改)时间
    private long mCreateTime;
    //便签可见性
    private int mVisible;
    //get set方法
    public int getmVisible() {
        return mVisible;
    }

    public void setmVisible(int mVisible) {
        this.mVisible = mVisible;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public long getmAlarmTime() {
        return mAlarmTime;
    }

    public void setmAlarmTime(long mAlarmTime) {
        this.mAlarmTime = mAlarmTime;
    }

    public long getmCreateTime() {
        return mCreateTime;
    }

    public void setmCreateTime(long mCreateTime) {
        this.mCreateTime = mCreateTime;
    }

    @Override
    public String toString() {
        return "Notes Id:" + mId + "mTitle:" + mTitle + " Content:" + mContent
                + " CreateTime:" + mCreateTime + " AlarmTime:" + mAlarmTime;
    }

}
