package com.example.eday.lanista;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.eday.lanista.view.viewgroup.FlyOutContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class newTeam extends AppCompatActivity {

    FlyOutContainer root;
    private View content;
    private Spinner sports;

    private View.OnClickListener contentOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            contentClicked();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = (FlyOutContainer) this.getLayoutInflater().inflate(R.layout.activity_newteam, null);
        setContentView(root);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        content = findViewById(R.id.content);
        content.setOnClickListener(contentOnClickListener);

//        setContentView(R.layout.activity_newteam);

        sports = findViewById(R.id.sports);

        List<String> sportsType=new ArrayList<String>();
        sportsType.add("Soccer");
        sportsType.add("Football");
        sportsType.add("Volleyball");
        sportsType.add("Beisball");
        sportsType.add("LaCross");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, sportsType);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sports.setAdapter(dataAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.menu_home:
                Toast.makeText(this, "Menu Home selected", Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent(this, landingPage.class);
                startActivity(intent);
                break;
            case R.id.settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                this.root.toggleMenu();
                break;
            // action with ID action_settings was selected
            case R.id.action_settings:
                Toast.makeText(this, "Action Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

        return true;
    }

    public void toggleMenu(View v){
        this.root.toggleMenu();
    }

    private void contentClicked() {
        this.root.toggleMenu();
    }


}