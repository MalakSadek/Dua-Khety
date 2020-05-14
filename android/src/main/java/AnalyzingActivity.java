package malaksadek.duakhety;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loopj.android.http.RequestParams;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.Object;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.Canny;
import static org.opencv.imgproc.Imgproc.HoughLines;
import static org.opencv.imgproc.Imgproc.blur;
import static org.opencv.imgproc.Imgproc.connectedComponentsWithStats;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.erode;
import static org.opencv.imgproc.Imgproc.rectangle;
import static org.opencv.imgproc.Imgproc.resize;
import org.opencv.core.*;
import org.opencv.core.Point;
//import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.core.Scalar;

/**
 * Created by malaksadek on 2/15/18.
 */

public class AnalyzingActivity extends AppCompatActivity {

    private static final String MODEL_FILE = "file:///android_asset/mymodeltrained.h5.pb";
    private static final String LABEL_FILE = "file:///android_asset/output.txt";
    private static final int INPUT_SIZEX = 50;
    private static final int INPUT_SIZEY = 75;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "separable_conv2d_5_input";
    private static final String OUTPUT_NAME = "output_node0";
    private Classifier classifier;
    private ArrayList<Mat> imgs, imgsBW;
    private Executor executor = Executors.newSingleThreadExecutor();
    HashMap<Integer,Double> M;
    CropImageView cimage;
    ImageView tck;
    Button crop;
    Bitmap bmp, bitmap, totalImage;
    float[] classifierresult;
    ProgressBar prgrs;
    ImageView image;
    String name, email;
    RequestParams params;
    String[] encodedString, classifierResult;
    Boolean onetime;
    Float[][] csvInts;
    int[] classifierResultt;
    Mat img_C;
    Mat edge_detected;
    Mat img_BW;
    Mat blurred;
    Mat labels;
    Mat stats;
    Mat centres;
    Mat clean;

    String uploadimage;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    imgs = new ArrayList<>();
                    imgsBW = new ArrayList<>();
                    encodedString = new String[100];
                    classifierResult = new String[100];
                    img_C = new Mat();
                    edge_detected = new Mat();
                    img_BW = new Mat();
                    blurred = new Mat();
                    labels = new Mat();
                    stats = new Mat();
                    centres =  new Mat();
                    clean = new Mat();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

