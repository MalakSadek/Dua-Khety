package malaksadek.duakhety;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by malaksadek on 2/15/18.
 */

public class OneTimeActivity extends AppCompatActivity {

    Button photo, signin, signup, search;
    ImageView image;
    boolean connected;
    List<MenuItem> items;

    void Setup() {
        photo = findViewById(R.id.photo);
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.signup);
        image = findViewById(R.id.image);
        search = findViewById(R.id.search);
    }

    void checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            connected = true;
        else
            connected = false;
    }

    void photoOnClickListener() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(OneTimeActivity.this, new String[] {android.Manifest.permission.CAMERA}, 1000);
        }
        else {
            Intent i = new Intent(getApplicationContext(), AnalyzingActivity.class);
            i.putExtra("OneTime", true);
            i.putExtra("History", false);
            startActivity(i);
        }
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

    void searchOnClickListener() {
        if(connected) {
            //we are connected to a network
            startActivity(new Intent(getApplicationContext(), SearchActivity.class));
        }
        else {
            Toast.makeText(getApplicationContext(), "You must be connected to the internet.", Toast.LENGTH_LONG).show();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onetime);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#BAA24E")));
        getSupportActionBar().setTitle("Dua-Khety");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Setup();
        checkInternet();
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               photoOnClickListener();
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

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchOnClickListener();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        items = new ArrayList<>();


        for(int i=0; i<menu.size(); i++){
            items.add(menu.getItem(i));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int position=items.indexOf(item);

        if (position == 1) {
            CreditsDialogFragment c = new CreditsDialogFragment();
            c.show(getFragmentManager(), "MyDialog");
        }
        else if(position == 0) {
            DevelopersDialogFragment d = new DevelopersDialogFragment();
            d.show(getFragmentManager(), "My Dialog");
        }
        else if(position == 2) {
            Intent i = new Intent(getApplicationContext(), TutorialActivity.class);
            i.putExtra("source", 2);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
