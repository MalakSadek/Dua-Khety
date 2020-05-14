package malaksadek.duakhety;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DictionaryActivity extends AppCompatActivity {

    ArrayList<DictionaryItem> dictionaryArray;
    ProgressBar pb;
    ListView dictionaryList;
    TextView counterlabel;
    String chosenCode;
    String[] codes;
    List<MenuItem> items;
    ImageView background;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#BAA24E")));
        getSupportActionBar().setTitle("Dictionary");
        codes = new String[800];
        counterlabel = findViewById(R.id.counterlabel);
        dictionaryArray = new ArrayList<>(800);
        dictionaryArray.ensureCapacity(800);
        dictionaryList = findViewById(R.id.dictionaryList);
        background = findViewById(R.id.imageView11);

        pb = findViewById(R.id.progressBar2);
        Toast.makeText(getApplicationContext(), "Please be patient, this takes a while as there are more than 700 hieroglyphics!", Toast.LENGTH_LONG).show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("GardinerCode")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            i = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    createDictionaryElement(document.getId(), document.get("Description").toString(), document.get("Meaning").toString());
                                }
                            }
                        } else {
                            Log.d("", "Error getting documents: ", task.getException());
                        }
                    }
                });


        dictionaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                dictionaryListOnClickListener(index);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());
        finish();
    }

    void createDictionaryElement(final String code, final String description, final String meaning) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("DictionaryPictures/"+code+".bin");
        storageRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                DictionaryItem temp = new DictionaryItem();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                temp.picture = bytes;
                temp.GCode = code;
                temp.description = description;
                temp.meaning = meaning;
                codes[i] = code;
             //   float width = Resources.getSystem().getDisplayMetrics().widthPixels;
             //   temp.SpaceWidth = (int) width/3;
                dictionaryArray.add(temp);

                Collections.sort(dictionaryArray, new Comparator<DictionaryItem>() {
                    @Override
                    public int compare(DictionaryItem t1, DictionaryItem t2) {
                        return t1.getGCode().compareTo(t2.getGCode());
                    }
                });

                CustomAdapterDictionary CA = new CustomAdapterDictionary(getApplicationContext(), dictionaryArray);
                ListView list = findViewById(R.id.dictionaryList);

                list.setAdapter(CA);
                i++;
                String temptext = "Loading "+i+" out of 740...";
                counterlabel.setText(temptext);

                if (i > 730) {
                    pb.setVisibility(View.INVISIBLE);
                    counterlabel.setVisibility(View.INVISIBLE);
                    background.setVisibility(View.INVISIBLE);
                }
            }
        });

    }


    void dictionaryListOnClickListener(int index) {
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        chosenCode = codes[index];


        SharedPreferences sp = getSharedPreferences("DuaKhetyPrefs", 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Code", chosenCode);
        editor.commit();

        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.infomenu, menu);

        items = new ArrayList<>();


        for(int i=0; i<menu.size(); i++){
            items.add(menu.getItem(i));
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int position=items.indexOf(item);

        if (position == 0) {
            InformationDialogFragment d = new InformationDialogFragment();
            d.show(getFragmentManager(), "My Dialog");
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            finish();
        }

        return true;
    }

}
