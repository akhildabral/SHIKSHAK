package com.tutor.shikshak.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

import static com.tutor.shikshak.other.Constants.getBatch;
import static com.tutor.shikshak.other.Constants.getCoaching;
import static com.tutor.shikshak.other.Constants.getStudent;
import static com.tutor.shikshak.other.Constants.getTeacher;

public class AddStudentActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private EditText batch_subject, batch_time;
    private String coaching, batch, teacher, subject, time, student;
    private Button buttonBatch;
    private Spinner coaching_spinner, batch_spinner, teacher_spinner, student_spinner;

    private ArrayList coachingArray = new ArrayList();
    private ArrayList batchArray = new ArrayList();
    private ArrayList teacherArray = new ArrayList();
    private ArrayList studentArray = new ArrayList();

    JSONObject jsonObj = new JSONObject();
    private String userEmail = new LoginActivity().userEmail();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle();

        batch_subject = (EditText) findViewById(R.id.choose_subject);
        batch_time = (EditText) findViewById(R.id.choose_time);
        buttonBatch = (Button) findViewById(R.id.button_submit);
        coaching_spinner = (Spinner) findViewById(R.id.choose_coaching);
        batch_spinner = (Spinner) findViewById(R.id.choose_batch);
        teacher_spinner = (Spinner) findViewById(R.id.choose_teacher);
        student_spinner = (Spinner) findViewById(R.id.choose_student);

        sendEmailForCoaching();
        coachingArray.add("Choose Coaching");
        ArrayAdapter<String> coachingAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, coachingArray);
        coachingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        coaching_spinner.setAdapter(coachingAdapter);

        batchArray.add("Choose Batch");
        coaching = String.valueOf(coaching_spinner.getSelectedItem());
        if(!coaching.equals("") || !coaching.equals("Choose Coaching")){
            Log.e("Coaching Name:", coaching);
            sendCoachingForBatch(coaching);
        }
        ArrayAdapter<String> batchAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, batchArray);
        batchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        batch_spinner.setAdapter(batchAdapter);

        teacherArray.add("Choose Teacher");
        batch = String.valueOf(batch_spinner.getSelectedItem());
        if(!batch.equals("") || !batch.equals("Choose Batch")){
            Log.e("Batch Name:", batch);
            sendBatchForTeacher(batch);
        }
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, teacherArray);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacher_spinner.setAdapter(teacherAdapter);

        studentArray.add("Choose Student");
        ArrayAdapter<String> studentAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, studentArray);
        studentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        student_spinner.setAdapter(studentAdapter);
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle("Add Student");
    }

    @Override
    public void onClick(View v) {

    }

    public void sendEmailForCoaching() {
        JSONObject json_obj = new JSONObject();

        try {
            json_obj.put("email", userEmail);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        if (json_obj.length() > 0) {
            new GetCoachingFromServer().execute(String.valueOf(json_obj));
        }
    }

    public void sendCoachingForBatch(String str) {
        JSONObject json_obj = new JSONObject();

        try {
            json_obj.put("coaching_name", str.toUpperCase());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        if (json_obj.length() > 0) {
            new GetBatchFromServer().execute(String.valueOf(json_obj));
        }
    }

    public void sendBatchForTeacher(String str) {
        JSONObject json_obj = new JSONObject();

        try {
            json_obj.put("batch_name", str.toLowerCase());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        if (json_obj.length() > 0) {
            new GetTeacherFromServer().execute(String.valueOf(json_obj));
            new GetStudentFromServer().execute(String.valueOf(json_obj));
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
                //  Log.e("RESULT", String.valueOf(cityArray));

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

    private class GetBatchFromServer extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
            String JsonDATA = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(getBatch + userEmail);
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

                String[] str=JsonResponse.split(",");

                for(int i=0;i<str.length;i++){
                    JSONObject emp=(new JSONObject(str[i]));
                    String response=emp.getString("batch_name").toUpperCase().trim();
                    String.valueOf(batchArray.add(response));
                }
                //coachingArray.remove("Choose Coaching");
                //  Log.e("RESULT", String.valueOf(cityArray));

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

    private class GetTeacherFromServer extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
            String JsonDATA = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(getTeacher);
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
                    String fname=emp.getString("fname").toUpperCase().trim();
                    String email=emp.getString("email").toLowerCase().trim();
                    String.valueOf(teacherArray.add(fname + ", "+email));
                }
                //coachingArray.remove("Choose Coaching");
                Log.e("Teacher Array", String.valueOf(teacherArray));

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

    private class GetStudentFromServer extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
            String JsonDATA = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(getStudent);
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
                    String fname=emp.getString("fname").toUpperCase().trim();
                    String email=emp.getString("email").toUpperCase().trim();
                    String.valueOf(studentArray.add(fname+", "+email));
                }
                //coachingArray.remove("Choose Coaching");
                Log.e("Student Array", String.valueOf(studentArray));

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
