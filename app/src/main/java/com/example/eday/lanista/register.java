package com.example.eday.lanista;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;


public class register extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = register.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;
    private SignInButton google_login_button;
    public LoginButton fb_login_button;
    public CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
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

            saveLoginToken("facebook_id", accessToken.getToken());
            gotoLandingPage(true, null);
        }

        fb_login_button = findViewById(R.id.fb_login_button);
        fb_login_button = (LoginButton) findViewById(R.id.fb_login_button);

        fb_login_button.registerCallback(callbackManager, new FacebookCallback < LoginResult > () {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();
                Log.i(TAG, accessToken);
                saveLoginToken("facebook_id", accessToken);
                gotoLandingPage(true, null);
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
            saveLoginToken("google_id", account.getIdToken());
            gotoLandingPage(true, account);
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
            saveLoginToken("google_id", account.getIdToken());
            gotoLandingPage(true, account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
//            updateUI(false, null);
        }
    }

    // Erase????
    /*
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        gotoLandingPage(false, null);
                    }
                });
  `  }
*/

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }



    private void gotoLandingPage(boolean isSignedIn, GoogleSignInAccount googleAcct) {
        if (isSignedIn) {
//            btnSignIn.setVisibility(View.VISIBLE); // was GONE
            Log.d(TAG, "We will save googleTokenID->" + googleAcct.getIdToken());

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

    private void saveLoginToken(String token_Type, String tokenID){

        JSONObject json = new JSONObject();
        try {
            json.put(token_Type, tokenID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue= Volley.newRequestQueue(this);

        Log.e("Sending Json", tokenID.toString());
        String myURL="http://18.218.188.251:3000/api/token";
        JsonObjectRequest objectRequest=new JsonObjectRequest(
                /**
                 * Creates a new request.
                 * @param method the HTTP method to use
                 * @param url URL to fetch the JSON from
                 * @param jsonRequest A {@link JSONObject} to post with the request. Null is allowed and
                 *   indicates no parameters will be posted along with request.
                 * @param listener Listener to receive the JSON response
                 * @param errorListener Error listener, or null to ignore errors.
                 **/
                Request.Method.POST,
                myURL,
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Rest Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rest Response", error.toString());
                    }
                }
        );

        requestQueue.add(objectRequest);
    }


}