package com.tutor.shikshak.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
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

import static com.tutor.shikshak.other.Constants.logInUrl;
import static com.tutor.shikshak.other.Constants.signInUrl;
import static com.tutor.shikshak.other.Constants.userImage;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private SignInButton btnSignIn;
    private Button btnLogIn;
    private static String personName;
    private static String personPhotoUrl;
    private static String email;
    private EditText userName;
    private EditText userPasscode;
    private String inputEmail, inputPasscode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btnLogIn = (Button) findViewById(R.id.btn_log_in);

        userName = (EditText) findViewById(R.id.text_field_username);
        userPasscode = (EditText) findViewById(R.id.textfield_password);

        btnSignIn.setOnClickListener(this);
        btnLogIn.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customizing G+ button
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setScopes(gso.getScopeArray());
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
//failed condition

        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e(TAG, "display name: " + acct.getDisplayName());
            //Log.e(TAG, "display email: " + acct.getPhotoUrl().toString());

            personName = acct.getDisplayName();
            email = acct.getEmail();
            personPhotoUrl = acct.getPhotoUrl().toString();

            Log.e(TAG, "user picture: " + "" +acct.getPhotoUrl().toString());

            if(personPhotoUrl.equals(null))
                personPhotoUrl = userImage;

            senddatatoserver();
        } else {
            // Signed out, show unauthenticated UI.
            Log.e(TAG, "User is not logged in.");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_sign_in:
                signIn();
                break;

            case R.id.btn_log_in:
                inputEmail = userName.getText().toString();
                inputPasscode = userPasscode.getText().toString();

                if (inputEmail.matches("")) {
                    Toast.makeText(this, "You did not enter the Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (inputPasscode.matches("")) {
                    Toast.makeText(this, "You did not enter the Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendemailtoserver();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

/*    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            //new user, send to profile activity
            Log.e("TAG", "New User");
            senddatatoserver();

        } else {
            //returned user, send to home activity
            Log.e("TAG", "Old User");
            btnSignIn.setVisibility(View.VISIBLE);
        }
    }*/

    public String userFirstName(){
        this.personName = personName;
        String[] fname = personName.split(" ");
        String personName = fname[0];
        return personName;
    }

    public String userLastName(){
        this.personName = personName;
        String[] lname = personName.split(" ");
        String personName = lname[1];
        return personName;
    }

    public String userEmail(){
        this.email = email;
        return email;
    }

    public String userPhotoUrl(){
        this.personPhotoUrl = personPhotoUrl;
        return personPhotoUrl;
    }

    public void senddatatoserver() {
        JSONObject json_obj = new JSONObject();

        try {
            json_obj.put("email", email);
            Log.e("send email:", email);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        if (json_obj.length() > 0) {
            new SendDataToServer().execute(String.valueOf(json_obj));
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
                URL url = new URL(signInUrl +email);

                Log.e("TAG", url.toString());
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
                Log.i("TAG", JsonResponse);
                Log.e("RESPONSE", JsonResponse);

                String str = JsonResponse.toString().trim().toLowerCase();

//                JSONObject emp=(new JSONObject(JsonResponse));
//                String response=emp.getString("response");
//                Log.e("RESULT", response);

                if(str.equals("false")){

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
                            startActivity(i);
                            finish();
                        }
                    });
                }

                else {
//                    JsonResponse = JsonResponse.replace("[","").replace("]","");
//                    JSONObject emp=(new JSONObject(JsonResponse));
//                    String response=emp.getString("account").toLowerCase().trim();
//                    Log.e("RESULT", response);
                    Handler handler = new Handler(Looper.getMainLooper());
//
//                    if(response.equals("student")){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(i);
                                finish();
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

    public void sendemailtoserver() {
        JSONObject json_obj = new JSONObject();

        try {
            json_obj.put("email", inputEmail);
            json_obj.put("password", inputPasscode);
            Log.e("send email:", inputEmail);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        if (json_obj.length() > 0) {
            new SendMailToServer().execute(String.valueOf(json_obj));
        }
    }

    private class SendMailToServer extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
            String JsonDATA = params[0];
            Log.e("send data:", JsonDATA);

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(logInUrl);
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
                String str = JsonResponse.toString().trim().toLowerCase();

                if(str.equals("false")){
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Incorrect Credentials.", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                else {
//                    JsonResponse = JsonResponse.replace("[","").replace("]","");
//                    JSONObject emp=(new JSONObject(JsonResponse));
//                    String response=emp.getString("account").toLowerCase().trim();
//                    Log.e("RESULT", response);
                    Handler handler = new Handler(Looper.getMainLooper());
//
//                    if(response.equals("student")){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(i);
                                finish();
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