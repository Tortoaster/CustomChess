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
import com.tortoaster.customchess.chess.Team;
import com.tortoaster.customchess.chess.piece.Piece;
import com.tortoaster.customchess.view.FileAdapter;
import com.tortoaster.customchess.view.SwipeToDeleteCallback;

import java.io.File;

public class PieceSelectorActivity extends AppCompatActivity implements FileAdapter.FileListener {
	
	private static final int NEW_PIECE = 1, EDIT_PIECE = 2;
	
	private String lastEdited;
	
	private FileAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_piece_selector);
		
		RecyclerView pieces = findViewById(R.id.pieces);
		
		RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
		
		adapter = new FileAdapter(getFilesDir().listFiles(), Piece.PREFIX, Piece.SUFFIX, this);
		
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
					adapter.add(new File(getFilesDir() + File.separator + Piece.PREFIX + intent.getStringExtra("name") + Piece.SUFFIX));
					break;
				case EDIT_PIECE:
					String name = intent.getStringExtra("name");
					if(!lastEdited.equals(name))
						adapter.replace(new File(getFilesDir() + File.separator + Piece.PREFIX + lastEdited + Piece.SUFFIX), new File(getFilesDir() + File.separator + Piece.PREFIX + name + Piece.SUFFIX));
			}
		}
	}
	
	@Override
	public void fileSelected(File file, String prefix, String suffix) {
		String name = file.getName();
		
		loadPiece(name.substring(prefix.length(), name.length() - suffix.length()));
	}
	
	@Override
	public void fileRemoved(File file, String prefix, String suffix) {
		String name = file.getName();
		
		name = name.substring(prefix.length(), name.length() - suffix.length());
		
		new File(getFilesDir() + File.separator + Team.LIGHT.getPrefix() + name + Team.SUFFIX).delete();
		new File(getFilesDir() + File.separator + Team.DARK.getPrefix() + name + Team.SUFFIX).delete();
	}
	
	public void loadPiece(String name) {
		lastEdited = name;
		
		Intent intent = new Intent(this, PieceEditorActivity.class);
		intent.putExtra("name", name);
		startActivityForResult(intent, EDIT_PIECE);
	}
	
	public void newPiece(View view) {
		Intent intent = new Intent(this, PieceEditorActivity.class);
		startActivityForResult(intent, NEW_PIECE);
	}
}
