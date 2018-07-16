package com.example.eday.lanista;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class credentials extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);
    }

    /** Called when the user taps the Send button */
    public void dolandingPage(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, landingPage.class);
        startActivity(intent);
    }

    public void doresetPwd(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, resetPwd.class);
        startActivity(intent);
    }

}
