package com.tortoaster.customchess.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.view.PieceMovesView;

public class PieceMovesActivity extends AppCompatActivity {
	
	private PieceMovesView pieceMovesView;
	private Switch jumping, repeating;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_piece_moves);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		
		pieceMovesView = findViewById(R.id.piece_moves);
		jumping = findViewById(R.id.jumping);
		repeating = findViewById(R.id.repeating);
		
		String s = getIntent().getStringExtra("given string");
		pieceMovesView.getMovesFromString(s);
	}
	
	/**
	 * The next selected move is a jumping move
	 */
	public void setJumping(View view) {
		pieceMovesView.setJumping(jumping.isChecked());
	}
	
	/**
	 * The next selected move is a repeating move
	 */
	public void setRepeating(View view) {
		pieceMovesView.setRepeating(repeating.isChecked());
	}
	
	/**
	 * Closes the Activity
	 */
	public void cancel(View view) {
		finish();
	}
	
	/**
	 * Returns all selected moves
	 */
	public void apply(View view) {
		String moves = pieceMovesView.getMoves();
		Intent result = new Intent();
		result.putExtra("string", moves);
		setResult(Activity.RESULT_OK, result);
		finish();
	}
	
	/**
	 * Sets helpMenu to visible if gone and gone if anything else.
	 */
	public void help(View view) {
	
	}
	
}
