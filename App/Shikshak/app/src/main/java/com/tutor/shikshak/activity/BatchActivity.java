package com.tutor.shikshak.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.tutor.shikshak.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.tutor.shikshak.other.Constants.addBatchUrl;
import static com.tutor.shikshak.other.Constants.getCoaching;
import static com.tutor.shikshak.other.Constants.getSubject;

public class BatchActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private EditText batch_name, batch_time, batch_fees;
    private String coachingB, batchB, subjectB, timeB, feesB;
    private Button buttonBatch;
    private Spinner coaching_spinner, subject_spinner;
    private ArrayList coachingArray = new ArrayList();
    private ArrayList subjectArray = new ArrayList();

    JSONObject jsonObj = new JSONObject();
    private String userEmail = new LoginActivity().userEmail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle();

        batch_name = (EditText) findViewById(R.id.batchName);
        batch_time = (EditText) findViewById(R.id.time);
        batch_fees = (EditText) findViewById(R.id.fees);
        coaching_spinner = (Spinner) findViewById(R.id.spinner_coaching_batch);
        subject_spinner = (Spinner) findViewById(R.id.spinner_subject);
        buttonBatch = (Button) findViewById(R.id.button_batch);
        buttonBatch.setOnClickListener(this);

        sendEmail();
        coachingArray.add("Choose Coaching");
        subjectArray.add("Choose Subject");

        ArrayAdapter<String> coachingAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, coachingArray);
        coachingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coaching_spinner.setAdapter(coachingAdapter);

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subjectArray);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject_spinner.setAdapter(subjectAdapter);
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle("Create a Batch");
    }

    @Override
    public void onClick(View v) {
        coachingB = String.valueOf(coaching_spinner.getSelectedItem());
        batchB = batch_name.getText().toString();
        subjectB = String.valueOf(subject_spinner.getSelectedItem());
        timeB = batch_time.getText().toString();
        feesB = batch_fees.getText().toString();

        if(!coachingB.equals("Choose Coaching") && !batchB.equals(null) && !subjectB.equals("Choose Subject") && !timeB.equals(null) && !feesB.equals(null)) {
            try {
                jsonObj.put("coaching_name", coachingB);
                jsonObj.put("batch_name", batchB);
                jsonObj.put("batch_sub", subjectB);
                jsonObj.put("batch_time", timeB);
                jsonObj.put("batch_fee", feesB);
                jsonObj.put("email", userEmail);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jsonObj.length() > 0) {
                new SendDataToServer().execute(String.valueOf(jsonObj));
            }
        }
        else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(BatchActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void sendEmail() {
        JSONObject json_obj = new JSONObject();

        try {
            json_obj.put("email", userEmail);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        if (json_obj.length() > 0) {
            new GetCoachingFromServer().execute(String.valueOf(json_obj));
            new GetSubjectFromServer().execute();
        }
    }

    private class SendDataToServer extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
            String JsonDATA = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(addBatchUrl);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                writer.close();
               // Log.e("error---", JsonDATA);

                //getting the response
                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    return null;
                }

                JsonResponse = buffer.toString();
                String str = JsonResponse.toString().trim().toLowerCase();

                // Log.e("response---", str);

                if(str.equals("true") || str.equals(1) || str.equals("1")){

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
//                                Intent i = new Intent(CoachingActivity.this, HomeActivity.class);
//                                startActivity(i);
                            finish();  //close this activity
                        }
                    });
                }

                else {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(BatchActivity.this, "Error in Batch Creation", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                return JsonResponse;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("ERROR", "Error closing stream", e);
                    }
                }
            }

            return null;
        }
    }

    private class GetCoachingFromServer extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
            String JsonDATA = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(getCoaching + userEmail);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                writer.close();

                //getting the response
                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    return null;
                }

                JsonResponse = buffer.toString();
                JsonResponse = JsonResponse.replace("[","").replace("]","");

                String[] city=JsonResponse.split(",");

                for(int i=0;i<city.length;i++){
                    JSONObject emp=(new JSONObject(city[i]));
                    String response=emp.getString("name").toUpperCase().trim();
                    String.valueOf(coachingArray.add(response));
                }
                //coachingArray.remove("Choose Coaching");
                Log.e("coaching array", String.valueOf(coachingArray));

                return JsonResponse;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("ERROR", "Error closing stream", e);
                    }
                }
            }

            return null;
        }
    }

    private class GetSubjectFromServer extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(getSubject);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                //getting the response
                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    return null;
                }

                JsonResponse = buffer.toString();
                JsonResponse = JsonResponse.replace("[","").replace("]","");

                String[] city=JsonResponse.split(",");

                for(int i=0;i<city.length;i++){
                    JSONObject emp=(new JSONObject(city[i]));
                    String response=emp.getString("subject_name").toUpperCase().trim();
                    String.valueOf(subjectArray.add(response));
                }
                //subjectArray.remove("Choose City");
                Log.e("subject array", String.valueOf(subjectArray));

                return JsonResponse;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("ERROR", "Error closing stream", e);
                    }
                }
            }

            return null;
        }
    }
}

