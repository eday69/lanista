package com.example.eday.lanista;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class credentials extends AppCompatActivity {
    public static final String profile_id = "com.example.eday.lanista.profile_id";

    userProfile user_id = new userProfile();

    public Button signInButton;
    public TextView forgotPwd;
    public TextView mEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);

        mEmail=findViewById((R.id.email));
        // User tried to register again, so he got sent to this page to
        // log in, but since we already have his email, display it.
        mEmail.setText(getIntent().getStringExtra("USER_EMAIL"));

        signInButton = findViewById(R.id.register);
        // Change text of button from register to login
        signInButton.setText("Login");
        // Change button listener
        signInButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
                tryLogin();
            }
        });
        forgotPwd = findViewById(R.id.textView18);
        // Change text of link to Reset Pwd
        forgotPwd.setText("Reset Pwd");

    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void tryLogin() {
        final TextView email=findViewById((R.id.email));
        TextView password=findViewById((R.id.password));

        if (!isEmailValid( email.getText().toString())) {
            Toast.makeText( getBaseContext(), "Email not valid, please check !", Toast.LENGTH_LONG )
                    .show();
            return;

        }

        JSONObject json = new JSONObject();
        try {
            json.put("email", email.getText() );
            json.put("password", password.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue= Volley.newRequestQueue(this);

//        String myURL="http://18.218.188.251:3000/api/profile";
        String myURL="http://10.0.2.2:3000/api/validateLogin";
        JsonObjectRequest objectRequest=new JsonObjectRequest(
                Request.Method.POST,
                myURL,
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Credentials", "Ready for reply");
                        try {
                            Log.e("Credentials", response.toString(4));
                            String id =   response.getString("id");
                            if (id != "null") {
                                Integer user_account_status_id = Integer.valueOf( response.getString( "user_account_status_id" ) );
                                switch (user_account_status_id) {
                                    case 20: // Waiting for email confirmation
                                        // Show message, do nothing.
                                        Toast.makeText( getBaseContext(), "Need to confirm email sent when registered", Toast.LENGTH_LONG )
                                                .show();
                                        break;
                                    case 30: // Received email confirmation
                                        // Check information, if incomplete, change status
                                        // and goto to info page
                                        goToInformationPage(Integer.parseInt( id ));
                                        break;
                                    case 50: // Incomplete information
                                        // go to info page.
                                        break;
                                    default:  // valid user?
                                        goToLandingPage(Integer.parseInt( id ));
                                        break;
                                }
                            }
                            else {
                                String msg = null;
                                try {
                                    msg = response.getString("msg");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (msg.length() > 0) {
                                    Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG)
                                            .show();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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

    private void goToLandingPage(int id) {
        Intent intent = new Intent( this, landingPage.class );
        intent.putExtra("USER_ID",  id );
        startActivity( intent );
    }

    private void goToInformationPage(int id) {
        Intent intent = new Intent( this, userInformation.class );
        intent.putExtra("USER_ID",  id );
        startActivity( intent );
    }

}
