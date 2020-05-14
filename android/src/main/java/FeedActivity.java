package malaksadek.duakhety;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import java.util.Objects;

/**
 * Created by malaksadek on 2/15/18.
 */

public class FeedActivity  extends AppCompatActivity {
    Item temp;
    Button search;
    CheckBox name, code;
    EditText text;
    ArrayList<Item> feedArray;
    ListView feedList;
    TextView searchcat;
    ProgressBar feedprog;
    Bitmap[] bmp;

    void Setup(){
        search = findViewById(R.id.searchBtn);
        name = findViewById(R.id.nameRadio);
        code = findViewById(R.id.codeRadio);
        text = findViewById(R.id.searchBox);
        feedList = findViewById(R.id.feedList);
        searchcat = findViewById(R.id.searchcat);
        feedprog = findViewById(R.id.progress);
        feedprog.setVisibility(View.INVISIBLE);
        bmp = new Bitmap[100];
        feedArray = new ArrayList<>(100);
    }

    void createFeedElement(final String name, final String code, final int i) {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child("UserPictures/"+name+code+".bin");

            storageRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Log.i("trying","Not getting image");
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inMutable = true;
                    bmp[i] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                    temp = new Item();
                    temp.Name = name;
                    temp.Code = code;
                    temp.history = 0;
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
                    feedArray.add(temp);
                    CustomAdapter CA = new CustomAdapter(getApplicationContext(), feedArray);
                    ListView list = findViewById(R.id.feedList);

                    list.setAdapter(CA);
                    feedprog.setVisibility(View.INVISIBLE);
                }
            });
    }


    void searchOnClickListener(){
        feedprog.setVisibility(View.VISIBLE);
        if(!name.isChecked() && !code.isChecked()) {
            Toast.makeText(getApplicationContext(), "Please select a search category.", Toast.LENGTH_LONG).show();
            feedprog.setVisibility(View.INVISIBLE);
        }
        else
        if(name.isChecked() && code.isChecked()) {
            Toast.makeText(getApplicationContext(), "Please select only one search category.", Toast.LENGTH_LONG).show();
            feedprog.setVisibility(View.INVISIBLE);
        } else
        if (text.getText().toString().equals("")) {
            showFeed("");
        }
        else
        if(!name.isChecked() && code.isChecked()) {
            searchcat.setText("CODE");
            showFeed(text.getText().toString());
        }
        else
        if(name.isChecked() && !code.isChecked()) {
            searchcat.setText("USERNAME");
            showFeed(text.getText().toString());
        }
    }

    void feedListOnClickListener(int i) {
        Intent intent = new Intent(getApplicationContext(), AnalyzingActivity.class);
        intent.putExtra("OneTime", true);
        intent.putExtra("History", true);

        if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
            String path;
                path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bmp[i], "Title", null);
                Log.i("path", path);
                Uri u = Uri.parse(path);

                intent.putExtra("Image", u.toString());

                startActivity(intent);


        }

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#BAA24E")));
        getSupportActionBar().setTitle("Social Feed");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Setup();
        feedprog.setVisibility(View.VISIBLE);
        feedArray = new ArrayList<>();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               searchOnClickListener();
            }
        });
        feedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                feedListOnClickListener(i);
            }
        });

        showFeed("");
    }

    public void showFeed(String filter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        feedArray.clear();
        CustomAdapter CA = new CustomAdapter(getApplicationContext(), feedArray);

        ListView list = findViewById(R.id.feedList);

        list.setAdapter(CA);

        if (Objects.equals(filter, "")) {
            db.collection("SocialContent")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int i = 0;
                                for (DocumentSnapshot document : task.getResult()) {
                                    if (document.exists()) {
                                        createFeedElement(document.get("Owner").toString(), document.get("GardinerCode").toString(), i);
                                        i++;
                                    } else {
                                        feedArray.clear();
                                        CustomAdapter CA = new CustomAdapter(getApplicationContext(), feedArray);

                                        ListView list = findViewById(R.id.feedList);

                                        list.setAdapter(CA);
                                        Toast t = new Toast(getApplicationContext());
                                        t.makeText(getApplicationContext(), "No Matching Results!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            } else {
                                Log.d("", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        } else {
            if (searchcat.getText() == "USERNAME") {
                db.collection("SocialContent")
                        .whereEqualTo("Owner", filter)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    int i = 0;
                                    for (DocumentSnapshot document : task.getResult()) {
                                        if (document.exists()) {
                                            createFeedElement(document.get("Owner").toString(), document.get("GardinerCode").toString(), i);
                                            i++;
                                        } else {
                                            feedArray.clear();
                                            CustomAdapter CA = new CustomAdapter(getApplicationContext(), feedArray);

                                            ListView list = findViewById(R.id.feedList);

                                            list.setAdapter(CA);
                                            Toast t = new Toast(getApplicationContext());
                                            t.makeText(getApplicationContext(), "No Matching Results!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } else {
                                    Log.d("", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            } else {
                db.collection("SocialContent")
                        .whereEqualTo("GardinerCode", filter)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    int i = 0;
                                    for (DocumentSnapshot document : task.getResult()) {
                                        if (document.exists()) {
                                            createFeedElement(document.get("Owner").toString(), document.get("GardinerCode").toString(), i);
                                            i++;
                                        } else {
                                            feedArray.clear();
                                            CustomAdapter CA = new CustomAdapter(getApplicationContext(), feedArray);

                                            ListView list = findViewById(R.id.feedList);

                                            list.setAdapter(CA);
                                            Toast t = new Toast(getApplicationContext());
                                            t.makeText(getApplicationContext(), "No Matching Results!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } else {
                                    Log.d("", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        }
    }
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { android.Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(getApplicationContext(), "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
}
