package com.example.eday.lanista;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;


public class userInformation extends AppCompatActivity {
    public static String profile_id = "com.example.eday.lanista.profile_id";

    // We can access the profile_id anywhere in this class !

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

        // Get me the profile_id of the user who landed us here.
        Intent intent = getIntent();
        profile_id = intent.getStringExtra("profile_id");

        Button saveInfoBtn = findViewById(R.id.saveInfo);
        // Change button listener
        saveInfoBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Do stuff here
                saveInfo();
            }
        });

        Spinner roles = findViewById(R.id.roles);

        // Populate role spinner
        getRoles(roles);

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

        // Get role
        Spinner roles = findViewById(R.id.roles);
        String textRole = roles.getSelectedItem().toString();
        Toast.makeText( getBaseContext(), textRole, Toast.LENGTH_LONG )
                .show();

        StringWithTag swt = (StringWithTag) roles.getSelectedItem();
        Integer keyRole = (Integer) swt.id;

        Toast.makeText( getBaseContext(), "Role : "+keyRole.toString(), Toast.LENGTH_LONG )
                .show();


        // Get DOB
        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();

        String strDate = String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(day);

        Toast.makeText( getBaseContext(), strDate, Toast.LENGTH_LONG )
                .show();

        // Get Name & LastName
        TextView txtFirstName=findViewById((R.id.txtFirstName));
        TextView txtLastName=findViewById((R.id.txtLastName));
        Toast.makeText( getBaseContext(), txtFirstName.getText(), Toast.LENGTH_LONG )
                .show();
        Toast.makeText( getBaseContext(), txtLastName.getText(), Toast.LENGTH_LONG )
                .show();

        JSONObject json = new JSONObject();
        try {
            json.put("user_profile_id", profile_id );
            json.put("user_role_id", keyRole );
            json.put("first_name", txtFirstName.getText());
            json.put("last_name", txtLastName.getText());
            json.put("user_dob", strDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue= Volley.newRequestQueue(this);

        Log.e("Sending Json", json.toString());
//        String myURL="http://18.218.188.251:3000/api/profile";
        String myURL="http://10.0.2.2:3000/api/profile/"+profile_id;

        JsonObjectRequest objectRequest=new JsonObjectRequest(
                Request.Method.PUT,
                myURL,
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("userinformation", "Ready for reply");
                        try {
                            Log.e("userinformation", response.toString(4));
                            String user_account_status_id =  response.getString("user_account_status_id");
                            if (user_account_status_id != "100") {  // profile is complete
                                goToLandingPage();
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


    private void getRoles(final Spinner roles) {
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
        intent.putExtra("profile_id",  profile_id );
        startActivity( intent );
    }

}
