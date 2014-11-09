package com.jose.javaquiz;

import java.util.Map;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	String userName,userEmail,userPassword,userRePassword;
	Activity activity = this;
	ProgressDialog  progress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		//progress=new ProgressDialog(this);
	}

	/*@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first

	    progress.dismiss();
	}*/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}
	
	
	
	public void getFormData(View button)
	{
		System.out.println("Inside the method getFormData");
		
		EditText editTextUserName = (EditText) findViewById(R.id.userNameTxt);
		userName = (editTextUserName.getText().toString()).trim();
		
		EditText editTextUserPasswordTxt = (EditText) findViewById(R.id.userPasswordTxt);
		userPassword = (editTextUserPasswordTxt.getText().toString()).trim();
		
		Log.d("progressProblem", "Showing progress dialog.");
		//Progress Dialog
		progress = ProgressDialog.show(this, "", "Login...");
		
	
			final Firebase fireUser = new Firebase("https://blistering-fire-1687.firebaseio.com/users/" + userName);
			
			fireUser.addListenerForSingleValueEvent(new ValueEventListener() {

				@Override
				public void onCancelled(FirebaseError arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onDataChange(DataSnapshot arg0) {
					// TODO Auto-generated method stub
					Object userSnapshot = arg0.getValue();
					
					if (userSnapshot == null) {
						

						Toast.makeText(getApplicationContext(), "No credentials to login", Toast.LENGTH_SHORT).show();
						
					}
					else
						{
							String obtainedUserName = (String)((Map)userSnapshot).get("userName");
							Log.d("test", "obtainedUserName = " + obtainedUserName);
							if(userPassword.equals((String)((Map)userSnapshot).get("userPassword")))
							{
								progress.cancel();
								Toast.makeText(activity, "credentials ok = " , 0).show();
								Intent intent = new Intent(activity, Sett.class);
								
							    startActivity(intent);
							}
							else
							{
								
								Toast.makeText(activity, "Not credential ok" , 0).show();
								
							}
						}
					progress.cancel();
						
					
						// There is an user with this id
						//Toast.makeText(activity, "Username is taken.", Toast.LENGTH_LONG).show();
						
					
				}
				
			});
			
			//
		
		System.out.println("The userName is : "+userName+".\nThe email is : "+userEmail+".\nThe password is : "
		+userPassword+".\nThe re-password is :" +userRePassword );
	}//end method
	
	
}
