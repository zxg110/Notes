package com.zxg.notes.activity;

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
    private KeyguardManager km;
    private KeyguardLock kl;
    private PowerManager pm;
    private PowerManager.WakeLock wl;
    private int notesId;
    private Button dialogKnown;
    private Button dialogDetail;
    private TextView titleView;
    private MediaPlayer alarmMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_laert);
        dialogKnown = (Button) findViewById(R.id.note_alarm_known);
        dialogDetail = (Button) findViewById(R.id.note_alarm_detail);
        titleView = (TextView) findViewById(R.id.note_alarm_title);
        dialogKnown.setOnClickListener(this);
        dialogDetail.setOnClickListener(this);
        initManager();
        wakeAndUnlockScreen();
        alarmMusic = new MediaPlayer();
        try {
            alarmMusic.setDataSource(this,
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            alarmMusic.prepare();
            alarmMusic.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = getIntent();
        titleView.setText(intent.getStringExtra("alarmTitle"));
        notesId = intent.getIntExtra("alarmid", 1);
        Log.i("zxg", "AlarmAlertActivity onCreated...");
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        case R.id.note_alarm_known:
            alarmMusic.stop();
            finish();
            break;
        case R.id.note_alarm_detail:
            Intent intent = new Intent(AlarmAlertActivity.this,
                    NoteEditActivity.class);
            intent.putExtra(NotesMainPresenter.MODE,
                    NotesMainPresenter.EDIT_MODE);
            intent.putExtra("notes_id", notesId);
            startActivity(intent);
            alarmMusic.stop();
            finish();
        default:
            break;
        }

    }

}
