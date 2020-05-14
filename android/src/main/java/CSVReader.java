package malaksadek.duakhety;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CSVReader {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Float[][] main(Context c) {

        String csvFile = "vectors.txt";
        String line = "";
        String cvsSplitBy = ",";
        Float[][] csvInts;

        csvInts = new Float[158][641];

        try (BufferedReader br = new BufferedReader(new InputStreamReader(c.getAssets().open(csvFile)))) {
            String[][] lines = new String[158][641];
            int x = 0;
            while ((line = br.readLine()) != null) {

               lines[x]= line.split(cvsSplitBy);
               x++;
            }

            for (int i = 0; i < 157; i++) {
                for (int j = 0; j < 640; j++) {
                    csvInts[i][j] = Float.valueOf(lines[i][j].replace("\"", ""));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return csvInts;
    }

}
