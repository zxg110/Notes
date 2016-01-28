package com.zxg.notes.activity;

import java.util.List;

import com.zxg.notes.R;
import com.zxg.notes.adapter.NotesAdapter;
import com.zxg.notes.bean.Notes;
import com.zxg.notes.interfaces.NotesListViewInterface;
import com.zxg.notes.presenter.NotesMainPresenter;

import android.os.Bundle;
import android.app.Activity;

import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

/**
 * NotesListActivity
 *
 * @author zxg
 */
public class NotesMainActivity extends Activity implements
        NotesListViewInterface, OnClickListener {
    final static String TAG = "NotesMainActivity";
    // no notes
    private LinearLayout noNotesLayout;
    // 备忘录列表
    private ListView notesListView;
    private Button newNotes;
    // presenter
    private NotesMainPresenter notesMainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        notesMainPresenter = new NotesMainPresenter(this,
                getApplicationContext());
        setContentView(R.layout.activity_main);
        initView();
        notesMainPresenter.initNotesListView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void initView() {
        noNotesLayout = (LinearLayout) findViewById(R.id.head_view);
        notesListView = (ListView) findViewById(R.id.notes_listview);
        newNotes = (Button) findViewById(R.id.new_notes);
        newNotes.setOnClickListener(this);
        notesListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                    int position, long id) {
                Log.i(TAG, "notesId+++:" + id + " position:" + position);
                notesMainPresenter.editNotes(id);

            }
        });
    }

    @Override
    public void showNoNotesImage(boolean isShow) {
        if (isShow) {
            noNotesLayout.setVisibility(View.VISIBLE);
        } else if (!isShow) {
            noNotesLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        case R.id.new_notes:
            notesMainPresenter.newNotes();
        }

    }

    @Override
    public void toNotesEditActivityForNew() {
        Intent intent = new Intent(NotesMainActivity.this,
                NoteEditActivity.class);
        intent.putExtra(NotesMainPresenter.MODE, NotesMainPresenter.NEW_MODE);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    @Override
    public void toNotesEditActivityForEdit(long id) {
        Intent intent = new Intent(NotesMainActivity.this,
                NoteEditActivity.class);
        intent.putExtra(NotesMainPresenter.MODE, NotesMainPresenter.EDIT_MODE);
        intent.putExtra("notes_id", id);
        startActivity(intent);
    }

    @Override
    public void fillNotesListView(List<Notes> notesList) {
        NotesAdapter notesAdapter = new NotesAdapter(notesList,
                getApplicationContext());
        notesListView.setAdapter(notesAdapter);
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
