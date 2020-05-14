package malaksadek.duakhety;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by malaksadek on 6/14/17.
 */

public class CustomAdapter extends ArrayAdapter<Item> {

    public CustomAdapter(Context context, ArrayList<Item> c) {
        super(context, 0, c);

    }


    TextView N, C;
    ImageView I;
    LinearLayout L;
    Item c;
    ViewGroup.LayoutParams params;

    void Setup(View convertView){
        N = convertView.findViewById(R.id.name);
        C = convertView.findViewById(R.id.code);
        I = convertView.findViewById(R.id.feedimage);
        L = convertView.findViewById(R.id.space);
        params = L.getLayoutParams();
    }

    void fillItem (int position) {
        c = getItem(position);
        if(c.history == 0)
            N.setText(c.Name);
        else
            N.setText(String.valueOf(position));
        C.setText(c.Code);
        L.setLayoutParams(params);
        params.width = c.SpaceWidth;
    }

    void handleImage() {

        String imageString = c.FeedImage;

        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput("config.txt", Context.MODE_PRIVATE));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        try {
            outputStreamWriter.write(c.FeedImage);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            outputStreamWriter.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        imageString.replace(" ", "+");
        byte[] decodedString = Base64.decode(imageString, 0);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        I.setImageBitmap(decodedByte);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        Setup(convertView);
        fillItem(position);
        handleImage();

        return convertView;
    }
}
