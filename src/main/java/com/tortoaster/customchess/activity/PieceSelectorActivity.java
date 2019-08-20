package com.tortoaster.customchess.activity;

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

public class PieceSelectorActivity extends AppCompatActivity implements Button.OnClickListener {
	
	private static final int NEW_PIECE = 1, EDIT_PIECE = 2;
	
	private String lastEdited;
	
	private FileAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_piece_selector);
		
		RecyclerView pieces = findViewById(R.id.pieces);
		
		RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
		
		adapter = new FileAdapter(getFilesDir().listFiles(), "p_", ".txt", this);
		
		pieces.setLayoutManager(manager);
		pieces.setItemAnimator(new DefaultItemAnimator());
		pieces.setAdapter(adapter);
		
		new ItemTouchHelper(new SwipeToDeleteCallback(this, adapter)).attachToRecyclerView(pieces);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(resultCode == RESULT_OK) {
			switch(requestCode) {
				case NEW_PIECE:
					adapter.add(new File(getFilesDir() + File.separator + "p_" + intent.getStringExtra("name") + ".txt"));
					break;
				case EDIT_PIECE:
					String name = intent.getStringExtra("name");
					if(!lastEdited.equals(name)) adapter.replace(new File(getFilesDir() + File.separator + "p_" + lastEdited + ".txt"), new File(getFilesDir() + File.separator + "p_" + name + ".txt"));
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		String name = ((Button) v).getText().toString();
		
		lastEdited = name;
		
		loadPiece(name);
	}
	
	public void loadPiece(String name) {
		Intent intent = new Intent(this, PieceEditorActivity.class);
		intent.putExtra("name", name);
		startActivityForResult(intent, EDIT_PIECE);
	}
	
	public void newPiece(View view) {
		Intent intent = new Intent(this, PieceEditorActivity.class);
		startActivityForResult(intent, NEW_PIECE);
	}
}
