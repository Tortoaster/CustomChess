package com.tortoaster.customchess.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tortoaster.customchess.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class PieceEditorActivity extends AppCompatActivity {
	
	private static final int resultAreMoves = 0, resultAreAttacks = 1, resultIsWhitePicture = 2, resultIsBlackPicture = 3;
	private boolean selectedWhitePic, selectedBlackPic, royal, capturable = true, customAttacks;
	private int value = 0;
	
	private Bitmap whitePicture = null, blackPicture = null;
	
	private String moves = "", attacks = "";
	
	private TextView valueText;
	
	private EditText nameField;
	
	private Switch mCapturableSwitch, mRoyalSwitch, mAttackMovesSwitch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_piece_editor);
		
		nameField = findViewById(R.id.piece_name);
		
		Intent intent = getIntent();
		String name = intent.getStringExtra("name");
		
		if(name != null) loadPiece(name);
		
		mCapturableSwitch = findViewById(R.id.capturable);
		mRoyalSwitch = findViewById(R.id.royal);
		mAttackMovesSwitch = findViewById(R.id.attack_moves);
		
		mCapturableSwitch.setChecked(capturable);
		mRoyalSwitch.setChecked(royal);
		valueText = findViewById(R.id.value);
		calculateValue();
		
		if(moves.equals(attacks) && !moves.isEmpty()) {
			Button attacksButton = findViewById(R.id.attacks_button);
			
			mAttackMovesSwitch.setChecked(true);
			attacksButton.setEnabled(false);
			attacksButton.setTextColor(Color.GRAY);
		}
		
		findImages(name);
	}
	
	/**
	 * Looks for the black and white image of the piece
	 *
	 * @param name the name of the piece
	 */
	public void findImages(String name) {
		try {
			whitePicture = BitmapFactory.decodeStream(openFileInput("w" + name + ".png"));
			setWhitePicture();
			selectedWhitePic = true;
		} catch(IOException e) {
			whitePicture = null;
		}
		try {
			blackPicture = BitmapFactory.decodeStream(openFileInput("b" + name + ".png"));
			setBlackPicture();
			selectedBlackPic = true;
		} catch(IOException e) {
			blackPicture = null;
		}
	}
	
	public void setRoyal(View view) {
		royal = mRoyalSwitch.isChecked();
		
		if(!capturable && royal) {
			capturable = true;
			mCapturableSwitch.setChecked(true);
			Toast.makeText(this, "no", Toast.LENGTH_SHORT).show();
		}
		calculateValue();
	}
	
	public void setCapturable(View view) {
		capturable = mCapturableSwitch.isChecked();
		if(!capturable && royal) {
			royal = false;
			mRoyalSwitch.setChecked(false);
			Toast.makeText(this, "no", Toast.LENGTH_SHORT).show();
		}
		
		calculateValue();
	}
	
	/**
	 * When enabled, the attacks of this piece will be the same as its normal moves.
	 */
	public void setAttackMoves(View view) {
		customAttacks = ((CheckBox) view).isChecked();
		Button attacksButton = findViewById(R.id.attacks_button);
		if(customAttacks) {
			attacksButton.setEnabled(false);
			attacksButton.setTextColor(Color.GRAY);
			attacks = moves;
		} else {
			attacksButton.setEnabled(true);
			attacksButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
		}
		
		calculateValue();
	}
	
	/**
	 * starts the activity that receives the moves of the piece
	 */
	public void makeMoves(View view) {
		Intent intent = new Intent(this, PieceMovesActivity.class);
		intent.putExtra("given string", moves);
		startActivityForResult(intent, resultAreMoves);
	}
	
	/**
	 * starts the activity that receives the attacks of the piece
	 */
	public void makeAttacks(View view) {
		Intent intent = new Intent(this, PieceMovesActivity.class);
		intent.putExtra("given string", attacks);
		startActivityForResult(intent, resultAreAttacks);
	}
	
	/**
	 * Starts the activity that receives the white picture of the piece
	 */
	public void getWhitePicture(View view) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), resultIsWhitePicture);
	}
	
	/**
	 * Starts the activity that receives the black picture of the piece
	 */
	public void getBlackPicture(View view) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), resultIsBlackPicture);
	}
	
	/**
	 * shows the whitePicture on screen
	 */
	public void setWhitePicture() {
		ImageView white = findViewById(R.id.white_image);
		if(whitePicture != null) {
			white.setImageBitmap(whitePicture);
		}
	}
	
	/**
	 * Calculates what the custom piece is currently worth approximately.
	 */
	public void calculateValue() {
		String[] movesList = new String[0];
		String[] attacksList = new String[0];
		
		value = 0;
		
		if(!moves.equals(""))
			movesList = moves.split(", ");
		if(!attacks.equals(""))
			attacksList = attacks.split(", ");
		if(royal)
			value = value + 11;
		if(!capturable)
			value = value - 5;
		if(movesList.length > 0)
			value = value + parseArray(movesList);
		if(attacksList.length > 0)
			value = value + parseArray(attacksList);
		
		valueText.setText(value + "");
	}
	
	/**
	 * shows the blackPicture on screen
	 */
	public void setBlackPicture() {
		ImageView black = findViewById(R.id.black_image);
		if(blackPicture != null) {
			black.setImageBitmap(blackPicture);
		}
	}
	
	/**
	 * @param array an array of moves
	 * @return a value that corresponds to the contents of the array
	 */
	public int parseArray(String[] array) {
		int calculation = 0;
		
		for(String move : array) {
			int moveValue = 0;
			System.out.println(move);
			String[] values = move.split(" ");
			int x = Integer.parseInt(values[0]);
			int y = Integer.parseInt(values[1]);
			boolean repeating = !(values[2].equals("0"));
			boolean jumping = !(values[3].equals("0"));
			
			if(x == 0)
				moveValue = moveValue + Math.abs(y);
			else if(y == 0)
				moveValue = moveValue + Math.abs(x);
			else moveValue = moveValue + Math.abs(x * y);
			if(repeating)
				moveValue = (moveValue * 8) + 8;
			if(jumping)
				moveValue = (int) Math.round(moveValue * 1.5);
			
			calculation += moveValue;
		}
		
		return calculation / 8;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == resultAreMoves && resultCode == RESULT_OK)
			moves = data.getStringExtra("string");
		else if(requestCode == resultAreAttacks && resultCode == RESULT_OK)
			attacks = data.getStringExtra("string");
		else if(requestCode == resultIsWhitePicture && resultCode == RESULT_OK) {
			selectedWhitePic = true;
			Uri uri = data.getData();
			try {
				whitePicture = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
				setWhitePicture();
			} catch(IOException e) {
				e.printStackTrace();
			}
		} else if(requestCode == resultIsBlackPicture && resultCode == RESULT_OK) {
			selectedBlackPic = true;
			Uri uri = data.getData();
			try {
				blackPicture = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
				setBlackPicture();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Create a txt with the properties of the saved piece. Saves the given images for the piece.
	 */
	public void makePiece(View view) {
		if(nameField.getText().toString().trim().isEmpty()) {
			Toast.makeText(this, "no", Toast.LENGTH_SHORT).show();
		} else if(nameField.getText().toString().contains(",")) {
			Toast.makeText(this, "no comma", Toast.LENGTH_SHORT).show();
		} else if(!selectedWhitePic || !selectedBlackPic) {
			Toast.makeText(this, "pics or it didn't happen", Toast.LENGTH_SHORT).show();
		} else {
			String name = "p_" + nameField.getText().toString() + ".txt";
			
			if(mAttackMovesSwitch.isChecked()) attacks = moves;
			
			String content = value + "|" + royal + "|" + capturable + "|" + moves + "|" + attacks + "|";
			
			FileOutputStream fos = null;
			
			try {
				fos = openFileOutput(name, MODE_PRIVATE);
				fos.write(content.getBytes());
				Toast.makeText(this, "dun", Toast.LENGTH_SHORT).show();
				// This sleep is because otherwise the finish is to quick after the message.
				try {
					Thread.sleep(500);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
				finish();
			} catch(IOException e) {
				Toast.makeText(this, "oh sh*t", Toast.LENGTH_SHORT).show();
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
			FileOutputStream outW = null;
			try {
				outW = openFileOutput("w" + nameField.getText().toString() + ".png", MODE_PRIVATE);
				whitePicture.compress(Bitmap.CompressFormat.PNG, 100, outW);
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(outW != null) {
						outW.close();
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			FileOutputStream outB = null;
			try {
				outB = openFileOutput("b" + nameField.getText().toString() + ".png", MODE_PRIVATE);
				blackPicture.compress(Bitmap.CompressFormat.PNG, 100, outB);
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(outB != null) {
						outB.close();
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			
			File[] files = getFilesDir().listFiles();
			Arrays.sort(files);
			
			for(File f : files) {
				System.out.println("Name:" + f.getName());
			}
		}
	}
	
	/**
	 * receive all the saved properties of the piece
	 *
	 * @param name the name of the given piece
	 */
	public void loadPiece(String name) {
		nameField.setText(name);
		
		FileInputStream fis = null;
		StringBuilder builder = new StringBuilder();
		
		try {
			fis = openFileInput("p_" + name + ".txt");
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
		
		String[] lines = content.split("\\|");
		
		value = Integer.parseInt(lines[0]);
		royal = Boolean.parseBoolean(lines[1]);
		capturable = Boolean.parseBoolean(lines[2]);
		moves = lines[3];
		attacks = lines[4];
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(customAttacks)
			attacks = moves;
		calculateValue();
	}
}