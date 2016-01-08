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
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * NotesListActivity
 *
 * @author zxg
 */
public class NotesMainActivity extends Activity implements
        NotesListViewInterface, OnClickListener {
    // no notes
    private LinearLayout noNotesLayout;
    // 备忘录列表
    private ListView notesListView;
    private Button newNotes;
    private Button deleteNotes;
    private Button toDeleteNotes;
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
        toDeleteNotes = (Button) findViewById(R.id.to_delete_notes);
        deleteNotes = (Button) findViewById(R.id.delete_notes);
        newNotes.setOnClickListener(this);
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
        case R.id.to_delete_notes:

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
    public void toNotesEditActivityForEdit() {
        Intent intent = new Intent(NotesMainActivity.this,
                NoteEditActivity.class);
        intent.putExtra(NotesMainPresenter.MODE, NotesMainPresenter.EDIT_MODE);
        startActivity(intent);
    }

    @Override
    public void fillNotesListView(List<Notes> notesList) {
        NotesAdapter notesAdapter = new NotesAdapter(notesList,
                getApplicationContext());
        notesListView.setAdapter(notesAdapter);
    }

}
