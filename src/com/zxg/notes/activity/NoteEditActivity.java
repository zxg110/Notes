package com.zxg.notes.activity;

import com.zxg.notes.R;
import com.zxg.notes.interfaces.NotesEditViewInterface;
import com.zxg.notes.presenter.NotesEditPresenter;
import com.zxg.notes.presenter.NotesMainPresenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class NoteEditActivity extends Activity implements OnClickListener,
        NotesEditViewInterface {
    private ImageButton notesEditBack;
    private ImageButton notesEditSave;
    private Button alarmSet;
    private TextView notesEditTime;
    private EditText notesEditText;
    // presenter
    private NotesEditPresenter notesEditPresenter;
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
        setContentView(R.layout.notes_edit_layout);
        notesEditPresenter = new NotesEditPresenter(this,
                getApplicationContext());
        initView();
        Intent intent = getIntent();
        if (NotesMainPresenter.EDIT_MODE.equals(intent
                .getStringExtra(NotesMainPresenter.MODE))) {
            long notesId = intent.getLongExtra("notes_id", 0);
            notesEditPresenter.initNotesData(notesId);
        }

    }

    private void initView() {
        notesEditBack = (ImageButton) findViewById(R.id.notes_edit_back);
        notesEditSave = (ImageButton) findViewById(R.id.notes_edit_save);
        alarmSet = (Button) findViewById(R.id.alarm_set);
        notesEditBack.setOnClickListener(this);
        notesEditSave.setOnClickListener(this);
        alarmSet.setOnClickListener(this);
        notesEditTime = (TextView) findViewById(R.id.notes_edit_time);
        notesEditText = (EditText) findViewById(R.id.notes_edit_text);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        case R.id.notes_edit_save:
            notesEditPresenter.saveNotes();
        case R.id.notes_edit_back:
            NoteEditActivity.this.finish();
        }

    }

    @Override
    public boolean checkContentIsEmpty() {
        return TextUtils.isEmpty(notesEditText.getText());
    }

    @Override
    public void showErrorToast(int msg) {
        Message message = new Message();
        message.what = NotesEditPresenter.CONTEXT_IS_EMPTY;
        handler.sendMessage(message);

    }

    @Override
    public String getNotesContent() {
        return notesEditText.getText().toString();
    }

    @Override
    public long getNotesAlarmTime() {
        // TODO Auto-generated method stub
        return 111100010;
    }

    @Override
    public int getCurrentNotesId() {
        // TODO Auto-generated method stub
        return -1;
    }

    @Override
    public void toNotesListView() {
        Intent intent = new Intent(NoteEditActivity.this,
                NotesMainActivity.class);
        startActivity(intent);
    }

    @Override
    public void setContentView(String content) {
        notesEditText.setText(content);

    }

    @Override
    public void setTimeView(String time) {
        notesEditTime.setText(time);

    }
}
