package com.example.eday.lanista;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.api.authorization.AuthCancellation;
import com.amazon.identity.auth.device.api.authorization.AuthorizeListener;
import com.amazon.identity.auth.device.api.authorization.AuthorizeResult;
import com.amazon.identity.auth.device.api.workflow.RequestContext;
import com.twitter.sdk.android.core.Twitter;

public class register extends AppCompatActivity {

    private RequestContext requestContext;

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

            }

            // There is a problem with the authorization
            @Override
            public void onError(AuthError authError) {

            }

            // Authorization was cancelled before the process finished
            @Override
            public void onCancel(AuthCancellation authCancellation) {

            }


        });

        
        setContentView(R.layout.activity_register);
    }
}
