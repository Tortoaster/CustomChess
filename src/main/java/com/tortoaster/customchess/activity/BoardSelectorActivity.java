package com.tortoaster.customchess.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.chess.Board;
import com.tortoaster.customchess.view.FileAdapter;
import com.tortoaster.customchess.view.SwipeToDeleteCallback;

import java.io.File;

public class BoardSelectorActivity extends AppCompatActivity implements FileAdapter.FileListener {
	
	private static final int NEW_BOARD = 1, EDIT_BOARD = 2;
	
	private boolean resultExpected;
	
	private String lastEdited;
	
	private FileAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_board_selector);
		
		RecyclerView boards = findViewById(R.id.boards);
		
		RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
		
		resultExpected = getIntent().getBooleanExtra("resultExpected", false);
		adapter = new FileAdapter(getFilesDir().listFiles(), Board.PREFIX, Board.SUFFIX, this);
		
		boards.setLayoutManager(manager);
		boards.setItemAnimator(new DefaultItemAnimator());
		boards.setAdapter(adapter);
		
		new ItemTouchHelper(new SwipeToDeleteCallback(this, adapter)).attachToRecyclerView(boards);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(resultCode == RESULT_OK) {
			switch(requestCode) {
				case NEW_BOARD:
					adapter.add(new File(getFilesDir() + File.separator + "b_" + intent.getStringExtra("name") + ".txt"));
					break;
				case EDIT_BOARD:
					String name = intent.getStringExtra("name");
					if(!lastEdited.equals(name)) adapter.replace(new File(getFilesDir() + File.separator + "b_" + lastEdited + ".txt"), new File(getFilesDir() + File.separator + "b_" + name + ".txt"));
			}
		}
	}
	
	@Override
	public void fileSelected(File file, String prefix, String suffix) {
		String name = file.getName();
		
		name = name.substring(prefix.length(), name.length() - suffix.length());
		
		if(resultExpected) {
			Intent intent = new Intent();
			intent.putExtra("result", name);
			
			setResult(RESULT_OK, intent);
			finish();
		} else {
			loadBoard(name);
		}
	}
	
	@Override
	public void fileRemoved(File file, String prefix, String suffix) {
	}
	
	public void loadBoard(String name) {
		lastEdited = name;
		
		Intent intent = new Intent(this, BoardEditorActivity.class);
		intent.putExtra("name", name);
		startActivityForResult(intent, EDIT_BOARD);
	}
	
	public void newBoard(View view) {
		Intent intent = new Intent(this, BoardEditorActivity.class);
		startActivityForResult(intent, NEW_BOARD);
	}
}
