package malaksadek.duakhety;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;

public class CustomAdapterDictionary extends ArrayAdapter<DictionaryItem> {

    TextView C, D, M;
    ImageView I;
   // LinearLayout L;
    DictionaryItem c;
    //ViewGroup.LayoutParams params;

    void Setup(View convertView){
        C = convertView.findViewById(R.id.dictionarycode);
        D = convertView.findViewById(R.id.dictionarydescription);
        M = convertView.findViewById(R.id.dictionarymeaning);
        I = convertView.findViewById(R.id.dictionaryimage);
       // L = convertView.findViewById(R.id.dictionaryspacce);
       // params = L.getLayoutParams();
    }

    void fillItem (int position) {
        c = getItem(position);

        C.setText(c.GCode);
        D.setText(c.description);
        M.setText(c.meaning);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap bmp = BitmapFactory.decodeByteArray(c.picture, 0, c.picture.length, options);
        I.setImageBitmap(bmp);
      //  L.setLayoutParams(params);
      //  params.width = c.SpaceWidth;
    }

    public CustomAdapterDictionary(Context context, ArrayList<DictionaryItem> c) {
        super(context, 0, c);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dictionaryitem, parent, false);
        }

        Setup(convertView);
        fillItem(position);

        return convertView;
    }

}
