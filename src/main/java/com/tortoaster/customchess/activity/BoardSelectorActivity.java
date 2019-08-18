package com.tortoaster.customchess.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.view.FileAdapter;

public class BoardSelectorActivity extends AppCompatActivity implements RecyclerView.OnClickListener {
	
	private boolean resultExpected;
	
	private RecyclerView.Adapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_board_selector);
		
		RecyclerView boards = findViewById(R.id.boards);
		
		resultExpected = getIntent().getBooleanExtra("resultExpected", false);
		adapter = new FileAdapter(getFilesDir().listFiles(), "b_", ".txt");
		
		boards.setAdapter(adapter);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		adapter.notifyDataSetChanged();
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
		Intent intent = new Intent(this, BoardEditorActivity.class);
		intent.putExtra("name", name);
		startActivity(intent);
	}
	
	public void newBoard(View view) {
		Intent intent = new Intent(this, BoardEditorActivity.class);
		startActivity(intent);
	}
}
