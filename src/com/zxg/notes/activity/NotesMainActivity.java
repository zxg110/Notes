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
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
    // 修改密码按钮
    private Button modifyPassword;
    private TextView appName;
    // presenter
    private NotesMainPresenter notesMainPresenter;
    public static final int CONTACT_REQUSET = 10;
    // share phone number
    private String phoneNum;
    // share contact name
    private String contactName;
    // shareNotes Id
    private int shareNoteId = -1;
    private boolean isPrivateNotesList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        notesMainPresenter = new NotesMainPresenter(this,
                getApplicationContext());
        setContentView(R.layout.activity_main);
        initPrivatePassword();
        initView();
        Intent intent = getIntent();
        if (NotesMainPresenter.PRIVATE.equals(intent
                .getStringExtra(NotesMainPresenter.VISIBLE))) {
            isPrivateNotesList = true;
            notesMainPresenter.initNotesListView(NotesMainPresenter.PRIVATE);
            initPrivateUI();
            Log.i("zxg", "private notes list onCreate...");
        } else {
            notesMainPresenter.initNotesListView(NotesMainPresenter.PUBLIC);
        }
        Log.i("zxg", "isPrivate:" + isPrivateNotesList);
    }

    SharedPreferences preferences;
    SharedPreferences.Editor editer;

    private void initPrivatePassword() {
        preferences = getSharedPreferences("passwordPref", MODE_PRIVATE);
        editer = preferences.edit();
        String password = preferences.getString("password", null);
        if (password == null) {
            editer.putString("password", "123456");
            editer.commit();
        }
        Log.i("zxg", "pref:" + preferences.getString("password", "none"));
    }

    private void initPrivateUI() {
        Log.i("zxg", "update private UI");
        appName.setText(getResources().getString(R.string.private_notes));
        newNotes.setText(getResources().getString(R.string.new_private_notes));
        modifyPassword.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void initView() {
        noNotesLayout = (LinearLayout) findViewById(R.id.head_view);
        notesListView = (ListView) findViewById(R.id.notes_listview);
        newNotes = (Button) findViewById(R.id.new_notes);
        appName = (TextView) findViewById(R.id.title_name_tv);
        modifyPassword = (Button) findViewById(R.id.modify_password);
        newNotes.setOnClickListener(this);
        appName.setOnClickListener(this);
        modifyPassword.setOnClickListener(this);
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
                        if (isPrivateNotesList == true) {
                            notesMainPresenter
                                    .initNotesListView(NotesMainPresenter.PRIVATE);
                        } else {
                            notesMainPresenter
                                    .initNotesListView(NotesMainPresenter.PUBLIC);
                        }
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
            break;
        case R.id.title_name_tv:
            if (isPrivateNotesList == false) {
                toPrivateNotesList();
            } else if (isPrivateNotesList == true) {
                toPublicNotesList();
            }
            break;
        case R.id.modify_password:
            modifyPassword();
            break;
        }

    }

    private void modifyPassword() {
        View view = View.inflate(this, R.layout.modify_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.modify_password));
        builder.setView(view);
        final EditText oldPwd = (EditText) view.findViewById(R.id.old_pwd);
        final EditText newPwd = (EditText) view.findViewById(R.id.new_pwd);
        final EditText newPwdAgain = (EditText) view
                .findViewById(R.id.new_pwd_again);
        builder.setPositiveButton(R.string.sure,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String oldPwdString = oldPwd.getText().toString()
                                .trim();
                        String newPwdString = newPwd.getText().toString()
                                .trim();
                        String newPwdAgainString = newPwdAgain.getText()
                                .toString().trim();
                        if (preferences.getString("password", "default")
                                .equals(oldPwdString)
                                && newPwdString.equals(newPwdAgainString)) {
                            editer.putString("password", newPwdString);
                            editer.commit();
                            Toast.makeText(
                                    getApplicationContext(),
                                    getResources().getString(
                                            R.string.modify_password_success),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    getResources().getString(
                                            R.string.modify_password_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    private void toPublicNotesList() {
        Log.i("zxg", "toPublicNotesList");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.private_notes));
        builder.setMessage(R.string.return_notes);
        builder.setPositiveButton(R.string.sure,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(NotesMainActivity.this,
                                NotesMainActivity.class);
                        finish();
                        startActivity(intent);

                        Log.i("zxg", "to public notes list");
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    private void toPrivateNotesList() {
        View view = View.inflate(this, R.layout.private_notes_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.private_notes));
        builder.setView(view);
        final EditText password = (EditText) view.findViewById(R.id.password);
        builder.setPositiveButton(R.string.sure,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pwd = password.getText().toString().trim();
                        if (preferences.getString("password", "default")
                                .equals(pwd)) {
                            Intent intent = new Intent(NotesMainActivity.this,
                                    NotesMainActivity.class);
                            intent.putExtra(NotesMainPresenter.VISIBLE,
                                    NotesMainPresenter.PRIVATE);
                            finish();
                            startActivity(intent);
                            Log.i("zxg", "to private notes list");
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    getResources()
                                            .getString(R.string.pwd_wrong),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    @Override
    public void toNotesEditActivityForNew() {
        Intent intent = new Intent(NotesMainActivity.this,
                NoteEditActivity.class);
        intent.putExtra(NotesMainPresenter.MODE, NotesMainPresenter.NEW_MODE);
        if (isPrivateNotesList == true) {
            Log.i("zxg", "new private notes");
            intent.putExtra(NotesMainPresenter.VISIBLE,
                    NotesMainPresenter.PRIVATE);
        }
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
        if (isPrivateNotesList == true) {
            intent.putExtra(NotesMainPresenter.VISIBLE,
                    NotesMainPresenter.PRIVATE);
        }
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
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.exit),
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
