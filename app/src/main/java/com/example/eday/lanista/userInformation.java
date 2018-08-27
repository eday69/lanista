package com.example.eday.lanista;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class userInformation extends AppCompatActivity {

    public class StringWithTag {
        public String string;
        public Integer id;

        public StringWithTag(String stringPart, Integer idPart) {
            string = stringPart;
            id = idPart;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_information);

        // Populate role spinner
        getRoles();

    }

    public void showDatePicker(View v) {
        DialogFragment newFragment = new MyDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "date picker");
    }

    public void onItemSelected(AdapterView<?> parant, View v, int pos, long id2) {
        StringWithTag s = (StringWithTag) parant.getItemAtPosition(pos);
        Object id = s.id;
        if (null != id && id instanceof String) {
            // Show toast
            Toast.makeText(getApplicationContext(), (String) id, Toast.LENGTH_SHORT).show();
        }
    }

    public void saveInfo() {
        final TextView email=findViewById((R.id.email));
        TextView password=findViewById((R.id.password));

        // Get role.
        // Get DOB
        // Get Name & LastName
        JSONObject json = new JSONObject();
        try {
            json.put("email", email.getText() );
            json.put("password", password.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue= Volley.newRequestQueue(this);

        Log.e("Sending Json", json.toString());
//        String myURL="http://18.218.188.251:3000/api/profile";
        String myURL="http://10.0.2.2:3000/api/login";
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
                            String id =  response.getString("id");
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
                                        break;
                                    case 50: // Incomplete information
                                        // go to info page.
                                        break;
                                    default:  // valid user?
                                        goToLandingPage();
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


    private void getRoles() {
        final TextView email=findViewById((R.id.email));
        TextView password=findViewById((R.id.password));

        RequestQueue requestQueue= Volley.newRequestQueue(this);

//        String myURL="http://18.218.188.251:3000/api/roles";
        String myURL="http://10.0.2.2:3000/api/roles";
        JsonArrayRequest objectRequest=new JsonArrayRequest(
                Request.Method.GET,
                myURL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Getting Roles", "Requested roles");
                        try {

                            List<StringWithTag> spnRoles = new ArrayList<StringWithTag>();

                            for(int i=0;i<response.length();i++){
                                JSONObject jsonObject1=response.getJSONObject(i);

                                String id=jsonObject1.getString("id");
                                String role=jsonObject1.getString("role");
                                spnRoles.add(new StringWithTag(role, Integer.parseInt(id)));
                            }
                            Spinner roles = findViewById(R.id.roles);
                            ArrayAdapter<StringWithTag> adap = new ArrayAdapter<> (getApplicationContext(), android.R.layout.simple_spinner_item, spnRoles);

                            roles.setAdapter(adap);

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

    private void goToLandingPage() {
        Intent intent = new Intent( this, landingPage.class );
        startActivity( intent );
    }

}
