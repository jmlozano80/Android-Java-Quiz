package com.jose.javaquiz;





import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class Sett extends Activity {

	
	public final static String EXTRA_NUMBER_OF_QUESTIONS="com.jose.javaquiz.NUMBER_OF_QUESTIONS";
	public final static String EXTRA_LEVEL="com.jose.javaquiz.LEVEL";
	private Spinner	spinner1,spinner2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		//Spinners
		/*spinner1 = (Spinner) findViewById(R.id.spinnerNumberOfQuestions);
        List<String> list = new ArrayList<String>();
        list.add("Android");
        list.add("Java");*/
		Spinner spinnerDifficulty = (Spinner) findViewById(R.id.spinnerDifficulty);
		ArrayAdapter<CharSequence> spinnerDifficultyAdapter = ArrayAdapter.createFromResource(
		            this, R.array.arrayDifficulty, R.layout.spinner_layout);
		spinnerDifficultyAdapter.setDropDownViewResource(R.layout.spinner_layout);
		spinnerDifficulty.setAdapter(spinnerDifficultyAdapter);
		
		Spinner spinnerNumberOfQuestions = (Spinner) findViewById(R.id.spinnerNumberOfQuestions);
		ArrayAdapter<CharSequence> spinnerNumberOfQuestionsAdapter = ArrayAdapter.createFromResource(
		            this, R.array.arrayNumberOfQuestions, R.layout.spinner_layout);
		spinnerNumberOfQuestionsAdapter.setDropDownViewResource(R.layout.spinner_layout);
		spinnerNumberOfQuestions.setAdapter(spinnerNumberOfQuestionsAdapter);
	}//end method Oncreate
	
	/**
	 * 
	 */
	 public void addListenerOnSpinnerItemSelection() 
	 {
			spinner1 = (Spinner) findViewById(R.id.spinnerNumberOfQuestions);
			//spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
		  }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

	
	/**
	 * Method to get the choosen elements of the spinner  and pass it to the afterlogin activity
	 */
	
	public void submit(View view)
	{
		//Get the selected items
		spinner1 = (Spinner) findViewById(R.id.spinnerNumberOfQuestions);
		spinner2= (Spinner) findViewById(R.id.spinnerDifficulty);
		String numberOfQuestions= spinner1.getSelectedItem().toString();
		String difficulty = spinner2.getSelectedItem().toString();
		
		//make toast to see the selected Items
		
		Toast.makeText(getApplicationContext(), "The level is " +difficulty+"\n"+
												"Number of questions "+numberOfQuestions, Toast.LENGTH_LONG).show();
		//send data to after_login
		Intent intent = new Intent(this,AfterLogin.class);
		intent.putExtra(EXTRA_NUMBER_OF_QUESTIONS, numberOfQuestions);
		intent.putExtra(EXTRA_LEVEL, difficulty);
		startActivity(intent);
		
	}//end method
	
}//end class
