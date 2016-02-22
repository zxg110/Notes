package com.zxg.notes.activity;

import java.util.Calendar;
import com.zxg.notes.R;
import com.zxg.notes.interfaces.NotesEditViewInterface;
import com.zxg.notes.presenter.NotesEditPresenter;
import com.zxg.notes.presenter.NotesMainPresenter;
import com.zxg.notes.util.AlarmUtil;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class NoteEditActivity extends Activity implements OnClickListener,
        NotesEditViewInterface {
    //界面左上角八叉按钮，用于返回列表界面
    private ImageButton notesEditBack;
    //界面右上角对勾按钮，用于保存便签或私密日记
    private ImageButton notesEditSave;
    //设置提醒按钮
    private Button alarmSet;
    //显示修改时间按钮
    private TextView notesEditTime;
    //便签内容文本框
    private EditText notesEditText;
    //删除便签按钮
    private Button deleteNotes;
    //当前界面便签的ID，初始值为-1
    private long currentNotesId = -1;
    //当前界面便签的提醒时间，初始值-1
    private long currentNotesAlarmTime = -1;
    //已经设置了提醒时间，再次点击设置提醒按钮弹出的删除界面
    private PopupWindow mPopWindow;
    //已经设置了提醒时间，再次点击设置提醒按钮弹出的删除菜单中的删除按钮
    private Button alarmDelete;
    // presenter notesEditPresenter中封装了该界面用到的方法(android MVP设计模式)
    private NotesEditPresenter notesEditPresenter;
    // add field:title 便签标题文本框
    private EditText notesTitleText;
    // AlarmUtil 该工具用于设置提醒时间
    AlarmUtil alarmUtil = null;
    // isPrivateNotes 判断当前界面是否为私密日记修改界面，默认为false
    private boolean isPrivateNotes = false;
    // handler
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case NotesEditPresenter.CONTEXT_IS_EMPTY:
                String toastContent = getResources().getString(
                        R.string.notes_empty_hint);
                Toast.makeText(NoteEditActivity.this, toastContent,
                        Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //加载布局
        setContentView(R.layout.notes_edit_layout);
        //创建notesEditPresenter对象
        notesEditPresenter = new NotesEditPresenter(this,
                getApplicationContext());
        //创建alarmUtil对象
        alarmUtil = new AlarmUtil(NoteEditActivity.this);
        //获取intent,根据intent中的数据来设置当前界面为私密日记修改界面还是便签修改界面
        Intent intent = getIntent();
        //intent中数据如果是密日记修改界面
        if (NotesMainPresenter.PRIVATE.equals(intent
                .getStringExtra(NotesMainPresenter.VISIBLE))) {
            //修改标记为true
            isPrivateNotes = true;

        }
        //初始化控件
        initView();
        //判断是修改界面还是新建
        if (NotesMainPresenter.EDIT_MODE.equals(intent
                .getStringExtra(NotesMainPresenter.MODE))) {
            //如果是修改界面，从intent中获取便签ID
            currentNotesId = intent.getLongExtra("notes_id", -1);
            Log.i("zxg", "currentNotesId" + currentNotesId);
            //显示删除便签按钮
            deleteNotes.setVisibility(View.VISIBLE);
            //根据Id加载该便签数据
            notesEditPresenter.initNotesData(currentNotesId);
        }

    }
    //初始化控件
    private void initView() {
        notesEditBack = (ImageButton) findViewById(R.id.notes_edit_back);
        notesEditSave = (ImageButton) findViewById(R.id.notes_edit_save);
        alarmSet = (Button) findViewById(R.id.alarm_set);
        deleteNotes = (Button) findViewById(R.id.delete_notes);
        deleteNotes.setOnClickListener(this);
        notesEditBack.setOnClickListener(this);
        notesEditSave.setOnClickListener(this);
        alarmSet.setOnClickListener(this);
        notesEditTime = (TextView) findViewById(R.id.notes_edit_time);
        notesEditText = (EditText) findViewById(R.id.notes_edit_text);
        // add field:title
        notesTitleText = (EditText) findViewById(R.id.notes_title_text);
        //初始化设置了便签提醒后再次点击设置提醒按钮所弹出的删除菜单
        initAlarmPopwnd();
        //判断当前界面是否为私密日记编辑界面，如果是调整UI
        if (isPrivateNotes == true) {
            //将设置提醒按钮设置为不显示
            alarmSet.setVisibility(View.GONE);
            //上方中央标题设置为私密日记
            notesEditTime.setText(getResources().getString(
                    R.string.new_private_notes));
            //删除按钮的Text设置为删除日记
            deleteNotes.setText(getResources().getString(R.string.delete_private_notes));
        }
    }
    //监听点击事件
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        //点击到保存按钮(界面上方右侧的对勾)
        case R.id.notes_edit_save:
            //判断标题和内容是否为空
            if (checkContentOrTitleIsEmpty()) {
                //如果是，弹出提示框
                showErrorToast(NotesEditPresenter.CONTEXT_IS_EMPTY);
            } else {
                //如果不是，保存便签或日记，关闭当前界面，返回列表界面
                notesEditPresenter.saveNotes();
                NoteEditActivity.this.finish();
            }
            break;
        //点击到返回界面(左上角八叉)
        case R.id.notes_edit_back:
            //返回便签或私密日记列表界面
            toNotesListView();
            //关闭当前界面
            NoteEditActivity.this.finish();
            break;
        //点击到删除按钮
        case R.id.delete_notes:
            //询问是否删除后删除便签
            makeSureDeleteNotes();
            break;
        //点击到设置提醒按钮
        case R.id.alarm_set:
            //如果当前便签提醒时间为-1
            if (currentNotesAlarmTime == -1) {
                //弹出时间选择框来设置提醒时间
                selectAlarmTimeFromDialog();
            } else {
                //如果不是-1.说明已设置过提醒时间，弹出删除菜单询问是否删除
                if (mPopWindow.isShowing()) {
                    mPopWindow.dismiss();
                } else {
                    popupWindowView.measure(MeasureSpec.UNSPECIFIED,
                            MeasureSpec.UNSPECIFIED);
                    int popupWidth = popupWindowView.getMeasuredWidth();
                    int popupHeight = popupWindowView.getMeasuredHeight();
                    int[] location = new int[2];
                    alarmSet.getLocationOnScreen(location);
                    mPopWindow.showAtLocation(alarmSet, Gravity.NO_GRAVITY,
                            (location[0] + v.getWidth() / 2) - popupWidth / 2,
                            location[1] - popupHeight);
                }
            }
            break;
        //点击到删除设置铃声按钮
        case R.id.popwnd_delete:
            //如果当前界面便签Id为-1，说明该界面为新建便签界面
            if (currentNotesId == -1) {
                //将铃声提醒时间设置为-1
                currentNotesAlarmTime = -1;
            } else {
                //若不是新建界面，则是修改界面，根据Id删除铃声提醒
                notesEditPresenter.deleteAlarm((int) currentNotesId,
                        currentNotesAlarmTime);
                currentNotesAlarmTime = -1;
            }
            if (mPopWindow.isShowing()) {
                mPopWindow.dismiss();
            }
            break;
        }

    }
    //删除便签或日记方法
    private void makeSureDeleteNotes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.delete_dialog_title));
        builder.setMessage(getResources().getString(
                R.string.delete_dialog_message));
        builder.setPositiveButton(R.string.sure,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        notesEditPresenter.deleteNote((int) currentNotesId);
                        NoteEditActivity.this.finish();
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
    //检查标题或内容是否为空方法
    private boolean checkContentOrTitleIsEmpty() {
        return TextUtils.isEmpty(notesEditText.getText())
                || TextUtils.isEmpty(notesTitleText.getText());
    }
    //弹出提示框方法
    @Override
    public void showErrorToast(int msg) {
        Message message = new Message();
        message.what = NotesEditPresenter.CONTEXT_IS_EMPTY;
        handler.sendMessage(message);

    }
    //实现接口方法：获取便签内容。该方法会在NotesEditPresenter调到
    @Override
    public String getNotesContent() {
        return notesEditText.getText().toString();
    }
    //实现接口方法：获取便签提醒时间。该方法会在NotesEditPresenter中调到
    @Override
    public long getNotesAlarmTime() {

        return currentNotesAlarmTime;
    }
    //实现接口方法：获取便签Id。该方法会在NotesEditPresenter中调到
    @Override
    public long getCurrentNotesId() {
        return currentNotesId;
    }
    //返回便签或日记列表界面
    @Override
    public void toNotesListView() {
        Intent intent = new Intent(NoteEditActivity.this,
                NotesMainActivity.class);
        //如果当前编辑的是私密日记，在intent中添加信息。列表界面会根据该信息来加载私密日记列表
        if (isPrivateNotes == true) {
            intent.putExtra(NotesMainPresenter.VISIBLE,
                    NotesMainPresenter.PRIVATE);
        }
        startActivity(intent);
    }
    //设置便签内容
    @Override
    public void setContentView(String content) {
        notesEditText.setText(content);
    }
    //设置便签修改时间
    @Override
    public void setTimeView(String time) {
        notesEditTime.setText(time);

    }
    //设置便签提醒时间方法
    public void selectAlarmTimeFromDialog() {
        View view = View.inflate(this, R.layout.date_time_picker, null);
        final DatePicker datePicker = (DatePicker) view
                .findViewById(R.id.date_picker);
        final TimePicker timePicker = (TimePicker) view
                .findViewById(R.id.time_picker);
        final Calendar calendar = Calendar.getInstance();
        timePicker.setIs24HourView(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.set_alarm_time));
        builder.setView(view);
        builder.setPositiveButton(R.string.sure,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        calendar.set(datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(),
                                timePicker.getCurrentMinute());

                        if (calendar.getTimeInMillis() < System
                                .currentTimeMillis()) {
                            Toast.makeText(
                                    NoteEditActivity.this,
                                    getResources().getString(
                                            R.string.alarm_time),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            currentNotesAlarmTime = calendar.getTimeInMillis();
                        }

                    }
                });

        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }
    //设置便签标题内容
    @Override
    public void setTitleView(String title) {
        notesTitleText.setText(title);

    }
    //实现接口方法：获取便签标题，该方法在NotesEditPresenter中会调到
    @Override
    public String getNotesTitle() {
        return notesTitleText.getText().toString();
    }
    //实现接口方法：获取便签提醒时间，该方法在NotesEditPresenter中会调到
    @Override
    public void setAlarmTime(long alarmTime) {
        currentNotesAlarmTime = alarmTime;
    }

    private View popupWindowView;
    //初始化删除提醒时间菜单方法
    private void initAlarmPopwnd() {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.alarm_delete_pop, null);
        popupWindowView = layout;
        mPopWindow = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setFocusable(true);
        alarmDelete = (Button) layout.findViewById(R.id.popwnd_delete);
        alarmDelete.setOnClickListener(this);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopWindow
                .setInputMethodMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.bg_transparent));
    }
    //实现接口方法：获取便签可见性：私密日记或便签，该方法在NotesEditPresenter中会调到
    @Override
    public int getVisible() {
        if (isPrivateNotes == true) {
            return NotesEditPresenter.VISIBLE_PRIVATE;
        } else {
            return NotesEditPresenter.VISIBLE_PUBLIC;
        }
    }

}
