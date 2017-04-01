package com.tutor.shikshak.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import static com.tutor.shikshak.other.Constants.addCoachingUrl;

public class CoachingActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private EditText coachingName, colonyName, city, address;
    private String coaching_name, colony_name, city_name, full_address;
    private Button button_coaching;

    JSONObject jsonObj = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coaching);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle();

        coachingName = (EditText) findViewById(R.id.coachingName);
        colonyName = (EditText) findViewById(R.id.colony);
        city = (EditText) findViewById(R.id.city);
        address = (EditText) findViewById(R.id.fullAddress);
        button_coaching = (Button) findViewById(R.id.button_coaching);
        button_coaching.setOnClickListener(this);
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle("Create a Coaching");
    }

    @Override
    public void onClick(View v) {
        coaching_name = coachingName.getText().toString();
        colony_name = colonyName.getText().toString();
        city_name = city.getText().toString();
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

                Log.e("response---", str);

                if(str.equals("true") || str.equals(1) || str.equals("1")){

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                Intent i = new Intent(CoachingActivity.this, HomeActivity.class);
                                startActivity(i);
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
}
