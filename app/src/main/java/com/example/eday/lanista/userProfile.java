package com.example.eday.lanista;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import javax.annotation.Nullable;

public class userProfile extends Application {

    // lanista user profile class

    // Fields
//    public String mURL="http://18.218.188.251:3000/";
    public String mURL="http://10.0.2.2:3000/";

    public int    id;
    public String last_name;
    public String first_name;
    public String full_name;
    public Date   user_dob;
    public int    user_role_id;
    public String email;
    public String password;
    public int    user_account_status_id;
    public String social_media;
    public String social_media_token;

    // Constructor
    public userProfile() {
        id = 0;
        last_name = null;
        first_name = null;
        full_name = null;
        user_dob = null;
        user_role_id = 0;
        email = null;
        password = null;
        user_account_status_id = 0;
        social_media = null;
        social_media_token = null;
    }

    public userProfile(@Nullable int new_id, @Nullable String new_last_name,
                       @Nullable String new_first_name, @Nullable String new_full_name,
                       @Nullable Date new_user_dob, @Nullable int new_user_role_id,
                       @Nullable String new_email, @Nullable String new_password,
                       @Nullable int new_user_account_status_id,
                       @Nullable String new_social_media, @Nullable String new_social_media_token) {
        id = new_id;
        last_name = new_last_name;
        first_name = new_first_name;
        full_name = new_full_name;
        user_dob = new_user_dob;
        user_role_id = new_user_role_id;
        email = new_email;
        password = new_password;
        user_account_status_id = new_user_account_status_id;
        social_media = new_social_media;
        social_media_token = new_social_media_token;
    }

    public void setProfileId(int newProfileId) {
        id = newProfileId;
    }

    public void setEmail(String newEmail) {
        email = newEmail;
    }

    public void setPwd(String newPwd) {
        password = newPwd;
    }

    public void setName(String newLastName, String newFirstName) {
        first_name = newFirstName;
        last_name = newLastName;
        full_name = newFirstName + " " + newLastName;
    }

    public void setSocialMediaLogin(String newSocialMedia, String newSocialMediaToken) {
        social_media = newSocialMedia;
        social_media_token = newSocialMediaToken;
    }

    public void setUserDob(Date newDob) {
        user_dob = newDob;
    }

    public void setUserRole(int newRole) {
        user_role_id = newRole;
    }

    public void setUsetAccountStatusId(int newUserAccountStatusId) {
        user_account_status_id = newUserAccountStatusId;
    }

    public int getUserProfileId() {
        return id;
    }

    public JSONObject getSocialMediaCredentials() {

        JSONObject json = new JSONObject();
        try {
            json.put("social_media", social_media);
            json.put("social_media_token", social_media_token);
            json.put("email", email);
            json.put("first_name", first_name);
            json.put("last_name", last_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONObject getEmailCredentials() {

        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public void doLogin(final Context ctx, JSONObject json) {
        RequestQueue requestQueue= Volley.newRequestQueue(ctx);

        JsonObjectRequest objectRequest=new JsonObjectRequest(
                Request.Method.POST,
                mURL+"api/profile",
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String user_id = response.getString( "id" );
                            if (user_id.length() > 0) {

                                Intent intent = new Intent(ctx, landingPage.class);
                                intent.putExtra("USER_ID", user_id);
                                ctx.startActivity(intent);

                            }
                            else {
                                Toast.makeText(ctx,response.getString( "msg" ) , Toast.LENGTH_LONG)
                                        .show();
                            }

                        } catch (JSONException e) {
                            Log.e("doLogin", "Que paso?");
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


}
