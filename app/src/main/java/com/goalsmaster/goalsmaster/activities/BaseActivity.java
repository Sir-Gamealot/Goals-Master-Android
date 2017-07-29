package com.goalsmaster.goalsmaster.activities;

import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ResultCodes;
import com.goalsmaster.goalsmaster.R;
import com.goalsmaster.goalsmaster.other.Globals;
import com.goalsmaster.goalsmaster.other.RequestCodes;
import com.goalsmaster.goalsmaster.utils.AppConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class BaseActivity extends AppCompatActivity {

    private static final String S_FIRST_RUN = "FIRST_RUN";
    private static final int REQ_CODE_AUTH = 1000;

    protected static FirebaseDatabase firebaseDB;
    protected static FirebaseAuth firebaseAuth;

    FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d(this.getClass().getSimpleName(), "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d(this.getClass().getSimpleName(), "onAuthStateChanged:signed_out");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check for log out state and redirect to login activity

        firebaseDB = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null) {
            List<AuthUI.IdpConfig> providers = new ArrayList<>();
            providers.add(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());
            providers.add(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());
            providers.add(new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build());
            providers.add(new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build());
            // Not logged in, present Create account / Login with Firebase UI
            Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                    .setIsSmartLockEnabled(AppConfig.getBoolean(getApplicationContext(), Globals.S_SMART_LOCK_STATE, true))
                    .setAvailableProviders(providers)
                    .setLogo(R.mipmap.ic_launcher)
                    .setTheme(R.style.AppTheme)
                    .build();
            AppConfig.putBoolean(getApplicationContext(), Globals.S_SMART_LOCK_STATE, true);
            startActivityForResult(intent, RequestCodes.AUTH);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RequestCodes.AUTH:
                final String msg;
                switch (resultCode) {
                    case RESULT_OK:
                       msg = "Signed in";
                        break;
                    case RESULT_CANCELED:
                        msg = "Cancelled";
                        break;
                    default:
                        msg = "Unknown error";
                        break;
                }
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
