package com.zxg.notes.presenter;
/**
 * Presenter:封装了NotesMainActivity中需要用到的一些方法
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.telephony.gsm.SmsManager;
import android.util.Log;

import com.zxg.notes.R;
import com.zxg.notes.bean.Notes;
import com.zxg.notes.database.NotesDAO;
import com.zxg.notes.interfaces.NotesListViewInterface;

public class NotesMainPresenter  {
    final static String TAG = "NotesMainPresenter";
    //定义字符串常量
    public static String MODE = "mode";
    public static String EDIT_MODE = "edit";
    public static String NEW_MODE = "new";
    public static String VISIBLE = "visible";
    public static String PRIVATE = "private";
    public static String PUBLIC = "public";
    private NotesListViewInterface notesListView;
    private List<Notes> notesList = new ArrayList<Notes>();
    private Context mContext;
    private NotesDAO notesDAO;

    public NotesMainPresenter(NotesListViewInterface notesListActivity,
            Context context) {
        notesListView = notesListActivity;
        mContext = context;
        Log.i(TAG, "mContext:" + context.toString());
        notesDAO = new NotesDAO(mContext);
    }
    //新建便签：调用notesListView接口中的toNotesEditActivityForNew方法
    //NotesMainActivity实现了该接口，也就是调用它的toNotesEditActivityForNew方法(MVP设计模式)
    public void newNotes() {
        notesListView.toNotesEditActivityForNew();
    }
    //初始化ListView，加载数据，根据参数visible不同来加载不同的数据
    public void initNotesListView(String visible) {
        notesList.clear();
        notesList = notesDAO.findAllNotes();
        Iterator<Notes> iter = notesList.iterator();
        //如果visible为private，删除visible为public的notes
        if (visible.equals(NotesMainPresenter.PRIVATE)) {
            while (iter.hasNext()) {
                Notes notes = iter.next();
                if (notes.getmVisible() == NotesEditPresenter.VISIBLE_PUBLIC) {
                    iter.remove();
                }
            }
        //如果visible为public，删除visible为private的notes
        } else if (visible.equals(NotesMainPresenter.PUBLIC)) {
            while (iter.hasNext()) {
                Notes notes = iter.next();
                if (notes.getmVisible() == NotesEditPresenter.VISIBLE_PRIVATE) {
                    iter.remove();
                }
            }
        }
        //调用接口的fillNotesListView方法，NotesMainActivity实现了该接口
        //所以这里调用的是NotesMainActivity的fillNotesListView方法
        notesListView.fillNotesListView(notesList);

    }
    //发送短信方法
    public void sendMessage(String phoneNum, int notesId) {
        Notes notes = notesDAO.findNotesById((long) notesId);
        String smsContent = mContext.getResources().getString(R.string.title)
                + notes.getmTitle() + "\n"
                + mContext.getResources().getString(R.string.content)
                + notes.getmContent();
        SmsManager smsManager = SmsManager.getDefault();
        if (smsContent.length() > 70) {
            List<String> contents = smsManager.divideMessage(smsContent);
            for (String sms : contents) {
                smsManager.sendTextMessage(phoneNum, null, sms, null, null);
            }
        } else {
            smsManager.sendTextMessage(phoneNum, null, smsContent, null, null);
        }
    }

    //编辑便签方法
    public void editNotes(long id) {
        notesListView.toNotesEditActivityForEdit(id);
    }

}
