package com.tutor.shikshak.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import static com.tutor.shikshak.other.Constants.getBatch;
import static com.tutor.shikshak.other.Constants.getCoaching;
import static com.tutor.shikshak.other.Constants.getStudent;
import static com.tutor.shikshak.other.Constants.getTeacher;

public class AddStudentActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private String coaching, batch, teacher, student;
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
//        coaching = String.valueOf(coaching_spinner.getSelectedItem());

        coaching_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                coaching = String.valueOf(coaching_spinner.getSelectedItem());
               // Toast.makeText(AddStudentActivity.this, String.valueOf(coaching_spinner.getSelectedItem()),Toast.LENGTH_SHORT).show();
                if((!coaching.equals("Choose Coaching"))){
                    Toast.makeText(AddStudentActivity.this, String.valueOf(coaching_spinner.getSelectedItem()),Toast.LENGTH_SHORT).show();
                    batchArray.clear();
                    batch_spinner.setSelection(0);
                    batchArray.add("Choose Batch");
                    sendCoachingForBatch(coaching);

                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing, just another required interface callback
            }
        }); // (optional)

        ArrayAdapter<String> batchAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, batchArray);
        batchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        batch_spinner.setAdapter(batchAdapter);
        teacherArray.add("Choose Teacher");
        studentArray.add("Choose Student");

        batch_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                batch = String.valueOf(batch_spinner.getSelectedItem());
                // Toast.makeText(AddStudentActivity.this, String.valueOf(coaching_spinner.getSelectedItem()),Toast.LENGTH_SHORT).show();
                if((!batch.equals("Choose Batch"))){
                    teacherArray.clear();
                    teacher_spinner.setSelection(0);
                    teacherArray.add("Choose Teacher");

                    studentArray.clear();
                    student_spinner.setSelection(0);
                    studentArray.add("Choose Student");

                    sendBatchForTeacher(batch, coaching);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing, just another required interface callback
            }
        }); // (optional)

        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, teacherArray);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacher_spinner.setAdapter(teacherAdapter);


        ArrayAdapter<String> studentAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, studentArray);
        studentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        student_spinner.setAdapter(studentAdapter);
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle("Add Student");
    }

    @Override
    public void onClick(View v) {
        //subject = batch_subject.getText().toString();
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
            json_obj.put("email", userEmail);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        if (json_obj.length() > 0) {
            new GetBatchFromServer().execute(String.valueOf(json_obj));
        }
    }

    public void sendBatchForTeacher(String batch, String coaching) {
        JSONObject json_obj = new JSONObject();

        try {
            json_obj.put("batch_name", batch);
            json_obj.put("coaching_name", coaching);
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

    private class GetBatchFromServer extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
            String JsonDATA = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(getBatch);
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
                Log.e("batch array", String.valueOf(batchArray));

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
                Log.e("Teacher Array 1", JsonResponse);
                JsonResponse = JsonResponse.replace("[","").replace("]","");
                Log.e("Teacher Array 2", JsonResponse);

                JSONObject emp=(new JSONObject(JsonResponse));
                String response1=emp.getString("fname").toLowerCase().trim();
                String response2=emp.getString("email").toLowerCase().trim();
                Log.e("Teacher Array 3", response1);
                Log.e("Teacher Array 4", response2);
                teacherArray.add(response1 + ", "+response2);

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
                Log.e("Student Array 1", JsonResponse);
                JsonResponse = JsonResponse.replace("[","").replace("]","");
                Log.e("Student Array 2", JsonResponse);

                JSONObject emp=(new JSONObject(JsonResponse));
                String response1=emp.getString("fname").toLowerCase().trim();
                String response2=emp.getString("email").toLowerCase().trim();
                Log.e("Student Array 3", response1);
                Log.e("Student Array 4", response2);
                studentArray.add(response1 + ", "+response2);

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
