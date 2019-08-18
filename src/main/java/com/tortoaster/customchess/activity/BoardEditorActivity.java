package com.tortoaster.customchess.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.chess.piece.Kind;
import com.tortoaster.customchess.chess.piece.Piece;
import com.tortoaster.customchess.chess.Team;
import com.tortoaster.customchess.view.BoardEditorView;
import com.tortoaster.customchess.view.PieceSelectorView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class BoardEditorActivity extends AppCompatActivity {
	
	private static final int SIZE_OFFSET = 4;
	
	private EditText boardName;
	
	private TextView widthText, heightText;
	
	private Switch colorSwitch;
	
	private SeekBar widthBar, heightBar;
	
	private PieceSelectorView selector;
	
	private String widthLabel, heightLabel;
	
	private BoardEditorView boardEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_board_editor);
		
		boardName = findViewById(R.id.board_name);
		widthText = findViewById(R.id.board_width_txt);
		heightText = findViewById(R.id.board_height_txt);
		widthBar = findViewById(R.id.board_width);
		heightBar = findViewById(R.id.board_height);
		selector = findViewById(R.id.piece_selector);
		boardEditor = findViewById(R.id.board_editor);
		colorSwitch = findViewById(R.id.black_white);
		
		widthLabel = (String) widthText.getText();
		heightLabel = (String) heightText.getText();
		
		int startWidth = widthBar.getProgress() + SIZE_OFFSET;
		int startHeight = heightBar.getProgress() + SIZE_OFFSET;
		
		widthText.setText(widthLabel + " " + (startWidth));
		heightText.setText(heightLabel + " " + (startHeight));
		
		widthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				int width = progress + SIZE_OFFSET;
				
				widthText.setText(widthLabel + " " + width);
				boardEditor.setBoardWidth(width);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		
		heightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				int height = progress + SIZE_OFFSET;
				
				heightText.setText(heightLabel + " " + height);
				boardEditor.setBoardHeight(height);
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
				if(isChecked) {
					boardEditor.setTeam(Team.BLACK);
					selector.setTeam(Team.BLACK);
				} else {
					boardEditor.setTeam(Team.WHITE);
					selector.setTeam(Team.WHITE);
				}
			}
		});
		
		for(Kind k : Kind.values()) {
			if(k != Kind.CUSTOM)
				selector.addPiece(Piece.createPiece(0, 0, Team.WHITE, k.getName(), null, this));
		}
		
		File[] files = getFilesDir().listFiles();
		
		for(File f : files)
			if(f.getName().startsWith("p_") && f.getName().endsWith(".txt"))
				selector.addPiece(Piece.createPiece(0, 0, Team.WHITE, f.getName().substring(2, f.getName().length() - 4), null, this));
		
		Intent intent = getIntent();
		
		String name = intent.getStringExtra("name");
		
		if(name != null) loadBoard(name);
	}
	
	/**
	 * Saves the current board to a file named b_[board name].txt
	 */
	public void saveBoard(View view) {
		if(!boardName.getText().toString().trim().isEmpty()) {
			FileOutputStream fos = null;
			String name = "b_" + boardName.getText() + ".txt";
			StringBuilder content = new StringBuilder();
			
			for(int y = 0; y < heightBar.getProgress() + SIZE_OFFSET; y++) {
				for(int x = 0; x < widthBar.getProgress() + SIZE_OFFSET; x++) {
					Piece p = boardEditor.getPiece(x, y);
					
					if(p == null) content.append("-");
					else content.append(p.getTeam() == Team.BLACK ? "b" : "w").append(p.getName());
					
					content.append(", ");
				}
				content.append("\n");
			}
			
			try {
				fos = openFileOutput(name, MODE_PRIVATE);
				fos.write(content.toString().getBytes());
				
				Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
				Thread.sleep(500);
				finish();
			} catch(IOException e) {
				Toast.makeText(this, "oh sh*t", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch(InterruptedException e) {
				e.printStackTrace();
			} finally {
				if(fos != null) {
					try {
						fos.close();
					} catch(IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			Toast.makeText(this, "no", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Loads a board from a file.
	 *
	 * @param name the name of the file to be read.
	 */
	public void loadBoard(String name) {
		boardName.setText(name);
		
		FileInputStream fis = null;
		StringBuilder builder = new StringBuilder();
		
		try {
			fis = openFileInput("b_" + name + ".txt");
			InputStreamReader fus = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(fus);
			String line;
			
			while((line = br.readLine()) != null) {
				builder.append(line).append("\n");
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(fis != null) {
				try {
					fis.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		String content = builder.toString();
		
		String[] lines = content.split("\n");
		int width = lines[0].split(", ").length;
		int height = lines.length;
		
		widthBar.setProgress(width - SIZE_OFFSET);
		heightBar.setProgress(height - SIZE_OFFSET);
		
		for(int y = 0; y < height; y++) {
			String[] line = lines[y].split(", ");
			for(int x = 0; x < width; x++) {
				Team team = line[x].charAt(0) == 'b' ? Team.BLACK : Team.WHITE;
				Piece p = Piece.createPiece(x, y, team, line[x].substring(1), null, this);
				if(p != null) {
					boardEditor.addPiece(p);
				}
			}
		}
	}
	
	/**
	 * @return the name of the piece the user selected from the available pieces
	 */
	public String getSelectedPiece() {
		return selector.getSelected().getName();
	}
}
