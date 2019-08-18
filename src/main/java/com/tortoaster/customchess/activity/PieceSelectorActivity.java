package com.tortoaster.customchess.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.view.FileAdapter;

public class PieceSelectorActivity extends AppCompatActivity implements RecyclerView.OnClickListener {
	
	private RecyclerView.Adapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_piece_selector);
		
		RecyclerView pieces = findViewById(R.id.pieces);
		
		adapter = new FileAdapter(getFilesDir().listFiles(), "p_", ".txt");
		
		pieces.setAdapter(adapter);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onClick(View v) {
		loadPiece(((Button) v).getText().toString());
	}
	
	public void loadPiece(String name) {
		Intent intent = new Intent(this, PieceEditorActivity.class);
		intent.putExtra("name", name);
		startActivity(intent);
	}
	
	public void newPiece(View view) {
		Intent intent = new Intent(this, PieceEditorActivity.class);
		startActivity(intent);
	}
}
