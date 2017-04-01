package com.tutor.shikshak.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.tutor.shikshak.R;

public class BatchActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // if (savedInstanceState == null) {
        setToolbarTitle();
        // }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle("Create a Batch");
    }
}

