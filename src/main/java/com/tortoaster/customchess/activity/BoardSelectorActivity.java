package com.tortoaster.customchess.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.view.ButtonAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BoardSelectorActivity extends AppCompatActivity implements Button.OnClickListener {
	
	private static final int NEW_BOARD = 1, EDIT_BOARD = 2;
	
	private boolean resultExpected;
	
	private String lastLoaded;
	
	private List<String> files;
	
	private RecyclerView.Adapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_board_selector);
		
		RecyclerView boards = findViewById(R.id.boards);
		
		resultExpected = getIntent().getBooleanExtra("resultExpected", false);
		
		files = new ArrayList<>();
		adapter = new ButtonAdapter(files, this);
		
		for(File file: getFilesDir().listFiles()) {
			String name = file.getName();
			if(name.startsWith("b_")) files.add(name.substring(2, name.length() - 4));
		}
		
		RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
		
		boards.setLayoutManager(manager);
		boards.setItemAnimator(new DefaultItemAnimator());
		boards.setAdapter(adapter);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch(requestCode) {
			case NEW_BOARD:
				files.add(intent.getStringExtra("name"));
				adapter.notifyDataSetChanged();
				break;
			case EDIT_BOARD:
				files.set(files.indexOf(lastLoaded), intent.getStringExtra("name"));
				adapter.notifyDataSetChanged();
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
