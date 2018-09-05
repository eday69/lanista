package com.example.eday.lanista;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LoginPage extends AppCompatActivity {

    userProfile user_id = new userProfile();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
    }

    @Override
    public void onStart() {
        super.onStart();

        // FACEBOOK
        // Do we have a pre-existing facebook token?
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {

            user_id.social_media = "facebook_id";
            user_id.social_media_token = accessToken.getToken();

            // Generate lanista id from db & got to landing page
            user_id.doLogin( LoginPage.this, user_id.getSocialMediaCredentials());

        }

        // GOOGLE
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            user_id.social_media = "google_id";
            user_id.social_media_token = account.getIdToken();

            // Generate lanista id from db & got to landing page
            user_id.doLogin( LoginPage.this, user_id.getSocialMediaCredentials());
        }

        // TWITTER
        // ????

        // AMAZON
        // ????
    }

    /** Called when the user taps the Send button */
    public void doLogin(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, credentials.class);
        startActivity(intent);
    }

    /** Called when the user taps the Send button */
    public void doRegister(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, register.class);
        startActivity(intent);
    }


}
