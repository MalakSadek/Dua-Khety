package malaksadek.duakhety;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by malaksadek on 2/15/18.
 */

public class SuccessActivity extends AppCompatActivity {

    Button photo, submit, feed, history, search;
    ImageView image;
    Uri resultUri;
    boolean connected;
    List<MenuItem> items;

    void Setup() {
        photo = findViewById(R.id.photo);
        submit = findViewById(R.id.submit);
        feed = findViewById(R.id.feed);
        history = findViewById(R.id.history);
        search = findViewById(R.id.search);
    }


    void photoOnClickListener(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(SuccessActivity.this, new String[] {android.Manifest.permission.CAMERA}, 1000);
        }
        else {
            Intent i = new Intent(getApplicationContext(), AnalyzingActivity.class);
            i.putExtra("OneTime", false);
            i.putExtra("History", false);
            startActivity(i);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 2) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                if (resultCode != 0) {
                    Uri resultUri = result.getUri();
                    Intent intent = new Intent(getBaseContext(), SubmitActivity.class);
                    intent.putExtra("Image", resultUri.toString());
                    startActivity(intent);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    void checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            connected = true;
        else
            connected = false;
    }


    void historyOnClickListener() {
        if(connected) {
            //we are connected to a network
            Intent i = new Intent(getApplicationContext(), HistoryActivity.class);
            i.putExtra("Title", false);
            startActivity(i);
        }
        else {
            Toast.makeText(getApplicationContext(), "You must be connected to the internet.", Toast.LENGTH_LONG).show();
        }
    }

    void feedOnClickListener() {
        if(connected) {
            //we are connected to a network
            startActivity(new Intent(getApplicationContext(), FeedActivity.class));
        }
        else {
            Toast.makeText(getApplicationContext(), "You must be connected to the internet.", Toast.LENGTH_LONG).show();
        }
    }

    void submitOnClickListener() {
        if(connected) {
            //we are connected to a network
            Intent intent = CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                    .getIntent(getApplicationContext());
            startActivityForResult(intent, 2);
        }
        else {
            Toast.makeText(getApplicationContext(), "You must be connected to the internet.", Toast.LENGTH_LONG).show();
        }
    }

    void searchOnClickListener(){
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
        setContentView(R.layout.activity_success);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#BAA24E")));
        getSupportActionBar().setTitle("Dua-Khety");


        Setup();
        checkInternet();

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             photoOnClickListener();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitOnClickListener();
            }
        });

        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedOnClickListener();
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historyOnClickListener();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchOnClickListener();
            }
        });

    }

    void startActivity(int i) {
        Intent intent;
        if (i == 1) {
            intent = new Intent(getBaseContext(), AnalyzingActivity.class);
        }
        else {
            intent = new Intent(getBaseContext(), SubmitActivity.class);
        }


        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("","Permission is granted");
                Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Dua-Khety Image" , "");
                intent.putExtra("Image", resultUri.toString());
                intent.putExtra("OneTime", false);
                startActivity(intent);
            } else {

                Log.v("","Permission is revoked");
                intent.putExtra("Image", resultUri.toString());
                intent.putExtra("OneTime", false);
                startActivity(intent);
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("","Permission is granted");
            Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Dua-Khety Image" , "");
            intent.putExtra("Image", resultUri.toString());
            intent.putExtra("OneTime", false);
            startActivity(intent);
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fullmenu, menu);

        items = new ArrayList<>();


        for(int i=0; i<menu.size(); i++){
            items.add(menu.getItem(i));
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int position=items.indexOf(item);

        if(position == 1) {

            CreditsDialogFragment c = new CreditsDialogFragment();
            c.show(getFragmentManager(), "My Dialog");
        }
        else if (position == 0) {
            DevelopersDialogFragment d = new DevelopersDialogFragment();
            d.show(getFragmentManager(), "My Dialog");
        }
        else if (position == 2) {
            Intent i = new Intent(getApplicationContext(), TutorialActivity.class);
            i.putExtra("source", 1);
            startActivity(i);
        }
        else if (position == 3) {

            if(connected) {
                //we are connected to a network
                LogoutDialogFragment L = new LogoutDialogFragment();
                L.show(getFragmentManager(), "My Dialog");
            }
            else {
                Toast.makeText(getApplicationContext(), "You must be connected to the internet.", Toast.LENGTH_LONG).show();
            }
        }
        return true;
    }
}
