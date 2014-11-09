package com.jose.javaquiz;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends Activity
{

	int numberOfQuestions;
	DBAdapter db;
	static final String TAG = "GameActivity";
	private TextView textTimer;
	private long startTime = 0L;
	private Handler myHandler = new Handler();
	long timeInMillies = 0L;
	long timeSwap = 0L;
	long finalTime = 0L;
	int seconds = (int) (finalTime / 1000);
	int minutes = (seconds / 60);
	int currentQuestion = 0;
	int attentedQuestions;
	String difficulty;
	int milliseconds = (int) (finalTime % 1000);
	ArrayList<Map<String, String>> questions = new ArrayList<Map<String, String>>();
	ArrayList<Map<String, String>> myAnswers = new ArrayList<Map<String, String>>();

	RadioGroup rGroup;
	RadioButton rB1;
	RadioButton rB2;
	RadioButton rB3;
	RadioButton rB4;
	CountDownTimer cdt;
	
	//variables for the intent
	public final static String EXTRA_SCORE="com.jose.javaquiz.SCORE";
	public final static String EXTRA_TOTAL_QUESTIONS="com.jose.javaquiz.TOTAL_QUESTIONS";
	public final static String EXTRA_ATTENTED="com.jose.javaquiz.ATTENTED";
	public final static String EXTRA_RIGHT="com.jose.javaquiz.RIGHT";
	public final static String EXTRA_TIME="com.jose.javaquiz.TIME";
	public final static String EXTRA_TOPSCORE="com.jose.javaquiz.TOPSCORE";
	

	
	
	
	
	/**
	 * This method get the questions from mysqlite database
	 * 
	 * @param difficulty
	 *            (the type of question)
	 * @param numberOfQuestions
	 * @return
	 */
	private ArrayList<Map<String, String>> getQuestions(String difficulty,
			int numberOfQuestions) {

		db.open();
		Cursor cursor = db.getQuestions(difficulty, numberOfQuestions);
		db.close();
		// ArraList of map(containing the questions, the answers and the correct
		// ans
		ArrayList<Map<String, String>> questionList = new ArrayList<Map<String, String>>();

		// while there are data
		while (cursor.moveToNext()) {
			// add elements to the map
			Map<String, String> questionMap = new HashMap<String, String>();

			String question = cursor.getString(cursor
					.getColumnIndex(DBAdapter.KEY_QUESTION));
			questionMap.put(DBAdapter.KEY_QUESTION, question);

			String answer1 = cursor.getString(cursor
					.getColumnIndex(DBAdapter.KEY_ANSWER1));
			questionMap.put(DBAdapter.KEY_ANSWER1, answer1);
			String answer2 = cursor.getString(cursor
					.getColumnIndex(DBAdapter.KEY_ANSWER2));
			questionMap.put(DBAdapter.KEY_ANSWER2, answer2);
			String answer3 = cursor.getString(cursor
					.getColumnIndex(DBAdapter.KEY_ANSWER3));
			questionMap.put(DBAdapter.KEY_ANSWER3, answer3);
			String answer4 = cursor.getString(cursor
					.getColumnIndex(DBAdapter.KEY_ANSWER4));
			questionMap.put(DBAdapter.KEY_ANSWER4, answer4);

			int correctAnswer = cursor.getInt(cursor
					.getColumnIndex(DBAdapter.KEY_CORRECTANSWER));
			questionMap.put(DBAdapter.KEY_CORRECTANSWER,
					Integer.toString(correctAnswer));

			// add map to the ARRAYLIST when all data was inserted into the map
			questionList.add(questionMap);

		}// end while loop

		return questionList;

	}// end method getQuestions

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		// make invisible the previous button
		Button previousQuestionButton = (Button) findViewById(R.id.buttonPreviousQuestion);
		previousQuestionButton.setVisibility(Button.INVISIBLE);

		// make invisible the submitQuiz button
		final Button submitQuizBtn = (Button) findViewById(R.id.buttonSubmitQuiz);
		submitQuizBtn.setVisibility(Button.INVISIBLE);

		// set the count down timer
		textTimer = (TextView) findViewById(R.id.textTimer);
		cdt = new CountDownTimer(300000, 1000) {

			public void onTick(long millisUntilFinished) {
				seconds = seconds % 60;
				textTimer
						.setText(""
								+ String.format(
										"%d : %02d ",
										TimeUnit.MILLISECONDS
												.toMinutes(millisUntilFinished),
										TimeUnit.MILLISECONDS
												.toSeconds(millisUntilFinished)
												- TimeUnit.MINUTES
														.toSeconds(TimeUnit.MILLISECONDS
																.toMinutes(millisUntilFinished))));

			}

			public void onFinish() {
				textTimer.setText("0:0");
				submitQuiz(null);
			}
		};
		cdt.start();
		// Getting the questions for the game
		Intent i = getIntent();

		db = new DBAdapter(this, Integer.parseInt(i.getStringExtra("version")));

		difficulty = i.getStringExtra("difficulty");
		numberOfQuestions = i.getIntExtra("questionNo", -1);

		Log.d("GameActivity", "Difficulty = " + difficulty + " questionNo = "
				+ numberOfQuestions);

		TextView counter = (TextView) findViewById(R.id.counter);

		counter.setText((currentQuestion + 1) + "/" + numberOfQuestions);

		// get the questions
		questions = getQuestions(difficulty, numberOfQuestions);

		Log.d(TAG, "Question List = " + questions);

		// DISPLAY QUESTION INTO THE ACTIVITY
		displayQuestions();

	}// end method

	/**
	 * This method helps to display the questions
	 */
	private void displayQuestions() {

		rGroup = (RadioGroup) findViewById(R.id.radioAnswer);

		rB1 = (RadioButton) findViewById(R.id.radioButton1);
		rB2 = (RadioButton) findViewById(R.id.radioButton2);
		rB3 = (RadioButton) findViewById(R.id.radioButton3);
		rB4 = (RadioButton) findViewById(R.id.radioButton4);

		// rGroup.clearCheck();
		/*
		 * for (int i = 1; i < 5; i++) { int id =
		 * getResources().getIdentifier("radioButton" + i, "id",
		 * getPackageName()); RadioButton rad = (RadioButton) findViewById(id);
		 * rad.setChecked(false); }
		 */

		rGroup.clearCheck();
		Log.d("displayQ", "\n========================Question "
				+ (currentQuestion + 1) + " =========================");
		Log.d("displayQ", "answer = " + myAnswers);
		for (int i = 0; i < myAnswers.size(); i++) {
			// Log.d("displayQ", "Looping i = " + i);
			String questionNumber = myAnswers.get(i).get("question");
			String currentQuestionText = Integer.toString(currentQuestion + 1);
			// Log.d("displayQ", "answerNo = " + answerNumber + " | currentQ = "
			// + currentQuestionText);

			if (questionNumber.equals(currentQuestionText)) {
				Log.d("displayQ", "Question matching... questionNumber = "
						+ questionNumber + " currentQuest = "
						+ currentQuestionText);
				boolean containsAnswer = !myAnswers.get(i).get("asnwer")
						.equals("-1");
				Log.d("displayQ", "contains answer: " + containsAnswer);
				if (containsAnswer) {
					// findViewById(getResources().getIdentifier(VIEW_NAME,
					// "id", getPackageName()));

					int answerSelected = Integer.parseInt(myAnswers.get(i).get(
							"asnwer"));
					Log.d("displayQ", "answer selected: " + answerSelected);
					// int id = getResources().getIdentifier("radioButton" +
					// Integer.toString(answerSelected), "id",
					// getPackageName());
					// RadioButton rad = (RadioButton) findViewById(id);
					// rad.setChecked(true);
					/*
					 * RadioGroup rGroup =(RadioGroup)
					 * findViewById(R.id.radioAnswer);
					 * 
					 * RadioButton rB1=(RadioButton)
					 * findViewById(R.id.radioButton1); RadioButton
					 * rB2=(RadioButton) findViewById(R.id.radioButton2);
					 * RadioButton rB3=(RadioButton)
					 * findViewById(R.id.radioButton3); RadioButton
					 * rB4=(RadioButton) findViewById(R.id.radioButton4);
					 */
					Log.d("switch", "This answer selected" + answerSelected);
					switch (answerSelected) {

					case 1:
						;

						// rGroup.check(R.id.radioButton1);
						// rB1=(RadioButton) rGroup.getChildAt(1);
						rB1.setChecked(true);

						Log.d("case1", "This should work: radioButton"
								+ answerSelected);
						break;

					case 2: // rB2=(RadioButton) rGroup.getChildAt(2);
						rB2.setChecked(true);
						break;

					case 3: // rB3=(RadioButton) rGroup.getChildAt(3);
						rB3.setChecked(true);
						break;
					case 4:
						// rB4=(RadioButton) rGroup.getChildAt(4);
						rB4.setChecked(true);
						break;

					case -1:
						break;

					}
				}
			}

			/*
			 * currentQuestion+1 asnwer,question
			 */
		}

		TextView counter = (TextView) findViewById(R.id.counter);
		counter.setText((currentQuestion + 1) + "/" + numberOfQuestions);

		// display question
		TextView textViewQuestion = (TextView) findViewById(R.id.textViewQuestion);
		textViewQuestion.setText(questions.get(currentQuestion).get(
				DBAdapter.KEY_QUESTION));

		// display answers
		// TextView textViewAnswer1 = (TextView)
		// findViewById(R.id.radioButton1);
		rB1.setText(questions.get(currentQuestion).get("answer1"));

		// TextView textViewAnswer2 = (TextView)
		// findViewById(R.id.radioButton2);
		rB2.setText(questions.get(currentQuestion).get("answer2"));

		// TextView textViewAnswer3 = (TextView)
		// findViewById(R.id.radioButton3);
		rB3.setText(questions.get(currentQuestion).get("answer3"));

		// TextView textViewAnswer4 = (TextView)
		// findViewById(R.id.radioButton4);
		rB4.setText(questions.get(currentQuestion).get("answer4"));

		Log.d("displayQ", "========================END Question "
				+ (currentQuestion + 1) + " =========================\n");

	}

	/**
	 * 
	 */
	public void unselectRadioButton() {

	}

	// next question
	public void nextQuestion(View view) {

		// rGroup.clearCheck();
		Button previousQuestionButton = (Button) findViewById(R.id.buttonPreviousQuestion);
		previousQuestionButton.setVisibility(Button.VISIBLE);

		// store the answers
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioAnswer);
		Map<String, String> answer = new HashMap<String, String>();
		int selected = radioGroup.getCheckedRadioButtonId();

		Log.d("displayQ", "selected = " + selected);

		switch (selected) {
		case R.id.radioButton1:
			answer.put("asnwer", "1");
			answer.put("question", Integer.toString(currentQuestion + 1)
					.toString());
			answer.put("corectAnswer",
					questions.get(currentQuestion).get("correctAnswer"));
			break;
		case R.id.radioButton2:
			answer.put("asnwer", "2");
			answer.put("question", Integer.toString(currentQuestion + 1)
					.toString());
			answer.put("corectAnswer",
					questions.get(currentQuestion).get("correctAnswer"));
			break;
		case R.id.radioButton3:
			answer.put("asnwer", "3");
			answer.put("question", Integer.toString(currentQuestion + 1)
					.toString());
			answer.put("corectAnswer",
					questions.get(currentQuestion).get("correctAnswer"));
			break;
		case R.id.radioButton4:
			answer.put("asnwer", "4");
			answer.put("question", Integer.toString(currentQuestion + 1)
					.toString());
			answer.put("corectAnswer",
					questions.get(currentQuestion).get("correctAnswer"));
			break;

		case -1:
			answer.put("asnwer", "-1");
			answer.put("question", Integer.toString(currentQuestion + 1)
					.toString());
			answer.put("corectAnswer",
					questions.get(currentQuestion).get("correctAnswer"));
			break;

		}

		// saveQuestion(currentQuestion, answer);

		try {
			Log.d("displayQ", "Removing answer...+!=£$!£!=$!+5$)%££(*£");
			myAnswers.remove(currentQuestion);
		} catch (Exception e) {
			Log.d("displayQ", "Couldn't remove mate.");
			e.printStackTrace();
		}

		myAnswers.add(currentQuestion, answer);
		Log.v("answer", "The answers  is: " + answer);
		currentQuestion++;

		displayQuestions();

		if ((currentQuestion + 1) == questions.size()) {

			Button nextQuestionButton = (Button) findViewById(R.id.buttonNextQuestion);
			nextQuestionButton.setVisibility(Button.INVISIBLE);

			Button submitQuizBtn = (Button) findViewById(R.id.buttonSubmitQuiz);
			submitQuizBtn.setVisibility(Button.VISIBLE);

		}

	}

	
	/**
	 * This method helps to display the previous question
	 * 
	 * @param view
	 */
	public void previousQuestion(View view) {
		Map<String, String> answer = new HashMap<String, String>();
		// rGroup.clearCheck();
		// store the answers
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioAnswer);

		int selected = radioGroup.getCheckedRadioButtonId();

		switch (selected) {
		case R.id.radioButton1:
			answer.put("asnwer", "1");
			answer.put("question", Integer.toString(currentQuestion + 1)
					.toString());
			answer.put("corectAnswer",
					questions.get(currentQuestion).get("correctAnswer"));
			break;
		case R.id.radioButton2:
			answer.put("asnwer", "2");
			answer.put("question", Integer.toString(currentQuestion + 1)
					.toString());
			answer.put("corectAnswer",
					questions.get(currentQuestion).get("correctAnswer"));
			break;
		case R.id.radioButton3:
			answer.put("asnwer", "3");
			answer.put("question", Integer.toString(currentQuestion + 1)
					.toString());
			answer.put("corectAnswer",
					questions.get(currentQuestion).get("correctAnswer"));
			break;
		case R.id.radioButton4:
			answer.put("asnwer", "4");
			answer.put("question", Integer.toString(currentQuestion + 1)
					.toString());
			answer.put("corectAnswer",
					questions.get(currentQuestion).get("correctAnswer"));
			break;
		case -1:
			answer.put("asnwer", "-1");
			answer.put("question", Integer.toString(currentQuestion + 1)
					.toString());
			answer.put("corectAnswer",
					questions.get(currentQuestion).get("correctAnswer"));
			break;

		}
		try {
			Log.d("displayQ", "removing question " + currentQuestion);
			myAnswers.remove(currentQuestion);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d("displayQ", "Adding question " + currentQuestion
				+ " \n holding : " + answer);
		if (!answer.isEmpty()) {
			myAnswers.add(currentQuestion, answer);
		}

		// myAnswers.

		Button previousQuestionButton = (Button) findViewById(R.id.buttonPreviousQuestion);

		currentQuestion--;
		if (currentQuestion == 0) {
			previousQuestionButton.setVisibility(Button.INVISIBLE);
		} else if (currentQuestion == (questions.size() - 2)) {
			Button nextQuestionButton = (Button) findViewById(R.id.buttonNextQuestion);
			nextQuestionButton.setVisibility(Button.VISIBLE);
		}

		rGroup.clearCheck();
		displayQuestions();

	}// end method

	/*
	 * 
	 * Method to submit the quiz
	 */
	//TODO
	public void submitQuiz(View view) {
		// checks for the last question(10th or 15th)
		//clock stop
		cdt.cancel();
		
		Map<String, String> answer = new HashMap<String, String>();
		// rGroup.clearCheck();
		// store the answers
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioAnswer);

		int selected = radioGroup.getCheckedRadioButtonId();

		
		switch (selected) {
		case R.id.radioButton1:
			answer.put("asnwer", "1");
			answer.put("question", Integer.toString(currentQuestion + 1)
					.toString());
			answer.put("corectAnswer",
					questions.get(currentQuestion).get("correctAnswer"));
			break;
		case R.id.radioButton2:
			answer.put("asnwer", "2");
			answer.put("question", Integer.toString(currentQuestion + 1)
					.toString());
			answer.put("corectAnswer",
					questions.get(currentQuestion).get("correctAnswer"));
			break;
		case R.id.radioButton3:
			answer.put("asnwer", "3");
			answer.put("question", Integer.toString(currentQuestion + 1)
					.toString());
			answer.put("corectAnswer",
					questions.get(currentQuestion).get("correctAnswer"));
			break;
		case R.id.radioButton4:
			answer.put("asnwer", "4");
			answer.put("question", Integer.toString(currentQuestion + 1)
					.toString());
			answer.put("corectAnswer",
					questions.get(currentQuestion).get("correctAnswer"));
			break;
		case -1:
			answer.put("asnwer", "-1");
			answer.put("question", Integer.toString(currentQuestion + 1)
					.toString());
			answer.put("corectAnswer",
					questions.get(currentQuestion).get("correctAnswer"));
			break;

		}
		try {
			Log.d("displayQ", "removing question " + currentQuestion);
			myAnswers.remove(currentQuestion);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d("displayQ", "Adding question " + currentQuestion
				+ " \n holding : " + answer);
		if (!answer.isEmpty()) {
			myAnswers.add(currentQuestion, answer);
		}

		// From here prepare to go to the next activity(results)
		 attentedQuestions = 0;
		TextView timeCounter = (TextView) findViewById(R.id.textTimer);
		String counter = timeCounter.getText().toString();
		
		// See how many question were attentd

		for (int i = 0; i < myAnswers.size(); i++) {

			String answerNumber = myAnswers.get(i).get("asnwer");
			Log.v("submitQuiz", " Inside Submit Quiz answer number"
					+ answerNumber);
			// blank answer
			if (answerNumber.equals("-1")) {
				// attentedQuestions--;
			} else {
				attentedQuestions++;
			}

			Log.v("submitQuiz", " You answer to " + attentedQuestions
					+ "questions out of " + myAnswers.size() + "at level "
					+ difficulty);

		}

		String secondsToSplit[] = counter.split(":");
		int secFromMin = Integer.parseInt(secondsToSplit[0].trim()) * 60;
		int sec = Integer.parseInt(secondsToSplit[1].trim());

		// Seconds
		int totalSecs = secFromMin + sec;

		// (Right Questions/Total Questions)
		// Total questions
		TextView totalQView = (TextView) findViewById(R.id.counter);
		String totalQuestionsToSplit[] = ((String) totalQView.getText())
				.split("/");
		int totalQuestion = Integer.parseInt(totalQuestionsToSplit[1]);

		// Get the right questions
		int score = 0;

		for (int i = 0; i < myAnswers.size(); i++) {
			myAnswers.get(i).get("question");
			String answerQuestion = (String) myAnswers.get(i).get("asnwer");
			String correctAnswer = myAnswers.get(i).get("corectAnswer");
			Log.d("GameActivity", answerQuestion + " vs " + correctAnswer);
			if (answerQuestion.equals(correctAnswer)) {
				score++;
			}

		}

		//
		// call to method to get final score
		double finalScore = getFinalScore(totalSecs, score, totalQuestion,difficulty);
		
		//Open connection with the database to insert the result 
		Log.d("GameActivity", "BEFORE ATTEMPTING OPENING THE DATABASE");
		db.open();
		Log.d("GameActivity", "AFTER ATTEMPTING OPENING THE DATABASE");
		
		long inserted = db.insertResult(finalScore, difficulty);
		Log.d("GameActivity", "Inserted result at row = " + inserted);
		
		//db.getResults();
		Cursor cursor = db.getResults();
		int indexKeyResult = (cursor.getColumnIndex(DBAdapter.KEY_RESULT));
		Log.d("GameActivity", "indexKeyResult = " + indexKeyResult);
		Log.d("GameActivity", "STORE RESULTs====== " + cursor.getString(indexKeyResult));
		db.close();

		

		Log.d("GameActivity", "FINAL SCORE====== " + finalScore);

		// Formula to get the final result

		int numberOfQuestionsComplete = myAnswers.size();
		Log.v("submitQuiz", "The counter stops at : " + counter
				+ "You complete " + attentedQuestions + "out of "
				+ numberOfQuestionsComplete + "Difficulty " + difficulty);
		
		
		//Parse the time taken to complete the quiz
		
		int secondsToMilliseconds=totalSecs*1000;
		int millisecond=300000-secondsToMilliseconds;
		
		int seconds = (int) (millisecond / 1000) % 60 ;
		int minutes = (int) ((millisecond / (1000*60)) % 60);
		
		Log.d("GameActivity", "minutes = "  + minutes +" seconds"+seconds);
		
		String secondString=String.valueOf(seconds);
		String minuteString=String.valueOf(minutes);
		
		String time= minuteString+":"+secondString;
		
		String finalScoreString=String.valueOf(finalScore);
		
		String scoreString=String.valueOf(score);
		
		String totalQuestionsString=String.valueOf(totalQuestion);
		String attentedQuestionsString= String.valueOf(attentedQuestions);
		Log.d("GameActivity", "finalScore = "  + finalScoreString);
		//prepare to send the intent
		
		//prepareIntent( null,finalScoreString, totalQuestionsString, scoreString, time);
		
		
		Intent intent = new Intent(getApplicationContext(),Result.class);
		
		db.open();
		String topScore=db.getTopScore();
		db.close();
		intent.putExtra(EXTRA_TOPSCORE, topScore);
		intent.putExtra(EXTRA_SCORE, finalScoreString);
		intent.putExtra(EXTRA_TOTAL_QUESTIONS, totalQuestionsString);
		intent.putExtra(EXTRA_RIGHT, scoreString);
		intent.putExtra(EXTRA_TIME, time);
		intent.putExtra(EXTRA_ATTENTED,attentedQuestionsString );
		
		startActivity(intent);
		
		/*Intent intent = new Intent(getApplicationContext(), Result.class);
		intent.putExtra("difficulty", difficulty);
		//intent.putExtra("questionNo", numberOfQ);
		//intent.putExtra("version", version);
	    startActivity(intent);*/

	}// end method submit

	// Method to get the final Score
	/**
	 * Method to get the final Score
	 * @param totalSecs
	 * @param score
	 * @param totalQuestion
	 * @param difficulty2
	 * @return
	 */
	public double getFinalScore(int totalSecs, int score, int totalQuestion,
			String difficulty2) {
		
		//casting to double
		double finalScore;
		double scoreDouble= (double) score;
		double totalQuestionsDouble=(double)totalQuestion;
		double totalSecsDouble=(double)totalSecs;
		
		if (difficulty2.equals("Easy")) 
		{
			if (totalSecs == 0)
			{
				finalScore = (scoreDouble/totalQuestionsDouble) * 1 + 10;
				
			} 
			else 
			{
				finalScore =  ((scoreDouble/totalQuestionsDouble)*totalSecsDouble);
				
			}

		} 
		else //level Medium
		{
			if (totalSecs == 0) 
			{
				finalScore = (scoreDouble/totalQuestionsDouble) * 1 + 15;
				
			}
			else 
			{
				finalScore = ((scoreDouble/totalQuestionsDouble) * totalSecsDouble) * 1.25;
				
			}

		}//end else
		
		Log.d("GameActivity", "INSIDE METHOD GETFINALSCORE====== "
				+ finalScore + "NON VARIABLE = "
				+ (score/totalQuestionsDouble) * 1 + 15);
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		//only return two digits
		return Double.parseDouble(df.format(finalScore));
	}// end method

	/**
	 * Method for the preparation to start na intent to go to the results activity
	 */
	
	public void prepareIntent(View view,String score,String totalQuestions,String correct, String time)
	{
		Intent intent = new Intent(getApplicationContext(),Result.class);
				
		intent.putExtra(EXTRA_SCORE, score);
		intent.putExtra(EXTRA_TOTAL_QUESTIONS, totalQuestions);
		intent.putExtra(EXTRA_RIGHT, correct);
		intent.putExtra(EXTRA_TIME, time);
		db.open();
		String topScore=db.getTopScore();
		db.close();
		intent.putExtra(EXTRA_TOPSCORE, topScore);
		startActivity(intent);
		
	}//end method
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	public void onRadioButtonClick(View v) {
		RadioButton button = (RadioButton) v;
		Toast.makeText(GameActivity.this, button.getText() + " was chosen.",
				Toast.LENGTH_SHORT).show();

	}

}
