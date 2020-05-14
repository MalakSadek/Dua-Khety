package malaksadek.duakhety;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class TutorialActivity extends AppCompatActivity {

    List<MenuItem> items;
    int tutorialcount;
    ImageView tutorialImage;
    Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#BAA24E")));
        getSupportActionBar().setTitle("Tutorial");
        tutorialcount = 0;
        tutorialImage = findViewById(R.id.tutorialImage);
        doneButton = findViewById(R.id.donebutton);
        tutorialImage.setImageResource(R.drawable.opening);
        doneButton.setVisibility(View.INVISIBLE);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int source = getIntent().getIntExtra("source", 0);

                if (source == 0) {
                    SharedPreferences sp = getSharedPreferences("DuaKhetyPrefs", 0);
                    sp.edit().putBoolean("Tutorial", false).commit();
                    startActivity(new Intent(getApplicationContext(), OpeningActivity.class));
                } else if (source == 1) {
                    startActivity(new Intent(getApplicationContext(), SuccessActivity.class));
                } else if (source == 2) {
                    startActivity(new Intent(getApplicationContext(), OneTimeActivity.class));
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tutorialmenu, menu);

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
            if (tutorialcount > 0)
                tutorialcount--;
        }

        else if(position == 1) {

            if (tutorialcount < 20)
                tutorialcount++;
        }

        switch(tutorialcount) {
            case 1:
                tutorialImage.setImageResource(R.drawable.opening);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 2:
                tutorialImage.setImageResource(R.drawable.f0s1);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 3:
                tutorialImage.setImageResource(R.drawable.f1t);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 4:
                tutorialImage.setImageResource(R.drawable.f1s1);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 5:
                tutorialImage.setImageResource(R.drawable.f1s2);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 6:
                tutorialImage.setImageResource(R.drawable.f1s3);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 7:
                tutorialImage.setImageResource(R.drawable.f1s4);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 8:
                tutorialImage.setImageResource(R.drawable.f2t);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 9:
                tutorialImage.setImageResource(R.drawable.f2s1);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 10:
                tutorialImage.setImageResource(R.drawable.f2s2);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 11:
                tutorialImage.setImageResource(R.drawable.f3t);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 12:
                tutorialImage.setImageResource(R.drawable.f3s1);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 13:
                tutorialImage.setImageResource(R.drawable.f3s2);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 14:
                tutorialImage.setImageResource(R.drawable.f4t);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 15:
                tutorialImage.setImageResource(R.drawable.f4s1);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 16:
                tutorialImage.setImageResource(R.drawable.f4s2);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 17:
                tutorialImage.setImageResource(R.drawable.f5t);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 18:
                tutorialImage.setImageResource(R.drawable.f5s1);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 19:
                tutorialImage.setImageResource(R.drawable.f5s2);
                doneButton.setVisibility(View.INVISIBLE);
                break;
            case 20:
                tutorialImage.setImageResource(R.drawable.closing);
                doneButton.setVisibility(View.VISIBLE);
                break;
            case 0:
                tutorialImage.setImageResource(R.drawable.opening);
                break;
        }
        return true;
    }
}
