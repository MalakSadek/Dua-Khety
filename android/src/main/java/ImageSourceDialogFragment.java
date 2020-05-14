package malaksadek.duakhety;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by malaksadek on 4/15/18.
 */

public class ImageSourceDialogFragment extends Dialog implements android.view.View.OnClickListener{

    public Activity c;
    Button camera, gallery;

    public ImageSourceDialogFragment(Activity a) {
        super(a);
        this.c = a;
        requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        setContentView(R.layout.dialog_imagesource);
        camera = findViewById(R.id.camera);
        gallery = findViewById(R.id.gallery);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        camera.setOnClickListener(this);
        gallery.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.camera:
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(c, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
                }
                else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DCIM), "Camera");
                    try {
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "JPEG_" + timeStamp + "_";
                        File output = File.createTempFile(imageFileName, ".jpg", storageDir);
                        Uri photoURI = FileProvider.getUriForFile(c,
                                BuildConfig.APPLICATION_ID + ".provider",
                                output);

                        SharedPreferences path = c.getSharedPreferences("DuaKhetyPrefs", 0);
                        final SharedPreferences.Editor editor = path.edit();
                        editor.putString("path", photoURI.toString());
                        editor.commit();
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                        c.startActivityForResult(intent, 999);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case R.id.gallery:
                Intent intent2 = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                c.startActivityForResult(intent2, 222);
                break;
        }
        dismiss();
    }
}
