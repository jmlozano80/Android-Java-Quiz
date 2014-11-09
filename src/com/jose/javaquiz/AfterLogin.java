package com.jose.javaquiz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class AfterLogin extends Activity {

	private DBAdapter db;
	static String version = null;
	
	//values from the setting activity
	Intent intent;
	Integer numberOfQ;
	String difficulty;
	
	ArrayList<Map<String, String>> questions = new ArrayList<Map<String,String>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_after_login);
		
		intent =getIntent();
		numberOfQ = Integer.parseInt(intent.getStringExtra(Sett.EXTRA_NUMBER_OF_QUESTIONS));
		difficulty = intent.getStringExtra(Sett.EXTRA_LEVEL);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.after_login, menu);
		return true;
	}
	
	public void goSettings(View view)
	{
		Intent intent = new Intent(getApplicationContext(), Sett.class);
	    startActivity(intent);
		
	}//end method
	
	/**
	 * this method starts the game
	 * @param view
	 */
	public void startGame(View view)
	{
		// TODO Obtain the difficulty
		
		
		// TODO Number of questions (Settings)
		
		
		
		// TODO VERSION CHECK
		
		Firebase firebaseVersionCheck = new Firebase("https://blistering-fire-1687.firebaseio.com/version/");
		
		firebaseVersionCheck.addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onCancelled(FirebaseError arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onDataChange(DataSnapshot arg0) {
				version = arg0.getValue().toString();
				Log.d("Firebase", "version = " + version);
				
				
				db = new DBAdapter(getApplicationContext(), Integer.parseInt(version));
				
				db.open();
				int questionVersion = db.getQuestionsVersion();
				db.close();
				Log.d("Firebase", "questionVersion = " + questionVersion);
				
				if (Integer.parseInt(version) == questionVersion)
				{	
					//not setting default data
					if(difficulty==null & numberOfQ==null)
					{
						difficulty="Easy";
						numberOfQ=10;
					}
					Intent intent = new Intent(getApplicationContext(), GameActivity.class);
					intent.putExtra("difficulty", difficulty);
					intent.putExtra("questionNo", numberOfQ);
					intent.putExtra("version", version);
				    startActivity(intent);
					
				} else
				{ // The version doesn't match
					// Insert Data
					
					Firebase firebase = new Firebase("https://blistering-fire-1687.firebaseio.com/");
					firebase.addListenerForSingleValueEvent(new ValueEventListener() {

						@Override
						public void onCancelled(FirebaseError arg0) {}

						@Override
						public void onDataChange(DataSnapshot arg0) {
							
							Object obj = arg0.getValue();
							
							
							Map<String, String> difficultiesMap = (Map)((Map)obj).get("difficulty");
							
							System.out.println("Difficulties map =  " +  difficultiesMap);
							
							Set difficultiesSet = difficultiesMap.keySet();
							
							Iterator difficultiesIterator = difficultiesSet.iterator();
							
							for (int i = 0; i < difficultiesSet.size(); i++)
							{
								Object difficultyObj = difficultiesIterator.next();
								
								String difficulty = difficultyObj.toString();
								
								System.out.println("Difficulty = " + difficulty.toString());
								
								Object questionObj = difficultiesMap.get(difficulty);
								
								Map<String,String> questionMap = (Map)((Map)questionObj);
								
								//System.out.println("questionMap = " + questionMap);
								
								Set questionSet = questionMap.keySet();
								
								int numberOfQuestions = questionSet.size();
								
								Iterator questionIterator = questionSet.iterator();
								
								for (int j = 0; j < numberOfQuestions; j++)
								{
									Object currentQuestion = questionIterator.next();
									System.out.println("current Question " + currentQuestion.toString());
									
									String[] currentSplitQuestion = currentQuestion.toString().split("q");
									int currentQuestionNumber = Integer.parseInt(currentSplitQuestion[1]);
									System.out.println("Current q no = " + currentQuestionNumber);
									
									// Obtain the details of the current question
									
									//Map<String,String> currentQuestionMap = (Map)((Map)currentQuestion);
									
									
									
									Map<String,String> temporaryQuestionMap = new HashMap<String,String>();
									
									Object testObj = questionMap.get(currentQuestion);
									
									//String answer1 = currentQuestionMap.get("answer1");
									
									String answer1 = (String)((Map)testObj).get("answer1");
									temporaryQuestionMap.put(DBAdapter.KEY_ANSWER1, answer1);
									String answer2 = (String)((Map)testObj).get("answer2");
									temporaryQuestionMap.put(DBAdapter.KEY_ANSWER2, answer2);
									String answer3 = (String)((Map)testObj).get("answer3");
									temporaryQuestionMap.put(DBAdapter.KEY_ANSWER3, answer3);
									String answer4 = (String)((Map)testObj).get("answer4");
									temporaryQuestionMap.put(DBAdapter.KEY_ANSWER4, answer4);
									
									String question = (String)((Map)testObj).get("question");
									temporaryQuestionMap.put(DBAdapter.KEY_QUESTION, question);
									
									String correctAnswer = (String)((Map)testObj).get("correctAnswer");
									temporaryQuestionMap.put(DBAdapter.KEY_CORRECTANSWER, correctAnswer);
									
									temporaryQuestionMap.put(DBAdapter.KEY_DIFFICULTY, difficulty);
									temporaryQuestionMap.put(DBAdapter.KEY_VERSION, version);
									
									questions.add(temporaryQuestionMap);
									
									
								}
								
								
							}
						
							db.open();
							/*db.setDatabaseVersion(Integer.parseInt(version));*/
							db.processQuestions(questions);
							db.close();
							Log.d("AfterLogin", "Done with processing questions.");
							
							Intent intent = new Intent(getApplicationContext(), GameActivity.class);
							intent.putExtra("difficulty", difficulty);
							intent.putExtra("questionNo", numberOfQ);
							intent.putExtra("version", version);
						    startActivity(intent);
							
							
						}
						
					});
					
					
				}
				
			}
			
		});
		
		
		
		
		
		
	}//end method

}//end class
