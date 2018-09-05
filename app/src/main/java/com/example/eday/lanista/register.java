package com.example.eday.lanista;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Future;


public class register extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{
    public static String profile_id = "com.example.eday.lanista.profile_id";
    private static final String TAG = register.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;
    private SignInButton google_login_button;
    public LoginButton fb_login_button;
    public CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    RelativeLayout emaillogin;

    userProfile user_id = new userProfile();


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

        fb_login_button = findViewById(R.id.fb_login_button);
        fb_login_button = (LoginButton) findViewById(R.id.fb_login_button);

        // When user pressed log in by email, this is the callback and the
        // login process.
        fb_login_button.registerCallback(callbackManager, new FacebookCallback < LoginResult > () {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();
                Log.i(TAG, accessToken);
                // generate Lanista id
                user_id.social_media = "facebook_id";
                user_id.social_media_token = accessToken;

                // Generate lanista id from db & got to landing page
                user_id.doLogin( register.this, user_id.getSocialMediaCredentials());

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
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        google_login_button.setSize(SignInButton.SIZE_STANDARD);
        google_login_button.setScopes(gso.getScopeArray());
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
//            String personId = account.getId();
            String personName = account.getDisplayName();
//            String personPhotoUrl = account.getPhotoUrl().toString();
            String email = account.getEmail();
            String familyName = account.getFamilyName();

            Log.e(TAG, " Name: " + personName +", email: " + email + ", Family Name: " + familyName);

            user_id.social_media = "google_id";
            user_id.social_media_token = account.getIdToken();
            user_id.first_name = personName;
            user_id.last_name = familyName;
            user_id.email = email;

            // Generate lanista id from db & got to landing page
            user_id.doLogin( register.this, user_id.getSocialMediaCredentials());

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
//            updateUI(false, null);
        }
    }




    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    /** Called when the user want to register by email */
    public void showRegisterbyEmail(View view) {
        // Do something in response to button
        Log.d(TAG, "emaillogin view id->" + emaillogin);
//        emaillogin.layout(0, 100, 0, 300);
        emaillogin.setVisibility(View.VISIBLE);
        TextView btn_email = findViewById(R.id.textView15);
        btn_email.setVisibility(View.INVISIBLE);
    }

    public void goToSignInPage(String email) {
        Intent intent = new Intent(this, credentials.class);
        intent.putExtra("USER_EMAIL", email );
        startActivity(intent);
    }

    // here we will try to save email/password in db
    public void doRegisterbyEmail(final View view) {
        TextView email= findViewById((R.id.email));
        final String tmp_email=String.valueOf(email.getText());
        TextView password=findViewById((R.id.password));
//        findViewById(R.id.emailLogin);
        JSONObject json = new JSONObject();
        try {
            json.put("email", tmp_email );
            json.put("password", password.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue= Volley.newRequestQueue(this);

        Log.e("Sending Json", json.toString());
//        String myURL="http://18.218.188.251:3000/api/profile";
        String myURL="http://10.0.2.2:3000/api/profile";
        JsonObjectRequest objectRequest=new JsonObjectRequest(
                Request.Method.POST,
                myURL,
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String id =  response.getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String msg = null;
                        try {
                            msg = response.getString("msg");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (msg.length() > 0) {
                            Toast.makeText(view.getContext(), "email already registered, please login", Toast.LENGTH_LONG)
                                    .show();
                            goToSignInPage(tmp_email);
                        }
                        else {
                            Toast.makeText(view.getContext(), "sent email to confirm registration", Toast.LENGTH_LONG)
                                    .show();

                        }
                        // Or waiting for confirmation email to be 'confirmed' !!

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

        // Do something in response to button
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