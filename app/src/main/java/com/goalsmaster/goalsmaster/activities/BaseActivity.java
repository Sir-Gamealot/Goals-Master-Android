package com.goalsmaster.goalsmaster.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.goalsmaster.goalsmaster.utils.AppConfig;

public class BaseActivity extends AppCompatActivity {

    private static final String S_FIRST_RUN = "FIRST_RUN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check for log out state and redirect to login activity
    }
}
