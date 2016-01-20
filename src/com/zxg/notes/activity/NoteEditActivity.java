package com.zxg.notes.activity;

import java.util.Calendar;

import com.zxg.notes.R;
import com.zxg.notes.adapter.NotesAdapter;
import com.zxg.notes.interfaces.NotesEditViewInterface;
import com.zxg.notes.presenter.NotesEditPresenter;
import com.zxg.notes.presenter.NotesMainPresenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class NoteEditActivity extends Activity implements OnClickListener,
        NotesEditViewInterface {
    private ImageButton notesEditBack;
    private ImageButton notesEditSave;
    private Button alarmSet;
    private TextView notesEditTime;
    private EditText notesEditText;
    private Button deleteNotes;
    private long currentNotesId = -1;
    private long currentNotesAlarmTime = -1;
    // presenter
    private NotesEditPresenter notesEditPresenter;
    // add field:title
    private EditText notesTitleText;
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
            currentNotesId = intent.getLongExtra("notes_id", 0);
            deleteNotes.setVisibility(View.VISIBLE);
            notesEditPresenter.initNotesData(currentNotesId);
        }

    }

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
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        case R.id.notes_edit_save:
            if (checkContentOrTitleIsEmpty()) {
                showErrorToast(NotesEditPresenter.CONTEXT_IS_EMPTY);
            } else {
                notesEditPresenter.saveNotes();
                toNotesListView();
                NoteEditActivity.this.finish();
            }
            break;
        case R.id.notes_edit_back:
            toNotesListView();
            NoteEditActivity.this.finish();
            break;
        case R.id.delete_notes:
            makeSureDeleteNotes();
            break;
        case R.id.alarm_set:
            selectAlarmTimeFromDialog();
        }

    }

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
                        notesEditPresenter.deleteNote(currentNotesId);
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

    private boolean checkContentOrTitleIsEmpty() {
        return TextUtils.isEmpty(notesEditText.getText())
                || TextUtils.isEmpty(notesTitleText.getText());
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

        return currentNotesAlarmTime;
    }

    @Override
    public long getCurrentNotesId() {
        return currentNotesId;
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

    public void selectAlarmTimeFromDialog() {
        View view = View.inflate(this, R.layout.date_time_picker, null);
        final DatePicker datePicker = (DatePicker) view
                .findViewById(R.id.date_picker);
        final TimePicker timePicker = (TimePicker) view
                .findViewById(R.id.time_picker);
        final Calendar calendar = Calendar.getInstance();
        timePicker.setIs24HourView(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.delete_dialog_title));
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
                        currentNotesAlarmTime = calendar.getTimeInMillis();
                        dialog.dismiss();
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

    @Override
    public void setTitleView(String title) {
        notesTitleText.setText(title);

    }

    @Override
    public String getNotesTitle() {
        Log.i("111", "TitleText:" + notesTitleText.getText().toString());
        return notesTitleText.getText().toString();
    }

}
