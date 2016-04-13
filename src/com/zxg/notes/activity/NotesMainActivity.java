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
    //新建便签按钮
    private Button newNotes;
    // 修改密码按钮
    private Button modifyPassword;
    //多功能便签textView
    private TextView appName;
    //私密空间textView
    private TextView privateSpace;
    // presenter
    private NotesMainPresenter notesMainPresenter;
    public static final int CONTACT_REQUSET = 10;
    //短信分享号码
    private String phoneNum;
    //短信分享联系人名字
    private String contactName;
    //短信分享便签的ID
    private int shareNoteId = -1;
    //用于记录当前activity是否为私密空间界面
    private boolean isPrivateNotesList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏activity上方的标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //创建Presenter，Presenter中封装了该页面需要用到的方法
        notesMainPresenter = new NotesMainPresenter(this,
                getApplicationContext());
        //加载布局文件
        setContentView(R.layout.activity_main);
        //初始化私密空间密码方法，密码存放在sharePreference中
        initPrivatePassword();
        //初始化组件
        initView();
        //获得intent
        Intent intent = getIntent();
        //根据intent中携带的数据来设置该页面是否为私密空间页面
        //如果是私密空间
        if (NotesMainPresenter.PRIVATE.equals(intent
                .getStringExtra(NotesMainPresenter.VISIBLE))) {
            //设置isPrivateNotesList为true
            isPrivateNotesList = true;
            //加载私密日记数据
            notesMainPresenter.initNotesListView(NotesMainPresenter.PRIVATE);
            //更新私密日记UI
            initPrivateUI();
        } else {
            //如果不是私密空间，加载便签数据
            notesMainPresenter.initNotesListView(NotesMainPresenter.PUBLIC);
        }
    }

    SharedPreferences preferences;
    SharedPreferences.Editor editer;
    //初始化私密空间密码防范，初始密码为123456
    private void initPrivatePassword() {
        preferences = getSharedPreferences("passwordPref", MODE_PRIVATE);
        editer = preferences.edit();
        String password = preferences.getString("password", null);
        if (password == null) {
            editer.putString("password", "123456");
            editer.commit();
        }
    }
    //更新私密空寂UI
    private void initPrivateUI() {
        //将appName这个textView的text从多功能便签改为私密日记
        appName.setText(getResources().getString(R.string.private_notes));
        //将newNotes按钮上的text从新建便签改为新建日记
        newNotes.setText(getResources().getString(R.string.new_private_notes));
        //设置修改密码按钮可见
        modifyPassword.setVisibility(View.VISIBLE);
        //设置privateSpace的Text为返回便签
        privateSpace.setText(getResources().getString(R.string.back_notes));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //初始化控件
    private void initView() {
        noNotesLayout = (LinearLayout) findViewById(R.id.head_view);
        notesListView = (ListView) findViewById(R.id.notes_listview);
        newNotes = (Button) findViewById(R.id.new_notes);
        appName = (TextView) findViewById(R.id.title_name_tv);
        modifyPassword = (Button) findViewById(R.id.modify_password);
        newNotes.setOnClickListener(this);
        privateSpace = (TextView)findViewById(R.id.private_text);
        privateSpace.setOnClickListener(this);
        modifyPassword.setOnClickListener(this);
        //为ListView设置点击监听器
        notesListView.setOnItemClickListener(new OnItemClickListener() {
            //获取被点击项的Id
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                    int position, long id) {
                Log.i(TAG, "notesId+++:" + id + " position:" + position);
                //调用notesMainPresenter的editNotes方法，进入修改页面
                notesMainPresenter.editNotes(id);
                //关闭当前页面
                finish();
            }
        });
        //为listView设置长按监听，长按弹出菜单
        notesListView
                .setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v,
                            ContextMenuInfo menuInfo) {
                        menu.setHeaderTitle(R.string.select_operate);
                        //为菜单添加一项：短信分享
                        menu.add(0, 0, 0, R.string.message_share);
                    }
                });
    }
    //为菜单添加点击监听器
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        // 得到notesId
        shareNoteId = Integer.valueOf((int) info.id);
        switch (item.getItemId()) {
        //如果点击了短信分享菜单，进入选择联系人界面
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
    //用于接收联系人界面选择的联系人信息
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver resol = getContentResolver();
        if (resultCode == Activity.RESULT_OK) {
            //获取选择的联系人Uri
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
            }
        }
        //弹出发送给好友弹框
        showSendMessageDialog();
    }
    //弹出发送给好友弹框方法
    private void showSendMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.send_message_confirm));
        builder.setMessage(getResources().getString(
                R.string.send_message_confirm)
                + contactName + "?");
        builder.setPositiveButton(R.string.send,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //调用notesMainPresenter的sendMessage方法发送短信
                        notesMainPresenter.sendMessage(phoneNum, shareNoteId);
                        //刷新界面
                        if (isPrivateNotesList == true) {
                            //如果当前界面为私密日记界面，加载私密日记数据
                            notesMainPresenter
                                    .initNotesListView(NotesMainPresenter.PRIVATE);
                        } else {
                            //如果不是私密日记假面，加载便签数据
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


    //onClick方法，用于监听按钮点击事件
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        //如果点击新建便签按钮，调用notesMainPresenter的newNotes()方法
        case R.id.new_notes:
            notesMainPresenter.newNotes();
            finish();
            break;
        //如果点击私密空间
        case R.id.private_text:
            //如果当前不是私密空间界面，进入私密空间界面
            if (isPrivateNotesList == false) {
                toPrivateNotesList();
            } else if (isPrivateNotesList == true) {
                //如果是，返回便签界面
                toPublicNotesList();
            }
            break;
        case R.id.modify_password:
            modifyPassword();
            break;
        }

    }
    //修改私密日记密码的方法
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
                        //获取输入的旧密码
                        String oldPwdString = oldPwd.getText().toString()
                                .trim();
                        //获取第一次输入的新密码
                        String newPwdString = newPwd.getText().toString()
                                .trim();
                        //获取第二次输入的新密码
                        String newPwdAgainString = newPwdAgain.getText()
                                .toString().trim();
                        //如果输入的旧密码与preference中保存的密码相同并且两次输入的新密码相同，修改密码
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
    //进入便签界面的方法
    private void toPublicNotesList() {
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
    //进入私密空间界面方法
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
                        //获取文本框中的密码
                        String pwd = password.getText().toString().trim();
                        //如果获取密码与preferences保存的密码相同进入界面
                        if (preferences.getString("password", "default")
                                .equals(pwd)) {
                            Intent intent = new Intent(NotesMainActivity.this,
                                    NotesMainActivity.class);
                            intent.putExtra(NotesMainPresenter.VISIBLE,
                                    NotesMainPresenter.PRIVATE);
                            //finish();
                            startActivity(intent);
                            //finish();
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
    //进入新建便签界面
    @Override
    public void toNotesEditActivityForNew() {
        Intent intent = new Intent(NotesMainActivity.this,
                NoteEditActivity.class);
        intent.putExtra(NotesMainPresenter.MODE, NotesMainPresenter.NEW_MODE);
        if (isPrivateNotesList == true) {
            intent.putExtra(NotesMainPresenter.VISIBLE,
                    NotesMainPresenter.PRIVATE);
        }
        startActivity(intent);
    }

    //携带便签Id进入修改便签界面，与toNotesEditActivityForNew方法进入的是一个activity
    //NotesEditActivity根据intent携带的数据不同判断是修改还是新建，若修改，加载出该便签的数据
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
    //填充listview数据方法
    @Override
    public void fillNotesListView(List<Notes> notesList) {
        NotesAdapter notesAdapter = new NotesAdapter(notesList,
                getApplicationContext());
        notesListView.setAdapter(notesAdapter);
    }
    private long exitTime = 0;
    //监听设备后退键，实现再按一次退出程序功能
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
