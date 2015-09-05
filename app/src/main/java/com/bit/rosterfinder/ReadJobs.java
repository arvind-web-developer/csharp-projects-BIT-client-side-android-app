package com.bit.rosterfinder;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bit.rosterfinder.app.AppConfig;
import com.bit.rosterfinder.helper.SessionManager;
import com.bit.rosterfinder.model.Job;
import com.bit.rosterfinder.parsers.JobJSONParser;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;

public class ReadJobs extends ListActivity implements AdapterView.OnItemClickListener {

	TextView output;
    ProgressBar pb;
    List<MyTask> tasks;

    List<Job> jobList;

	private Button btnLogout;
	private SessionManager session;


    ListView lstJobs = null;

    private MenuItem mItem = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_jobs);

		 btnLogout = (Button) findViewById(R.id.btnLogout);

		// session manager
		session = new SessionManager(getApplicationContext());

		//if (!session.isLoggedIn()) {
		//	logoutUser();
		//}

		//String name = "Test User";
		//String email = "test.user@demo.com";

		// Displaying the user details on the screen
		//txtName.setText(name);
		//txtEmail.setText(email);

		//	Initialize the TextView for vertical scrolling
		//output = (TextView) findViewById(R.id.textView);
		//output.setMovementMethod(new ScrollingMovementMethod());

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);


        lstJobs = (ListView) findViewById(android.R.id.list);

        tasks = new ArrayList<>();

        invokeMenu();

		// Logout button click event
		btnLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				logoutUser();
			}
		});
	}


	private void logoutUser() {
        /**
         * Logging out the user. Will set isLoggedIn flag to false in shared
         * preferences Clears the user data from sqlite users table
         * */
		session.setLogin(false);

		//db.deleteUsers();

		// Launching the login activity
		Intent intent = new Intent(ReadJobs.this, Login.class);
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

        // get a reference to the item you want to click manually
        //mItem = menu.findItem(item.getItemId());

        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_get_data) {
            invokeMenu();
        }
		return false;
	}

    private void invokeMenu() {
        if (isOnline()) {
            requestData(AppConfig.URL_JOBS);
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }
    }

    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    protected void updateDisplay() {
        //Use JobAdapter to display data
        JobAdapter adapter = new JobAdapter(this, R.layout.item_job, jobList);

        lstJobs.setAdapter(adapter);
        lstJobs.setOnItemClickListener(this);
        //setListAdapter(adapter);


	}

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {

       // startActivity(new Intent(this, UpdateJob.class));
      //  Toast.makeText(getApplicationContext(),
      //              "Job : " + pos, Toast.LENGTH_LONG).show();

        //Display job name in the TextView widget
        Job job = jobList.get(pos);

        Toast.makeText(getApplicationContext(),
                "SELECTED Job id=: " + job.getJobid(), Toast.LENGTH_LONG).show();

        Intent i = new Intent(this, UpdateJob.class);
        i.putExtra("message", job.getJobid());
        //setResult(RESULT_OK, i);

        startActivity(i);
        finish();

    }

    private class MyTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			//updateDisplay("Starting task");


            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
		}

		@Override
		protected String doInBackground(String... params) {

            String content = HttpManager.getData(params[0]);
			return content;
		}

		@Override
		protected void onPostExecute(String result) {

            jobList = JobJSONParser.parseFeed(result);
            updateDisplay();

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }
		}

        @Override
        protected void onProgressUpdate(String... values) {
            //updateDisplay(values[0]);
        }
    }
}
