package malaksadek.duakhety;

import android.content.Context;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.Objects;

/**
 * Created by malaksadek on 2/17/18.
 */


public class ResultsActivity extends AppCompatActivity {
    TextView code, description, meaning, linktitle, listlink;
    Button next, previous;
    ImageView aImage, gImage, link;
    Uri u;
    String Code;
    String[] codes;
    ProgressBar pb;
    JSONObject obj;
    int pageIndex;

    void Setup() {
        codes = new String[5];
        code = findViewById(R.id.code);
        description = findViewById(R.id.description);
        meaning = findViewById(R.id.meaning);
        link = findViewById(R.id.link1);
        listlink = findViewById(R.id.listlink);
        linktitle = findViewById(R.id.link2);
        aImage = findViewById(R.id.actualImage);
        gImage = findViewById(R.id.gardinerImage);
        next = findViewById(R.id.nextButton);
        previous = findViewById(R.id.previousButton);
        pb = findViewById(R.id.historyprogress5);
        pageIndex = 0;

        //From analyzing
        if(getIntent().getBooleanExtra("Title", true)) {
            getSupportActionBar().setTitle("Results");
            u = Uri.parse(getIntent().getExtras().getString("Image"));
            aImage.setImageURI(u);
        }
        //From dictionary
        else {
            getSupportActionBar().setTitle("Information");
            Bitmap b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("Image"),0,getIntent()
                            .getByteArrayExtra("Image").length);
            aImage.setImageBitmap(b);
        }


        link.setVisibility(View.INVISIBLE);
        linktitle.setVisibility(View.INVISIBLE);

    }


    void searchDatabase() {

        previous.setClickable(true);
        next.setClickable(true);
        previous.setSelected(true);
        next.setSelected(true);

        if(pageIndex == 0) {
            previous.setClickable(false);
            previous.setSelected(false);
        }
        if(pageIndex == 4) {
            next.setClickable(false);
            next.setSelected(false);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("GardinerCode").document(codes[pageIndex]);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        displayResults(document.get("Description").toString(), document.get("Meaning").toString(), document.get("Link").toString());
                    } else {
                        Log.d("", "No such document");
                    }
                } else {
                    Log.d("", "get failed with ", task.getException());
                }
            }
        });
    }

    void displayResults(final String Description, final String Meaning, final String Link) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("DictionaryPictures/"+codes[pageIndex]+".bin");

        storageRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.i("trying","Not getting image");
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                gImage.setImageBitmap(bmp);

                code.setText(codes[pageIndex]);

                description.setText(Description);
                meaning.setText(Meaning);
                if (!Link.equals("")) {
                    linktitle.setText(Link);
                    link.setVisibility(View.VISIBLE);
                    linktitle.setVisibility(View.VISIBLE);
                }
                pb.setVisibility(View.INVISIBLE);


                if (codes[pageIndex].contains("A")) {
                    if (codes[pageIndex].contains("Aa"))
                        listlink.setText(R.string.Aa);
                    else
                        listlink.setText(R.string.A);
                }
                else
                if (codes[pageIndex].contains("B"))
                    listlink.setText(R.string.B);
                else
                if (codes[pageIndex].contains("C"))
                    listlink.setText(R.string.C);
                else
                if (codes[pageIndex].contains("D"))
                    listlink.setText(R.string.D);
                else
                if (codes[pageIndex].contains("E"))
                    listlink.setText(R.string.E);
                else
                if (codes[pageIndex].contains("F"))
                    listlink.setText(R.string.F);
                else
                if (codes[pageIndex].contains("G"))
                    listlink.setText(R.string.G);
                else
                if (codes[pageIndex].contains("H"))
                    listlink.setText(R.string.H);
                else
                if (codes[pageIndex].contains("I"))
                    listlink.setText(R.string.I);
                else
                if (codes[pageIndex].contains("K"))
                    listlink.setText(R.string.K);
                else
                if (codes[pageIndex].contains("L"))
                    listlink.setText(R.string.L);
                else
                if (codes[pageIndex].contains("M"))
                    listlink.setText(R.string.M);
                else
                if (codes[pageIndex].contains("N"))
                    listlink.setText(R.string.N);
                else
                if (codes[pageIndex].contains("O"))
                    listlink.setText(R.string.O);
                else
                if (codes[pageIndex].contains("P"))
                    listlink.setText(R.string.P);
                else
                if (codes[pageIndex].contains("Q"))
                    listlink.setText(R.string.Q);
                else
                if (codes[pageIndex].contains("R"))
                    listlink.setText(R.string.R);
                else
                if (codes[pageIndex].contains("S"))
                    listlink.setText(R.string.S);
                else
                if (codes[pageIndex].contains("T"))
                    listlink.setText(R.string.T);
                else
                if (codes[pageIndex].contains("U"))
                    listlink.setText(R.string.U);
                else
                if (codes[pageIndex].contains("V"))
                    listlink.setText(R.string.V);
                else
                if (codes[pageIndex].contains("W"))
                    listlink.setText(R.string.W);
                else
                if (codes[pageIndex].contains("X"))
                    listlink.setText(R.string.X);
                else
                if (codes[pageIndex].contains("Y"))
                    listlink.setText(R.string.Y);
                else
                if (codes[pageIndex].contains("Z"))
                    listlink.setText(R.string.Z);


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
        setContentView(R.layout.activity_results);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#BAA24E")));
        Setup();
        pb.setVisibility(View.VISIBLE);
        previous.setClickable(false);
        previous.setSelected(false);
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            if(!Objects.equals(getIntent().getExtras().getString("Code"), "")) {
                Code = getIntent().getExtras().getString("Code");

                codes = Code.split(",");
                searchDatabase();

                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(pageIndex < 4) {
                            pageIndex++;
                            searchDatabase();
                        }

                    }
                });

                previous.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(pageIndex > 0) {
                            pageIndex--;
                            searchDatabase();
                        }

                    }
                });

            }
            else {
                Toast.makeText(getApplicationContext(), "Sorry, we are unable to classify this hieroglyphic!", Toast.LENGTH_LONG).show();

            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Gardiner's Code is: "+Code+"\nYou must be connected to the internet to receive extra information.", Toast.LENGTH_LONG).show();
        }

    }
}
