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
		
		pieceMovesView.setData(getIntent().getStringExtra("data"));
	}
	
	public void setJumping(View view) {
		pieceMovesView.setJumping(jumping.isChecked());
	}
	
	public void setRepeating(View view) {
		pieceMovesView.setRepeating(repeating.isChecked());
	}
	
	public void cancel(View view) {
		finish();
	}
	
	public void apply(View view) {
		Intent result = new Intent();
		result.putExtra("data", pieceMovesView.getData());
		setResult(Activity.RESULT_OK, result);
		finish();
	}
}
