package com.zxg.notes.activity;

import com.zxg.notes.R;
import com.zxg.notes.interfaces.NotesEditViewInterface;
import com.zxg.notes.presenter.NotesEditPresenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
    private String mode;
    // presenter
    private NotesEditPresenter notesEditPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.notes_edit_layout);
        notesEditPresenter = new NotesEditPresenter(this,
                getApplicationContext());
        initView();
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
        }

    }

    @Override
    public boolean checkContentIsEmpty() {
        return TextUtils.isEmpty(notesEditText.getText());
    }

    @SuppressLint("ShowToast")
    @Override
    public void showErrorToast(String msg) {
        String toastContent = null;
        if (NotesEditPresenter.CONTEXT_IS_EMPTY.equals(msg)) {
            toastContent = getResources().getString(R.string.notes_empty_hint);
        }

        Toast.makeText(this, toastContent, Toast.LENGTH_SHORT);
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
}