            //If the page is called from the history or feed activities and thus an image is already selected
            if(getIntent().getBooleanExtra("History", true)) {

                Setup();
                prepareImage();
                Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                intent.putExtra("Image", encodedString);
                intent.putExtra("Code", classifierResult);
                intent.putExtra("Title", true);
                startActivity(intent);
                finish();
            } else {
                //If the user wants to analyze a new image
                Setup();
                final ImageSourceDialogFragment idf = new ImageSourceDialogFragment(AnalyzingActivity.this);
                idf.show();
            }
        }
    }


    Bitmap segment(Bitmap bitmap)
    {
        Bitmap bmp32 = bitmap.copy(Bitmap.Config.RGB_565, true);
        Utils.bitmapToMat(bmp32, img_C);
        Utils.bitmapToMat(bmp32, clean);

        Imgproc.cvtColor(img_C, img_BW, Imgproc.COLOR_BGR2GRAY);

        Imgproc.blur(img_BW, blurred, new Size(3, 3));


        Scalar temp = Core.mean(blurred);
        Double avg = temp.val[0];

        Imgproc.threshold(img_BW, img_BW,  avg.intValue(), 255, Imgproc.THRESH_BINARY_INV);
        Imgproc.Canny(blurred, edge_detected, avg*0.66 ,avg*1.33, 3, false);

        // Extract components

        int no_of_Labels = Imgproc.connectedComponentsWithStats(edge_detected, labels, stats, centres, 8, CvType.CV_32S);
        int count = 0;
        for (int i = 0; i < no_of_Labels; i++)
        {
            int h = (int) stats.get(i, Imgproc.CC_STAT_HEIGHT)[0];
            int w = (int) stats.get(i, Imgproc.CC_STAT_WIDTH)[0];
            int l = (int) stats.get(i, Imgproc.CC_STAT_LEFT)[0];
            int t = (int) stats.get(i, Imgproc.CC_STAT_TOP)[0];

            if (stats.get(i, Imgproc.CC_STAT_AREA)[0] > 2000 ||
                    stats.get(i, Imgproc.CC_STAT_AREA)[0] < 100 ||
                    h > img_C.rows() / 2 || w > 200 || w<8 ||
                    stats.get(i, Imgproc.CC_STAT_LEFT)[0] <8)
                continue;

            int x = (int) centres.get(i, 0)[0], y = (int) centres.get(i, 1)[0];


            Imgproc.rectangle(img_C, new Point(x - w / 2, y - h / 2), new Point(x + w / 2, (y + h / 2) + 10), new Scalar(0, 0, 255), 1);
            count++;
            Log.i("Point no, ", i +"("+
                    stats.get(i, Imgproc.CC_STAT_TOP)[0]+
                    ","+stats.get(i, Imgproc.CC_STAT_LEFT)[0]+
                    ")"+ ": height is "+ h +
                    ", width is " + w + ", area is " +
                    stats.get(i, Imgproc.CC_STAT_AREA)[0]);

            //Normal images (for user)
            Rect roi = new Rect(Math.max(l-5, 0), Math.max(t-5, 0), Math.min(w+5, clean.cols()), Math.min(h+5, clean.rows()));
            Mat cropped = new Mat(clean, roi);
            imgs.add(count-1, cropped);

            //Threshold images (for classifier)
            Mat cropped2 = new Mat(clean, roi);
            Imgproc.cvtColor(cropped2, cropped2, Imgproc.COLOR_BGR2GRAY);
            Imgproc.blur(cropped2, cropped2, new Size(3, 3));

            Scalar temp2 = Core.mean(cropped2);
            Double avg2 = temp2.val[0];

            Imgproc.threshold(cropped2, cropped2,  avg2.intValue(), 255, Imgproc.THRESH_BINARY_INV);
            imgsBW.add(count-1, cropped2);
        }

        //Show user image with bounding boxes
        Bitmap.Config conf2 = Bitmap.Config.RGB_565; // see other conf types
        Bitmap bmp2 = Bitmap.createBitmap(img_C.width(), img_C.height(), conf2);
        Utils.matToBitmap(img_C, bmp2);
        image.setImageBitmap(bmp2);

        //Upload image without bounding boxes to database
        Bitmap bmp3 = Bitmap.createBitmap(clean.width(), clean.height(), conf2);
        bmp3 = Bitmap.createScaledBitmap(bmp3, clean.width(), clean.height(), false);
        Utils.matToBitmap(clean, bmp3);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp3.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageInByte = baos.toByteArray();
        uploadimage= Base64.encodeToString(imageInByte, Base64.DEFAULT);
        uploadimage = uploadimage.replaceAll(" ","");
        uploadimage = uploadimage.replaceAll("\n","");

        return bmp2;
    }

    void crop(int i) {
        crop = findViewById(R.id.crop);
        cimage.setImageBitmap(bmp);
        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cimage.getCroppedImageAsync();
            }
        });

        cimage.setGuidelines(CropImageView.Guidelines.ON_TOUCH);
        cimage.setCropShape(CropImageView.CropShape.RECTANGLE);
        cimage.setScaleType(CropImageView.ScaleType.FIT_CENTER);
        cimage.setAutoZoomEnabled(true);
        cimage.setShowProgressBar(true);

        cimage.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                if ((!onetime)&&(!getIntent().getBooleanExtra("History", false))) {
                    uploadData();
                }
                else {

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                            intent.putExtra("Image", encodedString);
                            intent.putExtra("Code", classifierResult);
                            intent.putExtra("Title", true);
                            startActivity(intent);
                            finish();
                        }
                    }, 5000);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == 999){
            //Camera
            if(resultCode == RESULT_OK) {
                SharedPreferences pathpref = getSharedPreferences("DuaKhetyPrefs", 0);
                String path = pathpref.getString("path", "");
                Uri uri = Uri.parse(path);
                try {
                    bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                crop(0);
            }
        }
        else {
            //Gallery
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                try {
                    bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                    crop(1);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i;
        if(!onetime)
            i = new Intent(AnalyzingActivity.this, SuccessActivity.class);
        else
            i = new Intent(AnalyzingActivity.this, OneTimeActivity.class);
        startActivity(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void Setup() {

        prgrs = findViewById(R.id.prog);
        image = findViewById(R.id.img);
        tck = findViewById(R.id.tck);
        cimage = findViewById(R.id.cropImageView);
        crop = findViewById(R.id.crop);

        prgrs.setVisibility(View.INVISIBLE);
        tck.setVisibility(View.INVISIBLE);
        image.setVisibility(View.INVISIBLE);

        imgs = new ArrayList<>();
        imgsBW = new ArrayList<>();
        encodedString = new String[100];
        classifierResult = new String[100];
        Log.i("OpenCV", "OpenCV loaded successfully");

        onetime = getIntent().getExtras().getBoolean("OneTime");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
                            INPUT_SIZEX,
                            INPUT_SIZEY,
                            IMAGE_MEAN,
                            IMAGE_STD,
                            INPUT_NAME,
                            OUTPUT_NAME);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });

        csvInts = new Float[157][640];

        csvInts = CSVReader.main(getApplicationContext());
    }

    void prepareImage() {

        if (getIntent().getBooleanExtra("History", false)) {
            bitmap = null;
            Uri imgURI = Uri.parse(getIntent().getStringExtra("Image"));
            getIntent().removeExtra("History");
            getIntent().removeExtra("Image");
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgURI);
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            bitmap = cimage.getCroppedImage();
        }
        totalImage = bitmap;
        prgrs.setVisibility(View.VISIBLE);
        tck.setVisibility(View.VISIBLE);
        cimage.setVisibility(View.INVISIBLE);
        image.setVisibility(View.VISIBLE);
        crop.setVisibility(View.INVISIBLE);
        getSupportActionBar().setTitle("Analyzing...");

        while (classifier == null) {
            Log.i("classifier still null", ".");
        }

        String labelFilename = "file:///android_asset/output.txt";
        String actualFilename = labelFilename.split("file:///android_asset/")[1];
        Vector<String> labels = new Vector<String>();
        Log.i(".", "Reading labels from: " + actualFilename);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getAssets().open(actualFilename)));
            String line;
            while ((line = br.readLine()) != null) {
                labels.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 360, 360, false);
        segment(resizedBitmap);

        int index[] = new int[5];
        classifierResultt = new int[imgsBW.size()];
        Float dist;
        Double sum = 0.0;
        Double min = 10000.0;
        int z = 0;

        for (int i = 0; i < imgsBW.size(); i++) {

            bitmap = Bitmap.createScaledBitmap(bitmap, imgsBW.get(i).width(), imgsBW.get(i).height(), false);
            Utils.matToBitmap(imgsBW.get(i), bitmap);
            bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZEX, INPUT_SIZEY, false);

            classifierresult = classifier.recognizeImage(bitmap);


                Double[] distances = new Double[157];
                M = new HashMap<Integer, Double>();
                for (int y = 0; y < labels.size() - 1; y++) {
                    for (int x = 0; x < classifierresult.length - 1; x++) {
                        dist = Math.abs(classifierresult[x] - csvInts[y][x]);
                        sum = sum + dist;
                    }
                    distances[y] = sum;
                    if (sum < min) {
                        min = sum;
                        if (z < 5) {
                            index[z] = y;
                            z++;
                        }
                    }

                    sum = 0.0;

                }

                classifierResult[i] = "";

                for (int k = 0; k < 5; k++) {
                    if(k == 4) {
                        classifierResult[i] += labels.elementAt(index[k]);
                    } else {
                        classifierResult[i] += labels.elementAt(index[k])+",";
                    }
                }


                bitmap = Bitmap.createScaledBitmap(bitmap, imgs.get(i).width(), imgs.get(i).height(), false);
                Utils.matToBitmap(imgs.get(i), bitmap);
                bitmap = Bitmap.createScaledBitmap(bitmap, imgs.get(i).width() / 2, imgs.get(i).height() / 2, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageInByte = baos.toByteArray();
                encodedString[i] = Base64.encodeToString(imageInByte, Base64.DEFAULT);
                encodedString[i] = encodedString[i].replaceAll(" ", "");
                encodedString[i] = encodedString[i].replaceAll("\n", "");

            }

        }


    public LinkedHashMap<Integer, Double> sortHashMapByValues(
            HashMap<Integer, Double> passedMap) {
        List<Integer> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Double> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<Integer, Double> sortedMap =
                new LinkedHashMap<>();

        Iterator<Double> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Double val = valueIt.next();
            Iterator<Integer> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Integer key = keyIt.next();
                Double comp1 = passedMap.get(key);
                Double comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 5) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    void uploadData() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        String code = getSaltString();
        name = getSharedPreferences("DuaKhetyPrefs", 0).getString("Name", "None");
        data.put("Owner", name);
        data.put("GardinerCode", code);
        data.put("Description", "");
        data.put("Photo", "/UserPictures/"+name+code);

        db.collection("SocialContent").document(getSaltString()).set(data);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("UserPictures/"+name+code+".bin");

        prepareImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        totalImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(imageBytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                intent.putExtra("Image", encodedString);
                intent.putExtra("Code", classifierResult);
                intent.putExtra("Title", true);
                startActivity(intent);
                finish();
            }
        });

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyzing);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#BAA24E")));
        getSupportActionBar().setTitle("Edit Image");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //Creates action button in the toolbar to go to profile activity

}
