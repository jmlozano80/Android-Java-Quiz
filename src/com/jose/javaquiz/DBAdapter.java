package com.jose.javaquiz;

import java.util.ArrayList;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter
{
	//global variables
	
	static final String KEY_ROWID="_id";
	static final String KEY_QUESTION="questionNumber";
	static final String KEY_ANSWER1="answer1";
	static final String KEY_ANSWER2="answer2";
	static final String KEY_ANSWER3="answer3";
	static final String KEY_ANSWER4="answer4";
	static final String KEY_CORRECTANSWER="correctAnswer";
	static final String KEY_VERSION="version";
	static final String KEY_DIFFICULTY="difficulty";
	static final String TAG="DBAdapter";
	
	 static final String KEY_ID = "_id";
	 static final String KEY_RESULT = "result";
	 static final String KEY_LEVEL = "level";
	
	//database 
	static final String DATABASE_NAME="MyDB";
	static final String DATABASE_TABLE="question";
	static final String DATABASE_TABLE1="results";
	static int databaseVersion;
	
	//static final String DATABASE_CREATE="CREATE table contacts (_id integer primary key autoincrement, questionNumber integer not null,answer1 text not null, );";
	
	//string to create the database
	static final String  DATABASE_CREATE = "CREATE table "
										   + DATABASE_TABLE
										   + "("
										   + KEY_ROWID
										   + " integer primary key autoincrement,"
										   + KEY_QUESTION
										   + " text not null,"
										   + KEY_ANSWER1
										   + " text not null, "
										   + KEY_ANSWER2
										   + " text not null, "
										   + KEY_ANSWER3
										   + " text not null, "
										   + KEY_ANSWER4
										   + " text not null, "
										   + KEY_CORRECTANSWER
										   + " integer not null, "
										   + KEY_VERSION
										   + " integer not null,"
										   + KEY_DIFFICULTY
										   + " text not null"
										   + ");" ;
	static final String  CREATE_TABLE_RESULTS = "CREATE table "
			   + DATABASE_TABLE1
			   + "("
			   + KEY_ROWID
			   + " integer primary key autoincrement,"
			   + KEY_RESULT
			   + " text not null,"
			   + KEY_LEVEL
			   + " text not null "
			   + ");" ;
	
	final Context context;
	
	DatabaseHelper DBHelper;
	SQLiteDatabase db;
	
	//Constructor
	public DBAdapter(Context ctx, int version)
	{
		databaseVersion = version;
		this.context=ctx;
		DBHelper = new DatabaseHelper(context);
	
	}//end contructor
	
	 /**
     * a private class that extended the SQLiteOpenHelper class,which is a helper class in Android to manage database 
     * creation and version management. In particular,you overrode the onCreate() and onUpgrade() methods:
     *
     */
	public static  class  DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context,DATABASE_NAME,null,databaseVersion);
		}
		
		//Create the new table in case in not created yet
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "CREATING DATABASE");
			try
			{
				db.execSQL(DATABASE_CREATE);
				db.execSQL(CREATE_TABLE_RESULTS);
			}
			catch(SQLException sql)
			{
				Log.d(TAG, "SQL Exception..... :(((((((((");
				sql.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					 + newVersion + ", which will destroy all old data");
					 db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
					 db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE1);
					 onCreate(db);
		}
		

		
		
	}//end inner class DatabaseHelper
	
		//---opens the database---
	 public DBAdapter open() throws SQLException 
	 {
		 db = DBHelper.getWritableDatabase();
		 return this;
	 }
	 
	 public int getDatabaseVersion() {
		 return databaseVersion;
	 }
	 
	 public void setDatabaseVersion(int version) {
		 databaseVersion = version;
	 }
	 /*	 
	 public void dropAllTables() {
		 db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
	 }
	 */
	//---closes the database---
	 public void close() 
	 {
		 DBHelper.close();
	 }
	 
	 

	 
	 /**
	  * Get the questions
	  * @return -1 for no version record
	  */
	 public int getQuestionsVersion() {
		 
		 int questionVersion = -1;
		 
		Cursor cursor = db.query(DATABASE_TABLE, new String[] { KEY_VERSION}, null, null, null, null, null);
		
		if (cursor.moveToFirst()) {
			questionVersion =  cursor.getInt(cursor.getColumnIndex(KEY_VERSION));
		} 
		
		 
		 
		return questionVersion;
		 
	 }
	 
	 
	 /*
	  * This method proccess thequestions
	  */
	public void processQuestions(ArrayList<Map<String, String>> questionList) {
		for (int i = 0; i < questionList.size(); i++) {
			
			String question = questionList.get(i).get(KEY_QUESTION);
			String answer1 = questionList.get(i).get(KEY_ANSWER1);
			String answer2 = questionList.get(i).get(KEY_ANSWER2);
			String answer3 = questionList.get(i).get(KEY_ANSWER3);
			String answer4 = questionList.get(i).get(KEY_ANSWER4);
			String correctAnswer = questionList.get(i).get(KEY_CORRECTANSWER);
			String difficulty = questionList.get(i).get(KEY_DIFFICULTY);
			String version = questionList.get(i).get(KEY_VERSION);
			
			long insertedRow = insertQuestions(question, answer1, answer2, answer3, answer4, correctAnswer, difficulty, version);
			Log.d(TAG, "Inserted row " + insertedRow);
		}
	}
	
	
	/**
	 * This method get the questions to be display
	 * @param difficulty
	 * @param numberOfQuestions
	 * @return
	 */
	public Cursor getQuestions(String difficulty, int numberOfQuestions) {
		
		
		
		/*Cursor mCursor = db.query(true, DATABASE_TABLE, null, null, null, null, null, null, null);*/
		
		/*Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {
		        KEY_ROWID, KEY_QUESTION, KEY_ANSWER1, KEY_ANSWER2, KEY_ANSWER3, KEY_ANSWER4, KEY_CORRECTANSWER, KEY_DIFFICULTY}, KEY_DIFFICULTY + "=" + difficulty,
		        null, null, null, null, "RANDOM() LIMIT " + Integer.toString(numberOfQuestions));*/
		
		String query = "SELECT * FROM " + DATABASE_TABLE + " WHERE " + KEY_DIFFICULTY + " = '" + difficulty + "' ORDER BY RANDOM() LIMIT " + Integer.toString(numberOfQuestions+1) + ";";
		Log.d(TAG, "Query = " + query);
		Cursor mCursor = db.rawQuery(query, null);
		
		mCursor.moveToFirst();
		
		/**/
		
		/*db.query(DATABASE_TABLE, new String[] {KEY_ROWID }, selection, selectionArgs, groupBy, having, orderBy, limit)*/
		
		return mCursor;
		
	}
	 
	/**
	 * This method insert the questions into the database
	 * @param question
	 * @param answer1
	 * @param answer2
	 * @param answer3
	 * @param answer4
	 * @param correctAnswer
	 * @param difficulty
	 * @param version
	 * @return
	 */
	//---insert a data into the database---
	 public long insertQuestions(String question,String answer1, String answer2,String answer3, String answer4,String correctAnswer,String difficulty,String version) 
	 {
		 
		 int correctAnswerInt=Integer.parseInt(correctAnswer);
		 int versionInt= Integer.parseInt(version);
		 
		 ContentValues initialValues = new ContentValues();
		 
		 initialValues.put(KEY_QUESTION, question);
		 initialValues.put(KEY_ANSWER1, answer1);
		 initialValues.put(KEY_ANSWER2, answer2);
		 initialValues.put(KEY_ANSWER3, answer3);
		 initialValues.put(KEY_ANSWER4, answer4);
	 
		 initialValues.put(KEY_CORRECTANSWER, correctAnswerInt);
		 initialValues.put(KEY_VERSION, versionInt);
	 initialValues.put(KEY_DIFFICULTY, difficulty);
	 return db.insert(DATABASE_TABLE, null, initialValues);
	 }
	 
	 
	
	 //---insert a contact into the database---
	    public long insertResult(double result, String level) 
	    {
	        ContentValues initialValues = new ContentValues();
	        initialValues.put(KEY_RESULT, result);
	        initialValues.put(KEY_LEVEL, level);
	        
	     
	     //   initialValues.put(KEY_TIMESTAMP,(DATETIME('now')) );
	        
	        return db.insert(DATABASE_TABLE1, null, initialValues);
	    }
	
	    
	  //---retrieves all the contacts---
	    public Cursor getResults()
	    {
	    	String query = "SELECT * FROM " + DATABASE_TABLE1 + " LIMIT " + 5 + ";";
	    	Log.d("ResultAdapter", "Query = " + query);
	    	Cursor mCursor = db.rawQuery(query, null);
	    	if (mCursor.moveToFirst()) Log.d("DBAdapter", "GetResults() - moved cursor to first position");
	    	return mCursor;
	    }
	 
	 
/*
 * Get Top Score	 
 */
	 public  String getTopScore()
	 {
		 String topScore = "0";
		 Log.d("ResultAdapter", "Before rawQuery v2");
		 Cursor cursor=db.rawQuery("SELECT MAX(result) result FROM results", null);
		
		 Log.d("ResultAdapter", "After rawQuery");
		 if (cursor.moveToNext()) Log.d("ResultAdapter", "Moved to first.");
		 else Log.d("ResultAdapter", "Couldn't move to first.");
		 if (! cursor.isNull(cursor.getColumnIndex(KEY_RESULT))) {
		 topScore=cursor.getString(cursor.getColumnIndex(KEY_RESULT));
		 Log.d("ResultAdapter", "The top result is " + topScore);
		  
		 } else {
			 Log.d("ResultAdapter", "The result is null");
		 }
		 
		  return topScore;
	 }
	  
	//---retrieves all the contacts---
/*	 public Cursor getAllContacts()
	 {
		 return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME,
			 KEY_EMAIL}, null, null, null, null, null);
	 }*/
}//end class DBAdapter
