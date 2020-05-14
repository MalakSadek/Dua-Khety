package malaksadek.duakhety;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by malaksadek on 2/17/18.
 */


//TODO: LINKS FOR BOTH THE PAGE AND USEFUL INFORMATION

public class SearchActivity extends AppCompatActivity {
    TextView code, description, meaning, linktitle, listlink;
    ImageView gImage, link, codeTitle, descriptionTitle, meaningTitle, symbolinfo, usefullinks;
    Uri u;
    String Code;
    Button searchButton;
    EditText input;
    List<MenuItem> items;
    ProgressBar pb;


    void Setup() {
        code = findViewById(R.id.code);
        description = findViewById(R.id.description);
        meaning = findViewById(R.id.meaning);
        link = findViewById(R.id.link1);
        listlink = findViewById(R.id.listlink);
        linktitle = findViewById(R.id.link2);
        gImage = findViewById(R.id.gardinerImage);
        searchButton = findViewById(R.id.searchbutton);
        input = findViewById(R.id.codeInput);
        usefullinks = findViewById(R.id.usefullinks);
        symbolinfo = findViewById(R.id.infosymbols);
        listlink.setMovementMethod(LinkMovementMethod.getInstance());
        linktitle.setMovementMethod(LinkMovementMethod.getInstance());
        pb = findViewById(R.id.historyprogress3);
        codeTitle = findViewById(R.id.codeTitle);
        descriptionTitle = findViewById(R.id.descriptionTitle);
        meaningTitle = findViewById(R.id.meaningTitle);


        usefullinks.setVisibility(View.INVISIBLE);
        symbolinfo.setVisibility(View.INVISIBLE);
        code.setVisibility(View.INVISIBLE);
        description.setVisibility(View.INVISIBLE);
        meaning.setVisibility(View.INVISIBLE);
        link.setVisibility(View.INVISIBLE);
        listlink.setVisibility(View.INVISIBLE);
        linktitle.setVisibility(View.INVISIBLE);
        gImage.setVisibility(View.INVISIBLE);

        codeTitle.setVisibility(View.INVISIBLE);
        descriptionTitle.setVisibility(View.INVISIBLE);
        meaningTitle.setVisibility(View.INVISIBLE);
    }

    void searchDatabase() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("GardinerCode").document(Code);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        displayResults(document.get("Description").toString(), document.get("Meaning").toString(), document.get("Link").toString());
                    } else {
                        code.setVisibility(View.INVISIBLE);
                        description.setVisibility(View.INVISIBLE);
                        meaning.setVisibility(View.INVISIBLE);
                        link.setVisibility(View.INVISIBLE);
                        listlink.setVisibility(View.INVISIBLE);
                        linktitle.setVisibility(View.INVISIBLE);
                        gImage.setVisibility(View.INVISIBLE);
                        usefullinks.setVisibility(View.INVISIBLE);
                        symbolinfo.setVisibility(View.INVISIBLE);
                        codeTitle.setVisibility(View.INVISIBLE);
                        descriptionTitle.setVisibility(View.INVISIBLE);
                        meaningTitle.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Incorrect Gardiner Code, try again.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("", "get failed with ", task.getException());
                }
            }
        });
    }


    void displayResults(final String Description, final String Meaning, final String Link) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("DictionaryPictures/"+Code+".bin");

        storageRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.i("trying","Not getting image");
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                gImage.setImageBitmap(bmp);

                code.setText(Code);

                description.setText(Description);
                meaning.setText(Meaning);
                if (!Link.equals("")) {
                    linktitle.setText(Link);
                    link.setVisibility(View.VISIBLE);
                    linktitle.setVisibility(View.VISIBLE);
                }


                if (Code.contains("A")) {
                    if (Code.contains("Aa"))
                        listlink.setText(R.string.Aa);
                    else
                        listlink.setText(R.string.A);
                }
                else
                if (Code.contains("B"))
                    listlink.setText(R.string.B);
                else
                if (Code.contains("C"))
                    listlink.setText(R.string.C);
                else
                if (Code.contains("D"))
                    listlink.setText(R.string.D);
                else
                if (Code.contains("E"))
                    listlink.setText(R.string.E);
                else
                if (Code.contains("F"))
                    listlink.setText(R.string.F);
                else
                if (Code.contains("G"))
                    listlink.setText(R.string.G);
                else
                if (Code.contains("H"))
                    listlink.setText(R.string.H);
                else
                if (Code.contains("I"))
                    listlink.setText(R.string.I);
                else
                if (Code.contains("K"))
                    listlink.setText(R.string.K);
                else
                if (Code.contains("L"))
                    listlink.setText(R.string.L);
                else
                if (Code.contains("M"))
                    listlink.setText(R.string.M);
                else
                if (Code.contains("N"))
                    listlink.setText(R.string.N);
                else
                if (Code.contains("O"))
                    listlink.setText(R.string.O);
                else
                if (Code.contains("P"))
                    listlink.setText(R.string.P);
                else
                if (Code.contains("Q"))
                    listlink.setText(R.string.Q);
                else
                if (Code.contains("R"))
                    listlink.setText(R.string.R);
                else
                if (Code.contains("S"))
                    listlink.setText(R.string.S);
                else
                if (Code.contains("T"))
                    listlink.setText(R.string.T);
                else
                if (Code.contains("U"))
                    listlink.setText(R.string.U);
                else
                if (Code.contains("V"))
                    listlink.setText(R.string.V);
                else
                if (Code.contains("W"))
                    listlink.setText(R.string.W);
                else
                if (Code.contains("X"))
                    listlink.setText(R.string.X);
                else
                if (Code.contains("Y"))
                    listlink.setText(R.string.Y);
                else
                if (Code.contains("Z"))
                    listlink.setText(R.string.Z);

                code.setVisibility(View.VISIBLE);
                description.setVisibility(View.VISIBLE);
                meaning.setVisibility(View.VISIBLE);
                listlink.setVisibility(View.VISIBLE);
                gImage.setVisibility(View.VISIBLE);
                usefullinks.setVisibility(View.VISIBLE);
                symbolinfo.setVisibility(View.VISIBLE);
                codeTitle.setVisibility(View.VISIBLE);
                descriptionTitle.setVisibility(View.VISIBLE);
                meaningTitle.setVisibility(View.VISIBLE);
                pb.setVisibility(View.INVISIBLE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#BAA24E")));
        getSupportActionBar().setTitle("Dictionary");
        Setup();
        pb.setVisibility(View.INVISIBLE);
        SharedPreferences sp = getSharedPreferences("DuaKhetyPrefs", 0);
        String code = sp.getString("Code", "");

        if(code != "") {
            Code = code;
            pb.setVisibility(View.VISIBLE);
            input.setText(code);
            searchDatabase();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Code", "");
            editor.commit();
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        Code = input.getText().toString();
                        pb.setVisibility(View.VISIBLE);
                        searchDatabase();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "You must be connected to the internet.", Toast.LENGTH_LONG).show();
                    }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dictionarymenu, menu);

        items = new ArrayList<>();


        for(int i=0; i<menu.size(); i++){
            items.add(menu.getItem(i));
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        startActivity(new Intent(getApplicationContext(), DictionaryActivity.class));
        return true;
    }
}
