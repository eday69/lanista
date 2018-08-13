package com.example.eday.lanista;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

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
