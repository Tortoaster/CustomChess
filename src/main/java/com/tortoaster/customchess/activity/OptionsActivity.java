package com.tortoaster.customchess.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.SeekBar;

import com.tortoaster.customchess.R;

public class OptionsActivity extends AppCompatActivity {
	
	public static final int PICK_LEVEL = 98432;
	private CheckedTextView player1, player2;
	private Button mBoardBtn;
	private SeekBar seekBar;
	
	/**
	 * This Activity contains options for the gameplay, such as what mBoardBtn to play with, which
	 * players are human and which are not, etcetera.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		
		mBoardBtn = findViewById(R.id.board);
		
		player1 = findViewById(R.id.player1);
		player2 = findViewById(R.id.player2);
		
		seekBar = findViewById(R.id.strength);
		
		seekBar.setProgress(1);
		
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == PICK_LEVEL && resultCode == RESULT_OK) {
			mBoardBtn.setText(data.getStringExtra("result"));
		}
	}
	
	public void chooseBoard(View view) {
		Intent intent = new Intent(this, BoardSelectorActivity.class);
		intent.putExtra("resultExpected", true);
		startActivityForResult(intent, PICK_LEVEL);
	}
	
	public void startGame(View view) {
		Intent intent = new Intent(this, GameActivity.class);
		
		intent.putExtra("p1c", player1.isChecked());
		intent.putExtra("p2c", player2.isChecked());
		intent.putExtra("strength", seekBar.getProgress() + 1);
		intent.putExtra("mBoardBtn", mBoardBtn.getText());
		
		startActivity(intent);
	}
	
	public void switchPlayer(View view) {
		CheckedTextView text = (CheckedTextView) view;
		
		if(text.isChecked()) {
			text.setText(R.string.human);
		} else {
			text.setText(R.string.computer);
		}
	}
}
