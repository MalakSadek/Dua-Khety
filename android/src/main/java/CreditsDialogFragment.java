package malaksadek.duakhety;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;


public class CreditsDialogFragment extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Application Credits:");
        alertDialogBuilder.setMessage("A special thanks to http://www.egyptianhieroglyphs.net/ for providing the Gardiner code's of hieroglyphics.\nAnd to Morris Franken & Jan Van Gemret for providing a dataset that made this classification possible.\n");
        //null should be your on click listener
        alertDialogBuilder.setNegativeButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }

}
