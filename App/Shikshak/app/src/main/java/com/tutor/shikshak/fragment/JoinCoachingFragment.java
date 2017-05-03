package com.tutor.shikshak.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import static com.tutor.shikshak.other.Constants.getAllCoaching;
import static com.tutor.shikshak.other.Constants.getBatchforCoaching;

public class JoinCoachingFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;
    private Spinner spin_coaching, spin_batch;
    private EditText edit_email;
    private static String txtaccount = "";
    private Button join_button;
    private ArrayList coachingArray = new ArrayList();
    private ArrayList batchArray = new ArrayList();
    private String coaching, batch;


    public JoinCoachingFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static JoinCoachingFragment newInstance(String param1, String param2) {
        JoinCoachingFragment fragment = new JoinCoachingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_joincoaching, container, false);

        View rootView = inflater.inflate(R.layout.fragment_joincoaching, container, false);

        spin_batch = (Spinner) rootView.findViewById(R.id.spinner_batch_selection);
        spin_coaching = (Spinner) rootView.findViewById(R.id.spinner_coaching_selection);
        //edit_email = (EditText) rootView.findViewById(R.id.user_email);
        join_button = (Button) rootView.findViewById(R.id.button_join_coaching);

        new GetCoachingFromServer().execute();
        coachingArray.add("Choose Coaching");
        ArrayAdapter<String> coachingAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, coachingArray);
        coachingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_coaching.setAdapter(coachingAdapter);

        batchArray.add("Choose Batch");

        spin_coaching.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                coaching = String.valueOf(spin_coaching.getSelectedItem());
                // Toast.makeText(AddStudentActivity.this, String.valueOf(coaching_spinner.getSelectedItem()),Toast.LENGTH_SHORT).show();
                if((!coaching.equals("Choose Coaching"))){
                    // Toast.makeText(AddStudentActivity.this, String.valueOf(coaching_spinner.getSelectedItem()),Toast.LENGTH_SHORT).show();
                    batchArray.clear();
                    spin_batch.setSelection(0);
                    batchArray.add("Choose Batch");
                    sendCoachingForBatch(coaching);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing, just another required interface callback
            }
        }); // (optional)

        ArrayAdapter<String> batchAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, batchArray);
        batchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_batch.setAdapter(batchAdapter);

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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

    private class GetCoachingFromServer extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
//            String JsonDATA = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(getAllCoaching);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

//                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
//                //writer.write(JsonDATA);
//                writer.close();

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
                URL url = new URL(getBatchforCoaching);
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
}
