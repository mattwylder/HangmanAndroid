package com.cssdpu.hangman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private ImageView mHead;
	private Button mGuessButton;
	private EditText mEditText;
	private TextView mGuessedTextView, mCorrectlyGuessedTextView;
	private TextView mFailureCount; //TODO: We'll replace this with a visual representation eventually
	private String mGuessedString = "";
	private ArrayList<String> mAllGuessedStringList = new ArrayList<String>();//list of all guessed letters
	private ArrayList<String> mWrongGuessedStringList = new ArrayList<String>();//list of incorrect guessed letters
	private int mWrongGuesses = 0;
	
	private String mCurrentWord;
	
	private static final int FAILURE_LIMIT = 6; //TODO:placeholder value (head, body 2 legs, 2 arms)
	
	private static final String[] WORDS = new String[] {
		"Belly", "Runner", "Chicago", "DePaul", "Fake"
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		resetGame();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_restart:
	    	resetGame();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * Replaces the current word and clears the current guess.
	 */
	private void resetGame() {
		mCurrentWord = null;
		mGuessedString = "";
		getEditText().setText("");
		getGuessedTextView().setText("");
		getCorrectlyGuessedTextView().setText("");
		getFailureTextView().setText("0 / " + FAILURE_LIMIT );
		mWrongGuesses = 0;
		mAllGuessedStringList = new ArrayList<String>();//list of all guessed letters
		mWrongGuessedStringList = new ArrayList<String>();//list of incorrect guessed letters
		updateBody();
		//reset other stuff here
		
		//Generate new word
		mCurrentWord = generateRandomWord();
		
		String underscoreWord = getCurrentWord().replaceAll("[A-Za-z]", "_ ");
		getCorrectlyGuessedTextView().setText(underscoreWord);
	}
	
	/**
	 * Displays a toast message
	 * 
	 * @param message
	 * 		the message to be displayed
	 */
	private void showToast(String message) {
		Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
	}
	
	//checks if 'text' has already been guessed
	//returns true if already guessed
	private boolean didGuess(String text) {
		String charsString = mGuessedString.substring(mGuessedString.indexOf(":")+1);
		if (mAllGuessedStringList.contains(text))
			return true;
		return false;
	}
	
	/**
	 * Determines whether or not the user has guessed correctly.
	 * 
	 * @param text
	 * 		The text guessed by the user.
	 * @return
	 * 		True if the user guess is in the Word.
	 */
	private boolean isCorrectGuess(String text) {
		if (getCurrentWord().contains(text))
			return true;
		return false;
	}
	
	private void guessWasWrong(){
		//TODO: add a body part to the screen.

		mWrongGuesses++;
		mFailureCount.setText("" + mWrongGuesses + " / " + FAILURE_LIMIT + "");
		updateBody();
		if(mWrongGuesses >= FAILURE_LIMIT){
			showToast("You've failed. Restart from the Options Menu");
		}
	}
	/**
	 * Updates the body based on the number of wrong guesses.
	 */
	private void updateBody(){
		if (mWrongGuesses == 0)
		{
			getHead().setVisibility(ImageView.INVISIBLE);
		}
		else if (mWrongGuesses == 1)
		{
			mHead.setVisibility(ImageView.VISIBLE);
		}
	}
	
	/**
	 * Submits a guess.
	 * 
	 * @param view
	 * 		Not exactly sure.
	 */
	public void onSubmitPress(View view) {
		final String text = getEditText().getText().toString().toUpperCase();
		getEditText().setText(""); //erase text
		
		if (text.length() == 0)
			return;
		
		if (didGuess(text)) {
			showToast(text+" was already guessed!");
			return; //don't penalize - just have them guess again
		}
		
		//add to list of all guessed letters (doesn't matter if wrong or right guess)
		mAllGuessedStringList.add(text);
		
		if (isCorrectGuess(text)) {
			showToast("Correct!");
			
			StringBuilder currentCorrectText = new StringBuilder(getCorrectlyGuessedTextView().getText().toString());
			final String currWord = getCurrentWord();
			
			int index = currWord.indexOf(text);
			while (index != -1) {
				currentCorrectText.setCharAt(index*2, text.charAt(0));
				index = currWord.indexOf(text, index+1);
			}
			getCorrectlyGuessedTextView().setText(currentCorrectText);
			getHead();
		}
		else {
			guessWasWrong();
			addToGuessedString(text);
		}
		
	}
	
	/**
	 * Adds guessed letter to list of guessed letters, sorts the list and
	 * then sets the display text to the newly sorted list.
	 * @param text the letter guessed
	 */
	private void addToGuessedString(String text) {
		mWrongGuessedStringList.add(text);
		
		Collections.sort(mWrongGuessedStringList);
		
		mGuessedString = "Guessed: ";
		
		for (String s : mWrongGuessedStringList) {
			mGuessedString += s;
		}
		
		getGuessedTextView().setText(mGuessedString);
	}
	
	/**
	 * Generates a new word from the array of words.
	 * 
	 * @return
	 * 		a new word in all upper case.
	 */
	private String generateRandomWord() {
		int rand = new Random().nextInt(WORDS.length);
		return WORDS[rand].toUpperCase();
	}
	
	
	/*
	 * Getter methods below
	 */
	public String getCurrentWord() {
		return mCurrentWord;
	}
	
	public TextView getGuessedTextView() {
		if (mGuessedTextView == null)
			mGuessedTextView = (TextView) findViewById(R.id.guessedTextView);
		return mGuessedTextView;
	}
	
	public TextView getCorrectlyGuessedTextView() {
		if (mCorrectlyGuessedTextView == null)
			mCorrectlyGuessedTextView = (TextView) findViewById(R.id.correctlyGuessedTextView);
		return mCorrectlyGuessedTextView;
	}
	
	public TextView getFailureTextView(){
		if (mFailureCount == null)
			mFailureCount = (TextView) findViewById(R.id.failureCount);
		return mFailureCount;
	}
	
	public EditText getEditText() {
		if (mEditText == null)
			mEditText = (EditText) findViewById(R.id.editText);
		return mEditText;
	}
	
	public Button getButton(){
		if (mGuessButton == null)
			mGuessButton = (Button) findViewById(R.id.guessButton);
		return mGuessButton;
	}
	
	public ImageView getHead(){
		if (mHead == null)
			mHead = (ImageView) findViewById(R.id.headView);
		return mHead;
	}
	
}
