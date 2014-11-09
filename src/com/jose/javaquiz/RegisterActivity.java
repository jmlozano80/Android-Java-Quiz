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
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity
{
	String userName,userEmail,userPassword,userRePassword;
	Activity activity = this;
	ProgressDialog  progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}
	/**
	 * 
	 */
	public void getFormData(View button)
	{
		System.out.println("Inside the method getFormData");
		
		EditText editTextUserName = (EditText) findViewById(R.id.userNameTxt);
		userName = (editTextUserName.getText().toString()).trim();
		
		EditText editTextUserEamilTxt = (EditText) findViewById(R.id.userEmailTxt);
		userEmail = (editTextUserEamilTxt.getText().toString()).trim();
		
		EditText editTextUserPasswordTxt = (EditText) findViewById(R.id.userPasswordTxt);
		userPassword = (editTextUserPasswordTxt.getText().toString()).trim();
		
		EditText editTextUserRePasswordTxt = (EditText) findViewById(R.id.userRePasswordTxt);
		userRePassword = (editTextUserRePasswordTxt.getText().toString()).trim();
		
		//Progress Dialog
				progress = ProgressDialog.show(this, "", "Registering...");
		if(!userPassword.equals(userRePassword))
		{	progress.dismiss();
			Toast.makeText(getApplicationContext(), "Passwords mismacth", Toast.LENGTH_SHORT).show();
		} else {
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
						//There is no user with this id
						fireUser.child("/userEmail/").setValue(userEmail);
						fireUser.child("/userPassword/").setValue(userPassword);
						progress.dismiss();
						Intent intent = new Intent(activity, LoginActivity.class);
					    startActivity(intent);
						
					} else {
						// There is an user with this id
						//Toast.makeText(activity, "Username is taken.", Toast.LENGTH_LONG).show();
						progress.dismiss();
						String password = (String)((Map)userSnapshot).get("userPassword");
						Toast.makeText(activity, "Username taken and password = " + password, 0).show();
						Intent intent = new Intent(activity, LoginActivity.class);
					    startActivity(intent);
					}
				}
				
			});
			
		}
		
		System.out.println("The userName is : "+userName+".\nThe email is : "+userEmail+".\nThe password is : "
		+userPassword+".\nThe re-password is :" +userRePassword );
	}//end method
	
	
	/**
	 * 
	 * @param view
	 */
	public void goToLoginActivity(View view)
	{
		
	}

}
