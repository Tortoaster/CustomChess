package com.tortoaster.customchess.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.view.FileAdapter;
import com.tortoaster.customchess.view.SwipeToDeleteCallback;

import java.io.File;

public class BoardSelectorActivity extends AppCompatActivity implements Button.OnClickListener {
	
	private static final int NEW_BOARD = 1, EDIT_BOARD = 2;
	
	private boolean resultExpected;
	
	private String lastLoaded;
	
	private FileAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_board_selector);
		
		RecyclerView boards = findViewById(R.id.boards);
		
		RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
		
		resultExpected = getIntent().getBooleanExtra("resultExpected", false);
		adapter = new FileAdapter(getFilesDir().listFiles(), "b_", ".txt", this);
		
		boards.setLayoutManager(manager);
		boards.setItemAnimator(new DefaultItemAnimator());
		boards.setAdapter(adapter);
		
		new ItemTouchHelper(new SwipeToDeleteCallback(adapter)).attachToRecyclerView(boards);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(resultCode == RESULT_OK) {
			switch(requestCode) {
				case NEW_BOARD:
				case EDIT_BOARD:
					adapter.add(new File(getFilesDir() + File.separator + "b_" + intent.getStringExtra("name") + ".txt"));
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		String name = ((Button) v).getText().toString();
		
		if(resultExpected) {
			Intent intent = new Intent();
			intent.putExtra("result", name);
			
			setResult(Activity.RESULT_OK, intent);
			finish();
		} else {
			loadBoard(name);
		}
	}
	
	public void loadBoard(String name) {
		lastLoaded = name;
		
		Intent intent = new Intent(this, BoardEditorActivity.class);
		intent.putExtra("name", name);
		startActivity(intent);
	}
	
	public void newBoard(View view) {
		Intent intent = new Intent(this, BoardEditorActivity.class);
		startActivityForResult(intent, NEW_BOARD);
	}
}
