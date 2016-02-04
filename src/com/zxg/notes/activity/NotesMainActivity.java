package com.zxg.notes.activity;

import java.util.List;

import com.zxg.notes.R;
import com.zxg.notes.adapter.NotesAdapter;
import com.zxg.notes.bean.Notes;
import com.zxg.notes.interfaces.NotesListViewInterface;
import com.zxg.notes.presenter.NotesMainPresenter;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
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
    public static final int CONTACT_REQUSET = 10;
    // share phone number
    private String phoneNum;
    // share contact name
    private String contactName;
    // shareNotes Id
    private int shareNoteId = -1;

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
                finish();
            }
        });
        notesListView
                .setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v,
                            ContextMenuInfo menuInfo) {
                        menu.setHeaderTitle(R.string.select_operate);
                        menu.add(0, 0, 0, R.string.message_share);
                        menu.add(0, 1, 0, R.string.delete_notes);
                    }
                });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        // 得到notesId
        shareNoteId = Integer.valueOf((int) info.id);
        switch (item.getItemId()) {
        case 0:
            Intent intent = new Intent(Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, CONTACT_REQUSET);
            break;
        case 1:
            break;
        default:
            break;
        }
        return super.onContextItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("zxg", "onActivityResult:");
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver resol = getContentResolver();
        if (resultCode == Activity.RESULT_OK) {

            Uri contactData = data.getData();
            Cursor c = managedQuery(contactData, null, null, null, null);
            c.moveToFirst();
            contactName = c.getString(c
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            // 取得联系人id,每个条目都有一个唯一的id(主键)
            String contactId = c.getString(c
                    .getColumnIndex(ContactsContract.Contacts._ID));
            // 取得联系人的号码
            Cursor phone = resol.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
                            + contactId, null, null);
            while (phone.moveToNext()) {
                phoneNum = phone
                        .getString(phone
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.i("zxg", "phoneNum:" + phoneNum);
            }
        }
        showSendMessageDialog();
    }

    private void showSendMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.delete_dialog_title));
        builder.setMessage(getResources().getString(
                R.string.send_message_confirm)
                + contactName + "?");
        builder.setPositiveButton(R.string.send,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notesMainPresenter.sendMessage(phoneNum, shareNoteId);
                        notesMainPresenter.initNotesListView();
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
        builder.create().show();
    }

    @Override
    public void showNoNotesImage(boolean isShow) {
        if (isShow) {
            noNotesLayout.setVisibility(View.INVISIBLE);
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
            finish();
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
        // finish();
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
