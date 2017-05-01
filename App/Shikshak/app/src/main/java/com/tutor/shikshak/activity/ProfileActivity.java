package com.tutor.shikshak.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

import static com.tutor.shikshak.other.Constants.addUserUrl;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtfname, txtlname, txtemail, txtpassword, txtcofmpassword, txtphone;
    private ImageView txtpicture;
    private Button btn_submit;
    private String iFname, iLname, iEmail, iPassword, iCfrnPassword, iPhone, iPicture, iAccount;

    LoginActivity loginObj = new LoginActivity();
    JSONObject jsonObj = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtfname = (EditText) findViewById(R.id.fname);
        txtlname = (EditText) findViewById(R.id.lname);
        txtemail = (EditText) findViewById(R.id.email);
        txtpassword = (EditText) findViewById(R.id.password);
        txtcofmpassword = (EditText) findViewById(R.id.confirm_password);
        txtphone = (EditText) findViewById(R.id.phone);
        txtpicture = (ImageView) findViewById(R.id.imgProfilePic);
        btn_submit = (Button) findViewById(R.id.button_profile);
        btn_submit.setOnClickListener(this);

        txtemail.setText(loginObj.userEmail());
        Glide.with(getApplicationContext()).load(loginObj.userPhotoUrl())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(txtpicture);
    }

/*    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radio_teacher:
                if (checked)
                    txtaccount = "teacher";
                    break;
            case R.id.radio_student:
                if (checked)
                    txtaccount = "student";
                    break;
        }
    }*/

    @Override
    public void onClick(View v) {
        iFname = txtfname.getText().toString();
        iLname = txtlname.getText().toString();
        iEmail = txtemail.getText().toString();
        iPassword = txtpassword.getText().toString();
        iCfrnPassword = txtcofmpassword.getText().toString();
        iPhone = txtphone.getText().toString();
        iPicture = loginObj.userPhotoUrl();

        if((iPassword.equals(iCfrnPassword))){
                try {
                    jsonObj.put("fname", iFname);
                    jsonObj.put("lname", iLname);
                    jsonObj.put("password", iPassword);
                    jsonObj.put("phone", iPhone);
                    //jsonObj.put("account", iAccount);
                    jsonObj.put("picture", "unknown");
                    jsonObj.put("email", iEmail);
                }
                catch (JSONException e) {
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
                    Toast.makeText(ProfileActivity.this, "Password is not same", Toast.LENGTH_SHORT).show();
                }
            });
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
                URL url = new URL(addUserUrl);

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

                Log.e("response---", str);

                if(str.equals("true")){
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                Intent i = new Intent(ProfileActivity.this, HomeActivity.class);
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
                            Toast.makeText(ProfileActivity.this, "Error in Profile Creation", Toast.LENGTH_LONG).show();
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
