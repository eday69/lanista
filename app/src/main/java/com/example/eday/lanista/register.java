package com.example.eday.lanista;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazon.identity.auth.device.api.workflow.RequestContext;

// import com.bumptech.glide.Glide;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Response;

public class register extends AppCompatActivity {

    private static final String TAG = register.class.getName();

    TwitterLoginButton twitterLoginButton;
    TextView textViewEmail;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //initialize Twitter Kit
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("412JANJnjOfZjNenv3PUDYq6k", "gHtFAje5FTaEv2vgpDzetUvzJBUPVvgCQCD7v1hqNG6ubhlEnx"))
                .debug(true)
                .build();
        Twitter.initialize(this);

        setContentView(R.layout.activity_register);

        twitterLoginButton = findViewById(R.id.twitter_login_button);
        textViewEmail = findViewById(R.id.textViewEmail);
        imageView = findViewById(R.id.imageView);






        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "twitter login successful");
                Log.i("Session Username: ", String.valueOf(result.data.getUserId()));

                login(result);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d(TAG, "twitter login unsuccessful");
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode,resultCode, data);
    }


    //login function accepting the result object
    public void login(final Result<TwitterSession> result) {

        //creating a twitter session with the result data
        TwitterSession twitterSession = result.data;

        //fetch the user profile's image url
        TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(true, false, true).enqueue(new retrofit2.Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()) {
                    User user   = response.body();

                    //getting the profile image url
                    String profileImage = user.profileImageUrl.replace("_normal", "");

                    textViewEmail.setText("Welcome " + user.name);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                Log.d(TAG, "there was a failure");
            }
        });
    }









}
