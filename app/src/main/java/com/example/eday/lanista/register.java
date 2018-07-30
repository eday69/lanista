package com.example.eday.lanista;

import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.api.Listener;
import com.amazon.identity.auth.device.api.authorization.AuthCancellation;
import com.amazon.identity.auth.device.api.authorization.AuthorizationManager;
import com.amazon.identity.auth.device.api.authorization.AuthorizeListener;
import com.amazon.identity.auth.device.api.authorization.AuthorizeRequest;
import com.amazon.identity.auth.device.api.authorization.AuthorizeResult;
import com.amazon.identity.auth.device.api.authorization.ProfileScope;
import com.amazon.identity.auth.device.api.authorization.Scope;
import com.amazon.identity.auth.device.api.authorization.User;
import com.amazon.identity.auth.device.api.workflow.RequestContext;

import com.twitter.sdk.android.core.Twitter;

import org.w3c.dom.Text;

public class register extends AppCompatActivity {

    private static final String TAG = register.class.getName();
    private RequestContext requestContext;

    private View aLoginButton;
    private Boolean aIsLoggedIn;
    private TextView aProfileText;
    private ProgressBar aLoginProgress;
    private TextView aLogoutTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //initialize Twitter Kit

        //we can add app secret and key here when we get them using the code below
        //TwitterConfig config = new TwitterConfig.Builder(this)
        //        .logger(new DefaultLogger(Log.DEBUG))
        //        .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.CONSUMER_KEY), getString(R.string.CONSUMER_SECRET)))
        //        .debug(true)
        //        .build();
        //Twitter.initialize(config);
        //Twitter.initialize(this);
        //if not we add it to resources <resources> <string></string></resources>

        super.onCreate(savedInstanceState);
        requestContext = RequestContext.create(this);

        requestContext.registerListener(new AuthorizeListener() {

            // Authorization was successful
            @Override
            public void onSuccess(AuthorizeResult authorizeResult) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //authorization is successful so we don't let the user login again
                        setLoggingInState(true);
                    }
                });
                fetchUserProfile();
            }

            // There is a problem with the authorization
            @Override
            public void onError(AuthError authError) {

                Log.e(TAG, "AuthError during authorization", authError);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAuthToast("There was a problem with the authorization. Please try again");
                        resetProfileView();
                        setLoggingInState(false);
                    }
                });
            }

            // Authorization was cancelled before the process finished
            @Override
            public void onCancel(AuthCancellation authCancellation) {

                Log.e(TAG, "User cancelled the login");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAuthToast("Authorization was cancelled");
                        resetProfileView();
                    }
                });

            }


        });

        
        setContentView(R.layout.activity_register);
        initializeUI();
    }

    @Override
    protected void onResume() {

        super.onResume();
        requestContext.onResume();
    }

    @Override
    protected void onStart() {

        super.onStart();
        Scope[] scopes = {ProfileScope.profile(), ProfileScope.postalCode()};
        AuthorizationManager.getToken(this, scopes, new Listener<AuthorizeResult, AuthError>() {
            @Override
            public void onSuccess(AuthorizeResult authorizeResult) {
                if (authorizeResult.getAccessToken() != null) {
                    //the user is logged in
                    fetchUserProfile();
                }
                else {
                    //the user is not logged in
                }
            }

            @Override
            public void onError(AuthError authError) {
                //the user is not signed in
            }
        });
    }


    private void fetchUserProfile() {
        User.fetch(this, new Listener<User, AuthError>() {

            //fetch completed successfully
            @Override
            public void onSuccess(User user) {

                final String name = user.getUserName();
                final String email = user.getUserEmail();
                final String account = user.getUserId();
                final String zipcode = user.getUserPostalCode();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateProfileData(name, email, account, zipcode);
                    }
                });
            }

            @Override
            public void onError(AuthError authError) {

                Log.e(TAG,"Authorization error");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        setLoggedOutState();

                        String errorMessage = "Error retrieving profile information , please try again";
                        Toast errorToast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
                        errorToast.setGravity(Gravity.CENTER,0,0);
                        errorToast.show();
                    }
                });
            }
        });
    }

    private void updateProfileData(String name, String email, String account, String zipcode){

        StringBuilder profileBuilder = new StringBuilder();

        profileBuilder.append(String.format("Welcome %s", name));
        profileBuilder.append(String.format("Your email is %s", email));
        profileBuilder.append(String.format("Your zipcode is %s", zipcode));

        final String profile = profileBuilder.toString();
        Log.e(TAG, "Profile is " + profile);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateProfileView(profile);
                setLoggedInState();
            }
        });
    }

    //Initializes all the UI elements
    private void initializeUI() {

        aLoginButton = findViewById(R.id.login_with_amazon);
        aLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthorizationManager.authorize(new AuthorizeRequest
                                .Builder(requestContext)
                                .addScopes(ProfileScope.profile(), ProfileScope.postalCode())
                                .build());
            }
        });

        aProfileText = (TextView) findViewById(R.id.profile_info);
        aLoginProgress = (ProgressBar) findViewById(R.id.log_in_progress);

    }

    private void updateProfileView(String profileInfo) {

        Log.d(TAG, "Updating profile view with user info");
        aProfileText.setText(profileInfo);


    }


    //set text back to the prompt originally displayed
    private void resetProfileView() {
        setLoggingInState(false);
        //aProfileText.setText();

    }

    private void setLoggedInState() {

        aLoginButton.setVisibility(Button.GONE);
        setLoggedInButtonsVisibility(Button.VISIBLE);
        aIsLoggedIn = true;
        setLoggingInState(false);

    }

    private void setLoggedOutState() {

        aLoginButton.setVisibility(Button.VISIBLE);
        setLoggedInButtonsVisibility(Button.GONE);
        aIsLoggedIn = false;

        resetProfileView();

    }


    private void setLoggedInButtonsVisibility(int visibility) {
        aLogoutTextView.setVisibility(visibility);
    }


    private void setLoggingInState(final boolean logginIn) {

        if (logginIn) {

            aLoginButton.setVisibility(Button.GONE);
            setLoggedInButtonsVisibility(Button.GONE);
            aLoginProgress.setVisibility(ProgressBar.VISIBLE);
            aProfileText.setVisibility(TextView.GONE);
        }
        else {

            if (aIsLoggedIn) {
                setLoggedInButtonsVisibility(Button.VISIBLE);
            }
            else {
                aLoginButton.setVisibility(Button.VISIBLE);
            }
            aLoginProgress.setVisibility((ProgressBar.GONE));
            aProfileText.setVisibility(TextView.VISIBLE);
        }
    }

    private void showAuthToast(String authToastMessage) {

        Toast authToast = Toast.makeText(getApplicationContext(),authToastMessage, Toast.LENGTH_LONG);
        authToast.setGravity(Gravity.CENTER, 0, 0 );
        authToast.show();

    }
}
