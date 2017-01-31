package com.tutor.shikshak;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class StudentHomeActivity extends AppCompatActivity {

    private TextView mtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        mtext = (TextView) findViewById(R.id.mytxt);

        LoginActivity la = new LoginActivity();
        String str = la.displayName1();
        mtext.setText(str);
    }
}
