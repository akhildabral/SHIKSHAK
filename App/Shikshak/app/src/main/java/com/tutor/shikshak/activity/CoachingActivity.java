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

import static com.tutor.shikshak.other.Constants.addCoachingUrl;
import static com.tutor.shikshak.other.Constants.getCity;
import static com.tutor.shikshak.other.Constants.getColony;

public class CoachingActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private EditText coachingName, address;
    private String coaching_name, colony_name, city_name, full_address;
    private Button button_coaching;
    private Spinner colony, city;
    private ArrayList cityArray = new ArrayList();
    private ArrayList colonyArray = new ArrayList();

    JSONObject jsonObj = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coaching);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle();

        coachingName = (EditText) findViewById(R.id.coachingName);
        colony = (Spinner) findViewById(R.id.spinner_colony);
        city = (Spinner) findViewById(R.id.spinner_city);
        address = (EditText) findViewById(R.id.fullAddress);
        button_coaching = (Button) findViewById(R.id.button_coaching);
        button_coaching.setOnClickListener(this);

        new GetCityFromServer().execute();
        new GetColonyFromServer().execute();

//        String[] cityArr = new String[cityArray.size()];
//        cityArr = (String[]) cityArray.toArray(cityArr);

//        ArrayAdapter<String> adapter = new ArrayAdapter(this,cityArr);

        cityArray.add("Choose City");
        colonyArray.add("Choose Colony");
       // Log.e("array---", String.valueOf(cityArray));
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cityArray);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(cityAdapter);

        ArrayAdapter<String> colonyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colonyArray);
        colonyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colony.setAdapter(colonyAdapter);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//        spinner.setAdapter(
//                new NothingSelectedSpinnerAdapter(
//                        adapter,
//                        R.layout.contact_spinner_row_nothing_selected,
//                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
//                        this));
// Apply the adapter to the spinner
      //  spinner.setAdapter(adapter);
       // spinner.setOnItemClickListener(this);




    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle("Create a Coaching");
    }

    @Override
    public void onClick(View v) {
        coaching_name = coachingName.getText().toString();
        colony_name = String.valueOf(colony.getSelectedItem());
        city_name = String.valueOf(city.getSelectedItem());
        full_address = address.getText().toString();

        try {
            jsonObj.put("name", coaching_name);
            jsonObj.put("colony", colony_name);
            jsonObj.put("city", city_name);
            jsonObj.put("address", full_address);
            jsonObj.put("owner", new LoginActivity().userEmail());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObj.length() > 0) {
            new SendDataToServer().execute(String.valueOf(jsonObj));
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
                URL url = new URL(addCoachingUrl);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                writer.close();
                Log.e("error---", JsonDATA);

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
                            Toast.makeText(CoachingActivity.this, "Error in Coaching Creation", Toast.LENGTH_LONG).show();
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

    private class GetCityFromServer extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
            //String JsonDATA = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(getCity);

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
                    String response=emp.getString("name").toUpperCase().trim();
                    String.valueOf(cityArray.add(response));
                }
                //cityArray.remove("Choose City");
                Log.e("city array", String.valueOf(cityArray));

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

    private class GetColonyFromServer extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
            //String JsonDATA = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(getColony);

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

                String[] colony=JsonResponse.split(",");

                for(int i=0;i<colony.length;i++){
                    JSONObject emp=(new JSONObject(colony[i]));
                    String response=emp.getString("name").toUpperCase().trim();
                    String.valueOf(colonyArray.add(response));
                }
                //colonyArray.remove("Choose Colony");
                Log.e("colony array", String.valueOf(cityArray));

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
