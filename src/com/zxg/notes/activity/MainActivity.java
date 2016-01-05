package com.zxg.notes.activity;

import com.zxg.notes.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
/**
 * NotesListActivity
 * @author zxg
 *
 */
public class MainActivity extends Activity {
	//no notes
	private LinearLayout noNotesLayout;
	//备忘录列表
	private ListView notesList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	private void initView(){
		noNotesLayout = (LinearLayout)findViewById(R.id.head_view);
		notesList = (ListView)findViewById(R.id.notes_listview);
	}
}
