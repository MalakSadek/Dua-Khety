package malaksadek.duakhety;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    EditText name, email, password, vpassword;
    String Name, Email, Password, Vpassword;
    Boolean sent = false;
    Button done;
    FirebaseAuth mAuth;
    boolean connected;
    ProgressBar pb;

    void checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            connected = true;
        else
            connected = false;
    }

    void Setup() {
        done = findViewById(R.id.done);
        name = findViewById(R.id.textName);
        email = findViewById(R.id.textEmail);
        password = findViewById(R.id.textPassword);
        vpassword = findViewById(R.id.textPassword2);
        pb = findViewById(R.id.historyprogress2);
    }

    void createAccount() {
            Toast.makeText(getApplicationContext(), "Creating Profile!", Toast.LENGTH_LONG).show();
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful());
                    if (!task.isSuccessful()) {
                        Log.d("TAG", "onComplete: Failed=" + task.getException().getMessage());
                        if (task.getException().getMessage().contains("password"))
                            Toast.makeText(getApplicationContext(), "Your password needs to be at least 6 characters, please try again!", Toast.LENGTH_SHORT).show();
                        else if (task.getException().getMessage().contains("email address"))
                            Toast.makeText(getApplicationContext(), "This email is already registered, please try again!", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "There was an error creating your profile, please try again!", Toast.LENGTH_SHORT).show();

                    } else {
                        if (!sent) {
                            sent = true;
                            sendVerificationEmail();
                        }
                    }

                }
            });
    }

    void collectInformation() {
        Name = name.getText().toString();
        Email = email.getText().toString();
        Password = password.getText().toString();
        Vpassword = vpassword.getText().toString();
    }

    void doneOnClickListener(){
        collectInformation();

        if(connected) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int unique = 1;
                                for (DocumentSnapshot document : task.getResult()) {
                                        if(Objects.equals(document.get("Owner").toString(), Name)) {
                                            unique = 0;
                                        }
                                    }

                                if (!Email.contains("@")) {
                                    Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_LONG).show();
                                } else if (!Password.equals(Vpassword)) {
                                    Toast.makeText(getApplicationContext(), "Passwords do not match.", Toast.LENGTH_LONG).show();
                                } else if ((Email.equals(null)) || (Password.equals(null)) || (Vpassword.equals(null)) || (Name.equals(null))) {
                                    Toast.makeText(getApplicationContext(), "Missing information, please fill all the fields.", Toast.LENGTH_LONG).show();
                                } else if (unique == 0) {
                                    Toast.makeText(getApplicationContext(), "Username already exists, please select another one.", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    pb.setVisibility(View.VISIBLE);
                                    createAccount();
                                }

                                } else {
                                Log.d("", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
            else {
                Toast.makeText(getApplicationContext(), "You must be connected to the internet.", Toast.LENGTH_LONG).show();
            }
        }

    void updateSharedPreferences() {
        SharedPreferences email = getSharedPreferences("DuaKhetyPrefs", 0);
        SharedPreferences.Editor editor = email.edit();
        editor.putString("Name", Name);
        editor.putString("Email", Email);
        editor.putString("Password", Password);
        editor.putBoolean("First", false);
        editor.commit();
    }

    private void sendVerificationEmail()
    {
        Toast.makeText(getApplicationContext(), "Sending Verification Email", Toast.LENGTH_SHORT).show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // email sent
                                // after email is sent just logout the user and finish this activity
                                updateSharedPreferences();
                                pb.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(getApplicationContext(), VerificationActivity.class));
                            }
                            else
                            {
                                // email not sent, so display message and restart the activity or do whatever you wish to do
                                //restart this activity
                                Toast.makeText(getApplicationContext(), "There was an error sending the verification email, please try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#BAA24E")));
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Setup();
        pb.setVisibility(View.INVISIBLE);
        checkInternet();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               doneOnClickListener();
            }
                                }
                );
    }

}
