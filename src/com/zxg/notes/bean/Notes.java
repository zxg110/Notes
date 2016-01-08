package com.zxg.notes.bean;

/**
 * java bean Notes
 * 
 * @author zxg
 * 
 */
public class Notes {
    private int mId;
    private String mContent;
    private long mAlarmTime;
    private long mCreateTime;

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
        return "Notes Id:" + mId + " Content:" + mContent + " CreateTime:"
                + mCreateTime + " AlarmTime:" + mAlarmTime;
    }

}
