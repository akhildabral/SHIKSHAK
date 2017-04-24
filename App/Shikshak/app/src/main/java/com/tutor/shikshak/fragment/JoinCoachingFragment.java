package com.tutor.shikshak.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.tutor.shikshak.R;

import java.util.ArrayList;

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

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        spin_batch = (Spinner) rootView.findViewById(R.id.spinner_batch_selection);
        spin_coaching = (Spinner) rootView.findViewById(R.id.spinner_coaching_selection);
        edit_email = (EditText) rootView.findViewById(R.id.user_email);
        join_button = (Button) rootView.findViewById(R.id.button_join_coaching);

//        coachingArray.add("Choose Coaching");
//        ArrayAdapter<String> coachingAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, coachingArray);
//        coachingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spin_coaching.setAdapter(coachingAdapter);
//
//        batchArray.add("Choose Batch");
//        ArrayAdapter<String> batchAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, batchArray);
//        batchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spin_batch.setAdapter(batchAdapter);

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

    public void onRadioButtonClicked(View view) {
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
    }
}
