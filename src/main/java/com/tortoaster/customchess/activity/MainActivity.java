package com.tortoaster.customchess.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tortoaster.customchess.R;

public class MainActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void play(View view) {
		Intent intent = new Intent(this, OptionsActivity.class);
		startActivity(intent);
	}
	
	public void boardEditor(View view) {
		Intent intent = new Intent(this, BoardSelectorActivity.class);
		startActivity(intent);
	}
	
	public void pieceEditor(View view) {
		Intent intent = new Intent(this, PieceSelectorActivity.class);
		startActivity(intent);
	}
}
