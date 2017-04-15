package com.goalsmaster.goalsmaster.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.goalsmaster.goalsmaster.R;
import com.goalsmaster.goalsmaster.utils.AppConfig;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity {

    private static final String S_FIRST_RUN = "FIRST_RUN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(AppConfig.getBoolean(this, S_FIRST_RUN, true) == false) {
           startMainActivity();
        }
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        AppConfig.putBoolean(this, S_FIRST_RUN, false);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick({R.id.text, R.id.fingerprint})
    public void onClick() {
        startMainActivity();
    }
}
