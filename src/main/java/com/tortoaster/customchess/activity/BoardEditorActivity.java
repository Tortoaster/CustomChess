package com.tortoaster.customchess.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.chess.Team;
import com.tortoaster.customchess.chess.piece.CustomPiece;
import com.tortoaster.customchess.chess.piece.Piece;
import com.tortoaster.customchess.view.BoardEditorView;
import com.tortoaster.customchess.view.PieceAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BoardEditorActivity extends AppCompatActivity implements PieceAdapter.PieceSelectListener {
	
	private static final int MIN_SIZE = 4;
	
	private List<Piece> pieceList;
	
	private EditText name;
	private SeekBar width, height;
	private BoardEditorView boardEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_board_editor);
		
		name = findViewById(R.id.board_name);
		width = findViewById(R.id.board_width);
		height = findViewById(R.id.board_height);
		boardEditor = findViewById(R.id.board_editor);
		
		RecyclerView selector = findViewById(R.id.selector);
		Switch colorSwitch = findViewById(R.id.black_white);
		
		pieceList = new ArrayList<>();
		
		final RecyclerView.Adapter adapter = new PieceAdapter(pieceList, this);
		
		pieceList.add(Piece.createPiece(0, 0, Team.getStartTeam(), "wall", null, this));
		pieceList.add(Piece.createPiece(0, 0, Team.getStartTeam(), "king", null, this));
		pieceList.add(Piece.createPiece(0, 0, Team.getStartTeam(), "queen", null, this));
		pieceList.add(Piece.createPiece(0, 0, Team.getStartTeam(), "bishop", null, this));
		pieceList.add(Piece.createPiece(0, 0, Team.getStartTeam(), "knight", null, this));
		pieceList.add(Piece.createPiece(0, 0, Team.getStartTeam(), "rook", null, this));
		pieceList.add(Piece.createPiece(0, 0, Team.getStartTeam(), "pawn", null, this));
		
		for(String s: getFilesDir().list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(Piece.PREFIX) && name.endsWith(Piece.SUFFIX);
			}
		})) {
			pieceList.add(Piece.createPiece(0, 0, Team.getStartTeam(), CustomPiece.PREFIX + s.substring(Piece.PREFIX.length(), s.length() - Piece.SUFFIX.length()), null, this));
		}
		
		RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
		
		selector.setLayoutManager(manager);
		selector.setItemAnimator(new DefaultItemAnimator());
		selector.setAdapter(adapter);
		
		boardEditor.setWidth(width.getProgress() + MIN_SIZE);
		boardEditor.setHeight(height.getProgress() + MIN_SIZE);
		
		width.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				boardEditor.setWidth(progress + MIN_SIZE);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		
		height.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				boardEditor.setHeight(progress + MIN_SIZE);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		
		colorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				for(Piece p : pieceList) {
					p.setTeam(p.getTeam().getOppositeTeam());
					
					adapter.notifyDataSetChanged();
				}
			}
		});
		
		Intent intent = getIntent();
		
		String name = intent.getStringExtra("name");
		
		if(name != null) load(name);
	}
	
	@Override
	public void pieceSelected(Piece piece) {
		boardEditor.setSelected(piece);
	}
	
	public void save(View view) {
		int width = this.width.getProgress() + MIN_SIZE;
		int height = this.height.getProgress() + MIN_SIZE;
		String name = this.name.getText().toString().trim();
		
		StringBuilder builder = new StringBuilder();
		Piece[][] pieces = boardEditor.getPieces();
		
		for(int y = 0; y < pieces[0].length; y++) {
			for(int x = 0; x < pieces.length; x++) {
				if(pieces[x][y] != null) {
					builder.append(pieces[x][y].getTeam().getPrefix()).append(pieces[x][y].getName()).append(", ");
				} else {
					builder.append('-').append(", ");
				}
			}
		}
		
		String content = width + "\n" + height + "\n" + builder.toString() + "\n";
		
		if(name.isEmpty()) {
			Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(name.contains("\n")) {
			Toast.makeText(this, "This name contains forbidden characters", Toast.LENGTH_SHORT).show();
			return;
		}
		
		try {
			FileOutputStream stream = openFileOutput("b_" + name + ".txt", MODE_PRIVATE);
			stream.write(content.getBytes());
			stream.close();
			
			Intent intent = new Intent();
			intent.putExtra("name", name);
			setResult(RESULT_OK, intent);
			finish();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void load(String name) {
		String content = "";
		
		try {
			content = getContent(openFileInput("b_" + name + ".txt"));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String[] lines = content.split("\n");
		
		int width = Integer.parseInt(lines[0]);
		int height = Integer.parseInt(lines[0]);
		
		this.name.setText(name);
		this.width.setProgress(width - MIN_SIZE);
		this.height.setProgress(height - MIN_SIZE);
		
		Piece[][] pieces = new Piece[width][height];
		String[] data = lines[2].split(", ");
		
		for(int i = 0; i < data.length; i++) {
			if(!data[i].equals("-")) {
				pieces[i % width][i / width] = searchAvailablePieces(data[i]);
			}
		}
		
		boardEditor.setWidth(Integer.parseInt(lines[0]));
		boardEditor.setHeight(Integer.parseInt(lines[1]));
		boardEditor.setPieces(pieces);
	}
	
	private Piece searchAvailablePieces(String name) {
		for(Piece p: pieceList) {
			for(Team t : Team.values()) {
				if(name.equals(t.getPrefix() + p.getName())) {
					Piece piece = p.copy();
					
					piece.setTeam(t);
					
					return piece;
				}
			}
		}
		
		return null;
	}
	
	// TODO: find a more suitable place for this
	public static String getContent(FileInputStream stream) {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		String line;
		
		try {
			while((line = reader.readLine()) != null) {
				builder.append(line).append('\n');
			}
			
			reader.close();
			stream.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return builder.toString();
	}
}
