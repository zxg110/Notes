package com.zxg.notes.activity;
/**
 *便签提醒activity
 */
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.zxg.notes.R;
import com.zxg.notes.presenter.NotesMainPresenter;

public class AlarmAlertActivity extends Activity implements OnClickListener {
    //键盘管理器
    private KeyguardManager km;
    //键盘锁
    private KeyguardLock kl;
    //电源管理器
    private PowerManager pm;
    //唤醒锁
    private PowerManager.WakeLock wl;
    //便签Id
    private int notesId;
    //我知道了按钮
    private Button dialogKnown;
    //查看详情按钮
    private Button dialogDetail;
    //显示便签标题的TextView
    private TextView titleView;
    //播放的音乐
    private MediaPlayer alarmMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载布局
        setContentView(R.layout.alarm_laert);
        //初始化控件
        dialogKnown = (Button) findViewById(R.id.note_alarm_known);
        dialogDetail = (Button) findViewById(R.id.note_alarm_detail);
        titleView = (TextView) findViewById(R.id.note_alarm_title);
        dialogKnown.setOnClickListener(this);
        dialogDetail.setOnClickListener(this);
        //初始化管理器
        initManager();
        //唤醒屏幕
        wakeAndUnlockScreen();
        //创建音乐
        alarmMusic = new MediaPlayer();
        //播放音乐
        try {
            alarmMusic.setDataSource(this,
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            alarmMusic.prepare();
            alarmMusic.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = getIntent();
        //设置标题
        titleView.setText(intent.getStringExtra("alarmTitle"));
        notesId = intent.getIntExtra("noteId", 1);
    }

    private void initManager() {
        // 获取电源管理器对象
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        // 获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
        wl.setReferenceCounted(false);
        // 得到键盘管理器
        km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        // 得到键盘锁管理器对象
        kl = km.newKeyguardLock("unLock");
    }

    private void wakeAndUnlockScreen() {
        if (pm.isScreenOn()) {
            return;
        } else {
            // 点亮屏幕
            wl.acquire();
            // 解锁
            kl.disableKeyguard();
        }
    }
    //监听屏幕点击事件
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        //点击到：我知道了 按钮 结束该界面
        case R.id.note_alarm_known:
            alarmMusic.stop();
            finish();
            break;
        //点击到：查看详情按钮：打开该便签的编辑界面
        case R.id.note_alarm_detail:
            Intent intent = new Intent(AlarmAlertActivity.this,
                    NoteEditActivity.class);
            intent.putExtra(NotesMainPresenter.MODE,
                    NotesMainPresenter.EDIT_MODE);
            intent.putExtra("notes_id", (long)notesId);
            startActivity(intent);
            alarmMusic.stop();
            finish();
        default:
            break;
        }

    }

}
