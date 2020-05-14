package malaksadek.duakhety;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

/**
 * Created by malaksadek on 2/15/18.
 */

public class SignInActivity extends AppCompatActivity {

    Button done;
    EditText email, password;
    String mail, pass, name;
    FirebaseAuth mAuth;
    boolean connected;
    ProgressBar pb;

    void checkInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            connected = true;
        else
            connected = false;
    }

    void Setup() {
        done = findViewById(R.id.signin);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        pb = findViewById(R.id.historyprogress4);
    }

    void doneOnClickListener() {
        pb.setVisibility(View.VISIBLE);
        mail = email.getText().toString();
        pass = password.getText().toString();

        if (connected) {
                mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(mail, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful());
                                if (!task.isSuccessful()) {
                                    pb.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), "Unable to sign in, please try again!", Toast.LENGTH_LONG).show();

                                } else {
                                    pb.setVisibility(View.VISIBLE);
                                    checkIfEmailVerified();
                                }
                                // ...
                            }
                        });

            }
        else {
            Toast.makeText(getApplicationContext(), "You must be connected to the internet.", Toast.LENGTH_LONG).show();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#BAA24E")));
        getSupportActionBar().setTitle("Sign In");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkInternet();
        Setup();
        pb.setVisibility(View.INVISIBLE);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneOnClickListener();
            }
        });
    }

    void updateSharedPreferences() {
        SharedPreferences email = getSharedPreferences("DuaKhetyPrefs", 0);
        final SharedPreferences.Editor editor = email.edit();
        editor.putString("Email", mail);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                for (int i = 0; i < task.getResult().size(); i++) {
                                    if(Objects.equals(document.get("Email").toString(), mail)){
                                        name = document.get("Owner").toString();
                                        editor.putString("Name", name);
                                        editor.putString("Password", pass);
                                        editor.putBoolean("First", false);
                                        editor.commit();
                                    }
                                }
                            }
                        } else {
                            Log.d("", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified()) {
            updateSharedPreferences();
            startActivity(new Intent(getApplicationContext(), SuccessActivity.class));
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Unknown error, please contact the developers!", Toast.LENGTH_LONG).show();

        }
    }
}
