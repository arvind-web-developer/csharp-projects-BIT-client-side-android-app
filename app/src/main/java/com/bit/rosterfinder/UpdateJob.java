package com.bit.rosterfinder;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bit.rosterfinder.app.AppConfig;
import com.bit.rosterfinder.parsers.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Arvind on 8/06/2015.
 */
public class UpdateJob extends Activity {
    TextView output;
    ProgressBar pb;
    List<GetJobDetails> tasks;

    EditText txtJob;
    EditText txtJobStatus;
    EditText txtComments;
    EditText txtCreatedAt;
    Button btnSave;
    Button btnDelete;

    String jobno = null;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // single job url
    //private static final String JOB_DETAILS_URL = "http://192.168.0.5:8080/bitWS_localhost/get_job_details.php";
    private static final String JOB_DETAILS_URL = AppConfig.URL_JOB_DETAILS;

    // url to update job
    //private static final String UPDATE_JOB_URL = "http://192.168.0.5:8080/bitWS_localhost/update_job.php";
    private static final String UPDATE_JOB_URL = AppConfig.URL_UPDATE_JOB;

    // url to delete job
    //private static final String DELETE_JOB_URL = "http://192.168.0.5:8080/bitWS_localhost/delete_job.php";
    private static final String DELETE_JOB_URL = AppConfig.URL_DELETE_JOB;


    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_JOB = "jobid";
    private static final String TAG_JOBSTATUS = "jobstatus";
    private static final String TAG_COMMENT = "comments";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

        // save button
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        // getting job details from intent
        Intent i = getIntent();

        // getting job id (jobid) from intent
        //jobno = i.getStringExtra(TAG_JOB);
        //jobno = i.getExtras().getString("message").toString();
        jobno = i.getExtras().getString("message");

		//Initialize the TextView for vertical scrolling
        output = (TextView) findViewById(R.id.textView);
        output.setMovementMethod(new ScrollingMovementMethod());


        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();

        invokeMenu();

        // save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // starting background task to update job
                new SaveJobDetails().execute();

                // after delete pass the control to ReadJobs Activity
                Intent intent = new Intent(UpdateJob.this, ReadJobs.class);
                startActivity(intent);
            }
        });

        // Delete button click event
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // deleting job in background thread
                new DeleteJob().execute();

                // after delete pass the control to ReadJobs Activity
                Intent intent = new Intent(UpdateJob.this, ReadJobs.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
            requestData(JOB_DETAILS_URL);
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }
    }

    private void requestData(String uri) {

        RequestPackage p = new RequestPackage();
        p.setMethod("POST");
        p.setUri(uri);


        p.setParam("jobid", jobno);

        GetJobDetails task = new GetJobDetails();
        task.execute(p);
    }

    protected void updateDisplay(String result) {
        //output.append(jobno + "\n");
        // Check for success tag
        int success;
        try {
            JSONObject json = new JSONObject(result);



            // check your log for json response
            Log.d("Single Job Details", json.toString());

            // json success tag
            success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                JSONArray jobs = json.getJSONArray("job");
                //jobs.length();
                //jobs.getJSONObject(0); // get first article in the array
                //jobs.getJSONObject(0).names(); // get first article keys [title,url,categories,tags]
                //jobs.getJSONObject(0).getString("jobid"); // return an job id
                //jobs.getJSONObject(0).getString("jobstatus"); // return an job status
                //jobs.getJSONObject(0).getString("comments"); // return an job comment

                // job with this jobid found
                // Edit Text
                txtJob = (EditText) findViewById(R.id.inputJob);
                txtJobStatus = (EditText) findViewById(R.id.inputJobStatus);
                txtComments = (EditText) findViewById(R.id.inputComments);

                // display job data in EditText
                //output.append(jobs.getJSONObject(0).names() + "\n");
                txtJob.setText(jobs.getJSONObject(0).getString(TAG_JOB));
                txtJobStatus.setText(jobs.getJSONObject(0).getString(TAG_JOBSTATUS));
                txtComments.setText(jobs.getJSONObject(0).getString(TAG_COMMENT));


            } else {
                // job with jobid not found

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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


    private class GetJobDetails extends AsyncTask<RequestPackage, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            String content = HttpManager.postDataPackage(params[0]);
            return content;
        }


        protected void onPostExecute(String result) {

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            updateDisplay(result);

            // check your log for json response
           // Log.d("Single Job Details", result.toString());

        }

    }



    /**
     * Background Async Task to  Save product Details
     * */
    private class SaveJobDetails extends AsyncTask<RequestPackage, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UpdateJob.this);
            pDialog.setMessage("Saving job ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Saving job
         * */
        protected String doInBackground(RequestPackage... args) {

            // getting updated data from EditTexts
            String job = txtJob.getText().toString();
            String jobstatus = txtJobStatus.getText().toString();
            String comment = txtComments.getText().toString();


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_JOB, job));
            params.add(new BasicNameValuePair(TAG_JOBSTATUS, jobstatus));
            params.add(new BasicNameValuePair(TAG_COMMENT, comment));



            // sending modified data through http request
            // Notice that update job url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(UPDATE_JOB_URL,
                    "POST", params);


            // check your log for json response
            Log.d("Saved Data ", json.toString());

            // check json success tag
            try {
                //JSONObject json = new JSONObject(content);
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about job update
                    setResult(100, i);


                    //startActivity(i);
                    finish();
                } else {
                    // failed to update product
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
            // dismiss the dialog once job updated
            pDialog.dismiss();

        }
    }


    /*****************************************************************
     * Background Async Task to Delete Job
     * */
    private class DeleteJob extends AsyncTask<RequestPackage, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UpdateJob.this);
            pDialog.setMessage("Deleting Job...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deleting job
         * */
        protected String doInBackground(RequestPackage... args) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("jobid", jobno));

                // getting job details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        DELETE_JOB_URL, "POST", params);

                // check your log for json response
                Log.d("Delete Job ", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // job successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = getIntent();
                    // send result code 100 to notify about job deletion
                    setResult(100, i);

                    finish();
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
            // dismiss the dialog once job deleted
            pDialog.dismiss();

        }

    }
}