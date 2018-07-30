package com.example.eday.lanista;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import android.support.annotation.Nullable;


public class landingPage extends AppCompatActivity  {

    // For debuggin purposes
    private static final String TAG = "DayDebug";
    // End of debugging
    // *************************
    GoogleApiClient mGoogleApiClient;
    boolean mSignInClicked;

    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView idTextView;

    private ProfileTracker profileTracker;
    private TextView mStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);


        // For Google Login process
        // Views
        mStatusTextView = findViewById(R.id.GoogleStatus);

        // For Facebook Login process
        photoImageView = (ImageView) findViewById(R.id.photoImageView);

        // Shared login
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        idTextView = (TextView) findViewById(R.id.idTextView);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        Bundle googleBundle = intent.getBundleExtra("GoogleAcct");

//        googleBundle.putString("id", googleAcct.getId());
//        googleBundle.putString("name", googleAcct.getDisplayName());
//        googleBundle.putString("photoUrl", googleAcct.getPhotoUrl().toString());
//        googleBundle.putString("email", googleAcct.getEmail());
//        googleBundle.putString("famName", googleAcct.getFamilyName());


        updateUI(googleBundle);

        /*
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
*/

        //copy this code on "Logout" Onclick
/*
        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mGoogleApiClient.isConnected()) {
//                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                    // updateUI(false);
                    System.err.println("LOG OUT ^^^^^^^^^^^^^^^^^^^^ SUCESS");
                }

            }
        });
*/
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }
/*
    GLogout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // ...
                            Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(i);
                        }
                    });
        }
    });
*/
    public void signOut(View view) {
        Log.v(TAG, "Signing OUT ! (Google) ");
//        mGoogleSignInClient.signOut();
        Log.v(TAG, "Elvis has left the building ");
    }

    private void updateUI(@Nullable Bundle googleBundle) {
        Log.v(TAG, "in updateUI");
        String GoogleId = googleBundle.getString("id");
        String GoogleName = googleBundle.getString("name");
        idTextView.setText(GoogleId);
        if (googleBundle != null) {
            mStatusTextView.setText(getString(R.string.signed_in_fmt, GoogleName));

            findViewById(R.id.GLogout).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            findViewById(R.id.GLogout).setVisibility(View.GONE);

        }
    }


    private void goRegisterScreen() {
        Intent intent = new Intent(this, register.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logout(View view) {
        LoginManager.getInstance().logOut();
        goRegisterScreen();
    }


}
