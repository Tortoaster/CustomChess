package com.tortoaster.customchess.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.chess.Move;
import com.tortoaster.customchess.view.PieceMovesView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class PieceEditorActivity extends AppCompatActivity {
	
	private static final int GET_MOVES = 0, GET_ATTACKS = 1, GET_LIGHT_IMAGE = 2, GET_DARK_IMAGE = 3;
	
	private boolean lightImageDone, darkImageDone;
	
	private Bitmap lightImage, darkImage;
	
	private String moveData, attackData;
	
	private EditText name;
	private Switch royal, capturable, customAttacks;
	private TextView royalHint, capturableHint, customAttacksHint;
	private Button attacks;
	private ImageView light, dark;
	private TextView value;
	
	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_piece_editor);
		
		moveData = attackData = "";
		
		name = findViewById(R.id.piece_name);
		royal = findViewById(R.id.royal);
		capturable = findViewById(R.id.capturable);
		customAttacks = findViewById(R.id.custom_attacks);
		royalHint = findViewById(R.id.royal_hint);
		capturableHint = findViewById(R.id.capturable_hint);
		customAttacksHint = findViewById(R.id.custom_attacks_hint);
		attacks = findViewById(R.id.attacks);
		light = findViewById(R.id.light_image);
		dark = findViewById(R.id.dark_image);
		value = findViewById(R.id.value);
		
		capturable.setChecked(true);
		attacks.setEnabled(false);
		value.setText(Integer.toString(0));
		
		royal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(royal.isChecked()) {
					capturable.setChecked(true);
				}
				
				calculateValue();
			}
		});
		
		capturable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!isChecked) {
					royal.setChecked(false);
				}
				
				calculateValue();
			}
		});
		
		customAttacks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					attacks.setEnabled(true);
				} else {
					attacks.setEnabled(false);
					
					attackData = moveData;
					
					calculateValue();
				}
			}
		});
		
		Intent intent = getIntent();
		String name = intent.getStringExtra("name");
		
		if(name != null) load(name);
	}
	
	public void setMoves(View view) {
		Intent intent = new Intent(this, PieceMovesActivity.class);
		intent.putExtra("data", moveData);
		startActivityForResult(intent, GET_MOVES);
	}
	
	public void setAttacks(View view) {
		Intent intent = new Intent(this, PieceMovesActivity.class);
		intent.putExtra("data", attackData);
		startActivityForResult(intent, GET_ATTACKS);
	}
	
	public void setLightImage(View view) {
		Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
		getIntent.setType("image/*");
		
		Intent pickIntent = new Intent(Intent.ACTION_PICK);
		pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		
		Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
		
		startActivityForResult(chooserIntent, GET_LIGHT_IMAGE);
	}
	
	public void setDarkImage(View view) {
		Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
		getIntent.setType("image/*");
		
		Intent pickIntent = new Intent(Intent.ACTION_PICK);
		pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		
		Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
		
		startActivityForResult(chooserIntent, GET_DARK_IMAGE);
	}
	
	public void displayLightImage() {
		if(lightImage != null) {
			light.setImageBitmap(lightImage);
		}
	}
	
	public void displayDarkImage() {
		if(darkImage != null) {
			dark.setImageBitmap(darkImage);
		}
	}
	
	@SuppressLint("SetTextI18n")
	public void calculateValue() {
		double score = 0;
		
		for(Move m : PieceMovesView.translateData(moveData)) {
			score += 0.10 * Math.sqrt(m.getDeltaX() * m.getDeltaX() + m.getDeltaY() * m.getDeltaY()) * (m.isJumping() ? 1.5 : 1) * (m.isRepeating() ? 4 : 1);
		}
		
		for(Move m : PieceMovesView.translateData(attackData)) {
			score += 0.25 * Math.sqrt(m.getDeltaX() * m.getDeltaX() + m.getDeltaY() * m.getDeltaY()) * (m.isJumping() ? 1.5 : 1) * (m.isRepeating() ? 4 : 1);
		}
		
		if(royal.isChecked()) score = score + 11;
		
		value.setText(Integer.toString((int) Math.round(score)));
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			switch(requestCode) {
				case GET_MOVES:
					moveData = data.getStringExtra("data");
					
					if(!customAttacks.isChecked()) attackData = moveData;
					
					calculateValue();
					
					break;
				case GET_ATTACKS:
					attackData = data.getStringExtra("data");
					
					calculateValue();
					
					break;
				case GET_LIGHT_IMAGE:
					try {
						Uri uri = data.getData();
						
						lightImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
						lightImageDone = true;
						
						displayLightImage();
					} catch(IOException e) {
						e.printStackTrace();
					}
					break;
				case GET_DARK_IMAGE:
					try {
						Uri uri = data.getData();
						
						darkImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
						darkImageDone = true;
						
						displayDarkImage();
					} catch(IOException e) {
						e.printStackTrace();
					}
			}
		}
	}
	
	public void save(View view) {
		boolean royal = this.royal.isChecked();
		boolean capturable = this.capturable.isChecked();
		int value = Integer.parseInt(this.value.getText().toString());
		String name = this.name.getText().toString().trim();
		
		if(!customAttacks.isChecked()) attackData = moveData;
		
		if(name.isEmpty()) {
			Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(name.contains("\n")) {
			Toast.makeText(this, "This name contains forbidden characters", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(!lightImageDone || !darkImageDone) {
			Toast.makeText(this, "Please provide two images", Toast.LENGTH_SHORT).show();
			return;
		}
		
		String content = value + "\n" + (royal ? 1 : 0) + "\n" + (capturable ? 1 : 0) + "\n" + moveData + "\n" + attackData + "\n";
		
		try {
			FileOutputStream stream = openFileOutput("p_" + name + ".txt", MODE_PRIVATE);
			stream.write(content.getBytes());
			stream.close();
			
			stream = openFileOutput("l_" + name + ".png", MODE_PRIVATE);
			lightImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
			stream.close();
			
			stream = openFileOutput("d_" + name + ".png", MODE_PRIVATE);
			darkImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
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
		StringBuilder builder = new StringBuilder();
		
		try {
			FileInputStream stream = openFileInput("p_" + name + ".txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line;
			
			while((line = reader.readLine()) != null) {
				builder.append(line).append('\n');
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		String content = builder.toString();
		
		String[] lines = content.split("\n");
		
		this.name.setText(name);
		value.setText(lines[0]);
		royal.setChecked(Integer.parseInt(lines[1]) != 0);
		capturable.setChecked(Integer.parseInt(lines[2]) != 0);
		if(lines.length > 3) moveData = lines[3];
		if(lines.length > 4) attackData = lines[4];
		
		customAttacks.setChecked(!moveData.equals(attackData));
		attacks.setEnabled(customAttacks.isChecked());
		
		try {
			FileInputStream stream = openFileInput("l_" + name + ".png");
			
			lightImage = BitmapFactory.decodeStream(stream);
			
			stream.close();
			
			lightImageDone = true;
			
			displayLightImage();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		try {
			FileInputStream stream = openFileInput("d_" + name + ".png");
			
			darkImage = BitmapFactory.decodeStream(stream);
			
			stream.close();
			
			darkImageDone = true;
			
			displayDarkImage();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showRoyalHint(View view) {
		if(royalHint.getVisibility() == View.GONE) {
			royalHint.setVisibility(View.VISIBLE);
		} else {
			royalHint.setVisibility(View.GONE);
		}
	}
	
	public void showCapturableHint(View view) {
		if(capturableHint.getVisibility() == View.GONE) {
			capturableHint.setVisibility(View.VISIBLE);
		} else {
			capturableHint.setVisibility(View.GONE);
		}
	}
	
	public void showCustomAttacksHint(View view) {
		if(customAttacksHint.getVisibility() == View.GONE) {
			customAttacksHint.setVisibility(View.VISIBLE);
		} else {
			customAttacksHint.setVisibility(View.GONE);
		}
	}
}