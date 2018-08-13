package com.example.eday.lanista;

import android.app.ActionBar;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;

//import static android.provider.ContactsContract.Intents.Insert.EMAIL;

public class register extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{
    public static final String GoogleId = "com.example.myfirstapp.GoogleId";
    private static final String TAG = register.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;
    private SignInButton google_login_button;
    public LoginButton fb_login_button;
    public CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleApiClient;
    RelativeLayout emaillogin;

    public String id, name, email, gender, birthday;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emaillogin=(RelativeLayout) findViewById(R.id.emailLogin);
        emaillogin.setVisibility(View.INVISIBLE);
        callbackManager = CallbackManager.Factory.create();

        google_login_button = findViewById(R.id.google_login_button);
        google_login_button.setColorScheme(0);
        TextView textView = (TextView) google_login_button.getChildAt(0);
        textView.setText("your_text_xyz");

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            updateUI(true, null);
        }


        fb_login_button = findViewById(R.id.fb_login_button);
        fb_login_button = (LoginButton) findViewById(R.id.fb_login_button);

        fb_login_button.registerCallback(callbackManager, new FacebookCallback < LoginResult > () {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();
                Log.i(TAG, accessToken);
                updateUI(true, null);
            }
            @Override
            public void onCancel() {
                System.out.println("onCancel");
            }
            @Override
            public void onError(FacebookException exception) {
                System.out.println("onError");
                Log.v("LoginActivity", exception.getCause().toString());
            }
        });
        initializeControls();
        initializeGPlusSettings();
    }

    private void initializeControls(){
//        google_login_button = (SignInButton) findViewById(R.id.btn_sign_in);
        google_login_button.setOnClickListener(this);
//        fb_login_button.setOnClickListener(this);

    }

    private void initializeGPlusSettings(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        google_login_button.setSize(SignInButton.SIZE_STANDARD);
        google_login_button.setScopes(gso.getScopeArray());
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            Log.d(TAG, "onStart - loggin in automatically ");
            updateUI(true, account);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_login_button:
                Log.d(TAG, "Clicked google sign in");
                signIn();
                break;
            case R.id.fb_login_button:
                Log.d(TAG, "Clicked facebook sign in");
                fb_login_button.performClick();
                break;
            // ...
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        // For google sign in
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }else{
            // for facebook sign in
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //Fetch values
            String personId = account.getId();
            String personName = account.getDisplayName();
            String personPhotoUrl = account.getPhotoUrl().toString();
            String email = account.getEmail();
            String familyName = account.getFamilyName();
            Log.e(TAG, "Id: "+personId+" Name: " + personName +", email: " + email + ", Image: " + personPhotoUrl +", Family Name: " + familyName);

            // Signed in successfully, show authenticated UI.
            updateUI(true, account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(false, null);
        }
    }

    // Erase????
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false, null);
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }



    private void updateUI(boolean isSignedIn, GoogleSignInAccount googleAcct) {
        if (isSignedIn) {
//            btnSignIn.setVisibility(View.VISIBLE); // was GONE

            // We have Google credentials, Go to Landing Page !
            Intent intent = new Intent(this, landingPage.class);
            intent.putExtra("googleAcct", googleAcct);
            Log.d(TAG, "googleAcct->" + googleAcct);
            startActivity(intent);

        } else {
            google_login_button.setVisibility(View.VISIBLE);
        }
    }

    /** Called when the user want to register by email */
    public void doRegisterbyEmail(View view) {
        // Do something in response to button
        Log.d(TAG, "emaillogin view id->" + emaillogin);
//        emaillogin.layout(0, 100, 0, 300);
        emaillogin.setVisibility(View.VISIBLE);
        TextView btn_email = findViewById(R.id.textView15);
        btn_email.setVisibility(View.INVISIBLE);
    }

    public void closeemail(View v){

        TextView btn_email = findViewById(R.id.textView15);
        btn_email.setVisibility(View.VISIBLE);
        emaillogin.setVisibility(View.INVISIBLE);
    }


}