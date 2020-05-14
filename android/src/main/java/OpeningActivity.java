package malaksadek.duakhety;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by malaksadek on 2/15/18.
 */

public class OpeningActivity extends AppCompatActivity {

    SharedPreferences firsttime;
    Button cont, signin, signup;
    boolean connected;

    boolean checkFirst() {
        firsttime = getSharedPreferences("DuaKhetyPrefs", 0);
        boolean first = firsttime.getBoolean("First", true);
        return first;
    }

    void Setup() {
        cont = findViewById(R.id.continuetoapp);
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.signup);
    }

    void checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            connected = true;
        else
            connected = false;
    }


    void signupOnClickListener() {
            if (connected) {
            //we are connected to a network
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        else {
            Toast.makeText(getApplicationContext(), "You must be connected to the internet.", Toast.LENGTH_LONG).show();
        }
    }

    void signinOnClickListener() {
        if(connected) {
            //we are connected to a network
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        }
        else {
            Toast.makeText(getApplicationContext(), "You must be connected to the internet.", Toast.LENGTH_LONG).show();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#BAA24E")));
        getSupportActionBar().setTitle("Welcome");

        checkInternet();

        if (checkFirst()) {
            Setup();

            boolean tutorial = firsttime.getBoolean("Tutorial", true);

            if (tutorial) {
                Intent i = new Intent(getApplicationContext(), TutorialActivity.class);
                i.putExtra("source", 0);
                startActivity(i);
            }
            else {
                cont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent (getApplicationContext(), OneTimeActivity.class));
                    }
                });
                signup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signupOnClickListener();
                    }
                });

                signin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signinOnClickListener();
                    }
                });
            }
        }
    else
    {
        startActivity(new Intent(getApplicationContext(), SuccessActivity.class));
    }

}

}
