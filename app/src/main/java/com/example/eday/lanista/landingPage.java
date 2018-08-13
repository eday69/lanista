package com.example.eday.lanista;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.util.TypedValue;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import com.example.eday.lanista.view.viewgroup.FlyOutContainer;


public class landingPage extends AppCompatActivity  {

    FlyOutContainer root;

    // For debuggin purposes
    private static final String TAG = landingPage.class.getSimpleName();
    // End of debugging
    // *************************
    GoogleApiClient mGoogleApiClient;
    boolean mSignInClicked;

    private GoogleSignInClient mGoogleSignInClient;
    private View content;

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView idTextView;
    private TextView logStatusTextView;

    private GoogleSignInAccount googleAcct;

    public Button GLogout;
    public String id, name, email, gender, birthday;

    private TableLayout mcoachtable;


    private View.OnClickListener contentOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            contentClicked();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = (FlyOutContainer) this.getLayoutInflater().inflate(R.layout.activity_landing_page, null);
        setContentView(root);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        content = findViewById(R.id.content);
        content.setOnClickListener(contentOnClickListener);
        //        setContentView(R.layout.activity_landing_page);


        // Shared login
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        idTextView = (TextView) findViewById(R.id.idTextView);
        logStatusTextView = (TextView) findViewById(R.id.logStatus);

        mcoachtable = findViewById(R.id.tableCoachTeams);


        GLogout = findViewById(R.id.GLogout);

        GLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    logoutFromFacebook(); // Logout from Facebook
                }
                else {
                    // LogOut from Google
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status status) {
                                    // ...
                                    emailTextView.setText(getString(R.string.signed_in_fmt, "Logged Out !!!!"));
                                }
                            });
                    // Go to Register Screen after logout.
                    Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                }
                goRegisterScreen();
            }
        });


    }

    @Override
    protected void onStart() {

        // Get the Intent that started this activity and extract the string
        googleAcct= getIntent().getParcelableExtra("googleAcct");
        Log.d(TAG, "In ! googleAcct" + googleAcct);

        //Fetch values
        if (googleAcct != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            mGoogleApiClient.connect();

            String id = googleAcct.getId();
            String name = googleAcct.getDisplayName();
            String email = googleAcct.getEmail();


            Bundle gBundle = new Bundle();

            gBundle.putString("method", "google");
            gBundle.putString("id", id);
            gBundle.putString("name", name);
            gBundle.putString("email", email);

            // Todo:
            // Save in DB login credentials from Google.

            Log.d(TAG, "Got google credentials, now go to updateui");
            updateUI(true, gBundle);
        }
        else {


            /*
        super.onStart();
*/
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (accessToken != null) {
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                Log.d(TAG, "Facebook log in !!");
                                Log.d(TAG, String.valueOf(object));
                                // Application code
                                Bundle fb_b = new Bundle();

                                try {
                                    id = object.getString("id");
                                    name = object.getString("name");
                                    fb_b.putString("method", "fb");
                                    fb_b.putString("id", id);
                                    fb_b.putString("name", name);
                                    Log.v(TAG, "bef Name: " + name);
                                    Log.e("UserDate", String.valueOf(object));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                updateUI(true, fb_b);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link");
                request.setParameters(parameters);
                request.executeAsync();
            }

        }
        super.onStart();

        addTeams();

    }

    private void updateUI(Boolean isLogin, Bundle loginData) {
        Log.v(TAG, "in updateUI");
        String logMethod = loginData.getString("method");
        String Id = "";
        String Name = "";
        String Email = "";
        String ProfilePic = "";

        switch (logMethod) {
            case "fb":
                Log.d(TAG, "updateui. logmethod is fb");
                Id = loginData.getString("id");
                Name = loginData.getString("name");
                Log.v(TAG, "aft Name: "+Name);
                ProfilePic = loginData.getString("profile_pic");
                break;
            case "google":
                Log.d(TAG, "updateui. logmethod is google");
                Id = loginData.getString("id");
                Name = loginData.getString("name");
                Email = loginData.getString("email");
                break;
            default:
                break;
        }
        Glide.with(this).load(ProfilePic)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);

        logStatusTextView.setText("Signed from " + logMethod);
        idTextView.setText("Id: "+Id);
        emailTextView.setText("email: "+Email);
        nameTextView.setText("Welcome "+Name);
    }

    public void signOut(View view) {
        Log.v(TAG, "Signing OUT ! (Google) ");
//        mGoogleSignInClient.signOut();
        Log.v(TAG, "Elvis has left the building ");
    }

    private void goRegisterScreen() {
        Log.v(TAG, "in goRegisterScreen");
        Intent intent = new Intent(this, register.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logoutFromFacebook(){
        LoginManager.getInstance().logOut();
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

    private void addTeams() {

        addTeamInfo(1, "Chivas", 1);
        addTeamInfo(2, "Leones", 2);
        addTeamInfo(3, "Tablajeros de occidente", 3);
        addTeamInfo(4, "Flying girls", 4);
        addTeamInfo(5, "Yankees of Calgary", 5);
        addTeamInfo(6, "Jordan sons", 6);
    }

    private void addTeamInfo(int i, String teamName, int ballType) {
        int leftRowMargin=0;
        int topRowMargin=0;
        int rightRowMargin=0;
        int bottomRowMargin = 0;
        String mballType="";

        switch (ballType) {
            case 1:
                mballType="soccer_ball";
                break;
            case 2:
                mballType="football";
                break;
            case 3:
                mballType="volley_ball";
                break;
            case 4:
                mballType="lacross";
                break;
            case 5:
                mballType="baseball";
                break;
            case 6:
                mballType="basketball";
                break;

        }

        // This is the team ball image
        final ImageView iv1 = new ImageView(this);
            iv1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            iv1.setImageResource(getResources().getIdentifier(mballType, "drawable", getPackageName()));
            iv1.setPadding(10, 3, 3, 3);
            iv1.setMaxWidth(40);

        // This is the team name info
        final TextView tv2 = new TextView(this);
            tv2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.MATCH_PARENT));

            tv2.setGravity(Gravity.LEFT);
            tv2.setPadding(15, 0, 0, 0);
            tv2.setTextColor(Color.parseColor("#ff00ff"));
            tv2.setText(teamName);
            tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    teamClicked();
                }
            });

//        content.setOnClickListener(contentOnClickListener);

        // New table row
        final TableRow tr = new TableRow(this);
            tr.setId(i);
            TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            trParams.setMargins(leftRowMargin, topRowMargin, rightRowMargin, bottomRowMargin);
            tr.setPadding(15,7,0,8);
            tr.setLayoutParams(trParams);

        // Add the image view and the text view
        tr.addView(iv1);
        tr.addView(tv2);

        // Add to table the row
        mcoachtable.addView(tr, trParams);

    }

    private void teamClicked() {
        Intent intent = new Intent(this, teamLanding.class);
        startActivity(intent);
    }

    public void doNewTeam(View v){
        Intent intent = new Intent(this, newTeam.class);
        startActivity(intent);
    }

}
