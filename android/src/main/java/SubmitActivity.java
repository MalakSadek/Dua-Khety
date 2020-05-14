package malaksadek.duakhety;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by malaksadek on 2/15/18.
 */

public class SubmitActivity extends AppCompatActivity {
    Button submit;
    EditText code, description;
    String Code, Description;
    ImageView image;
    boolean connected;
    Uri u;

    void checkInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            connected = true;
        else
            connected = false;
    }

    void Setup() {
        code = findViewById(R.id.code);
        submit = findViewById(R.id.submit);
        image = findViewById(R.id.submitimage);
        description = findViewById(R.id.description);
        u = Uri.parse(getIntent().getExtras().getString("Image"));

        image.setImageURI(u);
    }



    void sendEmail() {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("application/image");
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"malaksadek@aucegypt.edu","mohapard@aucegypt.edu","agha@aucegypt.edu","mohamady996@aucegypt.edu"});
        i.putExtra(Intent.EXTRA_SUBJECT, "New Dua-Khety Submission Photo");


        if (Description.equals(null))
            i.putExtra(Intent.EXTRA_TEXT   , "Code:" + Code);
        else
            i.putExtra(Intent.EXTRA_TEXT   , "Code:" + Code + "\nDescription: " + Description);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File output = File.createTempFile(imageFileName, ".jpg", storageDir);
            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    output);

            i.putExtra(Intent.EXTRA_STREAM  , photoURI.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            startActivityForResult(Intent.createChooser(i, "Send email..."), 0);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getApplicationContext(), "Photo saved to your files, don't forget to attach it in the email!", Toast.LENGTH_LONG).show();

    }

    void submitOnClickListener() {
        Code = code.getText().toString();
        Description = description.getText().toString();

        if (Code.equals("")) {
            Toast.makeText(getApplicationContext(), "You must include a Gardiner's Code", Toast.LENGTH_LONG).show();
        }
        else {
            if (connected) {
                //we are connected to a network
                sendEmail();
            }
            else {
                Toast.makeText(getApplicationContext(), "You must be connected to the internet.", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#BAA24E")));
        getSupportActionBar().setTitle("Photo Submission");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Setup();
        checkInternet();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               submitOnClickListener();
            }
        });
    }
}
