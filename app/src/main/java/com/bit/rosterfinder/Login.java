package com.bit.rosterfinder;


import com.bit.rosterfinder.app.AppConfig;
import com.bit.rosterfinder.helper.EmailValidator;
import com.bit.rosterfinder.helper.SessionManager;
import com.bit.rosterfinder.parsers.JSONParser;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Login extends Activity implements OnClickListener{
	
	private EditText user, pass;
	private Button mSubmit, mRegister;
	
	 // Progress Dialog
    private ProgressDialog pDialog;
 
    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    private String username;
    private String password;

    private EmailValidator emailValidator;
    
    //php login script location:

    
    //testing on Emulator:
    //private static final String LOGIN_URL = "http://192.168.0.4:8080/webservice/login.php";
	private static final String LOGIN_URL = AppConfig.URL_LOGIN;

    
    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

	private SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		//setup input fields
		user = (EditText)findViewById(R.id.username);
		pass = (EditText)findViewById(R.id.password);
		
		//setup buttons
		mSubmit = (Button)findViewById(R.id.btnLogin);
		mRegister = (Button)findViewById(R.id.btnRegister);
		
		//register listeners
		mSubmit.setOnClickListener(this);
		mRegister.setOnClickListener(this);

		// Session manager
		session = new SessionManager(getApplicationContext());

		// Check if user is already logged in or not
		if (session.isLoggedIn()) {
			// User is already logged in. Take him to main activity
			Intent intent = new Intent(Login.this, ReadJobs.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnLogin:
            username = user.getText().toString();
            password = pass.getText().toString();

            emailValidator = new EmailValidator();
            boolean valid = emailValidator.validate(username);

            // Check for correct data in the form
            if ((username.trim().length() > 0 && password.trim().length() > 0) && valid) {
                
                // login user
                new AttemptLogin().execute();
            } else {
                // Prompt user to enter credentials
                Toast.makeText(getApplicationContext(),
                        "Please enter the credentials!", Toast.LENGTH_LONG)
                        .show();
            }
			break;
		case R.id.btnRegister:
				Intent i = new Intent(this, Register.class);
				startActivity(i);
			break;

		default:
			break;
		}
	}
	
	class AttemptLogin extends AsyncTask<String, String, String> {

		 /**
         * Before starting background thread Show Progress Dialog
         * */
		boolean failure = false;
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		
		@Override
		protected String doInBackground(String... args) {
			// TODO Auto-generated method stub
			 // Check for success tag
            int success;
            //String username = user.getText().toString();
            //String password = pass.getText().toString();


            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

                Log.d("request!", "starting");
                // getting Job details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                       LOGIN_URL, "POST", params);

                // check your log for json response
                Log.d("Login attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                	Log.d("Login Successful!", json.toString());
                	Intent i = new Intent(Login.this, ReadJobs.class);
                	finish();
    				startActivity(i);
					// Create login session
					session.setLogin(true);
                	return json.getString(TAG_MESSAGE);
                }else{
                	Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                	return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
			
		}
		/**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
            	Toast.makeText(Login.this, file_url, Toast.LENGTH_LONG).show();
            }
 
        }
		
	}
		 

}
