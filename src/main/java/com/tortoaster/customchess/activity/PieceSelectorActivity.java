package com.tortoaster.customchess.activity;

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

public class PieceSelectorActivity extends AppCompatActivity implements Button.OnClickListener {
	
	private static final int NEW_PIECE = 1, EDIT_PIECE = 2;
	
	private String lastLoaded;
	
	private List<String> files;
	
	private RecyclerView.Adapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_piece_selector);
		
		RecyclerView pieces = findViewById(R.id.pieces);
		
		files = new ArrayList<>();
		adapter = new ButtonAdapter(files, this);
		
		for(File file : getFilesDir().listFiles()) {
			String name = file.getName();
			if(name.startsWith("p_")) files.add(name.substring(2, name.length() - 4));
		}
		
		RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
		
		pieces.setLayoutManager(manager);
		pieces.setItemAnimator(new DefaultItemAnimator());
		pieces.setAdapter(adapter);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(resultCode == RESULT_OK) {
			files.add(intent.getStringExtra("name"));
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onClick(View v) {
		loadPiece(((Button) v).getText().toString());
	}
	
	public void loadPiece(String name) {
		lastLoaded = name;
		
		Intent intent = new Intent(this, PieceEditorActivity.class);
		intent.putExtra("name", name);
		startActivityForResult(intent, EDIT_PIECE);
	}
	
	public void newPiece(View view) {
		Intent intent = new Intent(this, PieceEditorActivity.class);
		startActivityForResult(intent, NEW_PIECE);
	}
}
