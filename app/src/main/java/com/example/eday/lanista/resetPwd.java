package com.example.eday.lanista;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class resetPwd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
    }

    /** Called when the user taps the Send button */
    public void doLogin(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, credentials.class);
        startActivity(intent);
    }

}
