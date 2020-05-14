package malaksadek.duakhety;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.util.Base64;

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

/**
 * Created by malaksadek on 2/15/18.
 */

public class HistoryActivity extends AppCompatActivity{
    Item temp;
    String owner, code;
    ListView list;
    Button clear;
    Bitmap[] bmp;
    ArrayList<Item> historyArray;
    ProgressBar histprog;

    void Setup() {
        clear = findViewById(R.id.clear);
        historyArray = new ArrayList<>();
        histprog = findViewById(R.id.historyprogress);
        bmp = new Bitmap[100];
    }

    void createHistoryElement(final String name, final String code, final int i) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("UserPictures/"+name+code+".bin");

        storageRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.i("trying","Not getting image");
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                bmp[i] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                Log.i("image pls", bmp.toString());
                temp = new Item();
                temp.Name = name;
                temp.Code = code;
                temp.history = 1;

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp[i].compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageInByte = baos.toByteArray();
                String encodedString;
                encodedString = Base64.encodeToString(imageInByte, Base64.DEFAULT);
                encodedString = encodedString.replaceAll(" ", "");
                encodedString = encodedString.replaceAll("\n", "");
                temp.FeedImage = encodedString;
                float width = Resources.getSystem().getDisplayMetrics().widthPixels;
                temp.SpaceWidth = (int) width/3;

                historyArray.add(temp);
                CustomAdapter CA = new CustomAdapter(getApplicationContext(), historyArray);
                list.setAdapter(CA);
                histprog.setVisibility(View.INVISIBLE);
            }
        });
    }

    void findHistory() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("SocialContent")
                .whereEqualTo("Owner", owner)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    code = document.get("GardinerCode").toString();
                                    createHistoryElement(document.get("Owner").toString(), document.get("GardinerCode").toString(), i);
                                    i++;
                                } else {
                                    historyArray.clear();
                                    CustomAdapter CA = new CustomAdapter(getApplicationContext(), historyArray);

                                    ListView list = findViewById(R.id.feedList);

                                    list.setAdapter(CA);
                                    Toast t = new Toast(getApplicationContext());
                                    t.makeText(getApplicationContext(), "No Matching Results!", Toast.LENGTH_LONG).show();
                                    histprog.setVisibility(View.INVISIBLE);
                                }
                            }

                        } else {
                            Log.d("", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    void removeUser(int i) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("SocialContent")
                .whereEqualTo("GardinerCode", code)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                                historyArray.remove(historyArray.get(i));
                                CustomAdapter CA = new CustomAdapter(getApplicationContext(), historyArray);
                                list = findViewById(R.id.historyList);
                                list.setAdapter(CA);
                            }
                        } else {
                            Log.d("", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    void clearHistory() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("SocialContent")
                .whereEqualTo("Owner", owner)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                                historyArray.clear();
                                CustomAdapter CA = new CustomAdapter(getApplicationContext(), historyArray);
                                list = findViewById(R.id.historyList);
                                list.setAdapter(CA);
                            }
                        } else {
                            Log.d("", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    void actOnSource() {
        //Analyzing results
        if (getIntent().getBooleanExtra("Title", false)) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#BAA24E")));
            getSupportActionBar().setTitle("Detected Symbols");
            String[] imgs = (String[]) getIntent().getExtras().get("Image");
            String[] codes = (String[]) getIntent().getExtras().get("Code");
            getIntent().removeExtra("Image");
            getIntent().removeExtra("Code");
            int count = 0;
            for (int i = 0; i < imgs.length; i++) {
                if(codes[i] == null) {
                    temp = new Item();
                    temp.Code = codes[count];
                    temp.FeedImage = imgs[count];
                    historyArray.add(count, temp);
                    count++;
                }
            }

            CustomAdapter CA = new CustomAdapter(getApplicationContext(), historyArray);
            list.setAdapter(CA);
            clear.setVisibility(View.INVISIBLE);
            histprog.setVisibility(View.INVISIBLE);
        }
        //Actual history
        else {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#BAA24E")));
            getSupportActionBar().setTitle("Search History");
            SharedPreferences prefs = getSharedPreferences("DuaKhetyPrefs", 0);
            owner = prefs.getString("Name", "None");
            findHistory();


            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    removeUser(i);
                    return false;
                }
            });


            clear.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    clearHistory();
                    return false;
                }
            });

            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "This will permanently delete your entire history! Press for 2 seconds if you're sure.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    void listOnItemClickListener(int i) {

        if (!getIntent().getBooleanExtra("Title", false)) {
            //Actual history
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(HistoryActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
            } else if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(HistoryActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 500); }
                else {
                Intent intent = new Intent(getApplicationContext(), AnalyzingActivity.class);
                intent.putExtra("OneTime", true);
                intent.putExtra("History", true);

                    String path;
                    path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bmp[i], "Title", null);
                    Log.i("path", path);
                    Uri u = Uri.parse(path);

                    intent.putExtra("Image", u.toString());

                    startActivity(intent);


            }
        } else {
        //Analyzing image
        Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
        historyArray.get(i).FeedImage.replace(" ", "+");
        byte[] decodedString = Base64.decode(historyArray.get(i).FeedImage, 0);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        decodedByte.compress(Bitmap.CompressFormat.PNG, 100, bs);

        intent.putExtra("Image", bs.toByteArray());
        intent.putExtra("Code", historyArray.get(i).Code);
        intent.putExtra("Title", false);
        startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i;

        if (!getIntent().getBooleanExtra("Title", false)) {
            //success
            i = new Intent(getApplicationContext(), SuccessActivity.class);
        }
        else {
            //analyzing
            Boolean onetime = getIntent().getExtras().getBoolean("OneTime");
            if (onetime) {
                i = new Intent(getApplicationContext(), OneTimeActivity.class);
            } else {
                i = new Intent(getApplicationContext(), SuccessActivity.class);
            }
            getIntent().removeExtra("OneTime");

        }
        startActivity(i);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Setup();

        list = findViewById(R.id.historyList);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listOnItemClickListener(i);
            }
        });

        actOnSource();
    }
}
